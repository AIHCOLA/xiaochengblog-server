package com.xiaochengblog.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaochengblog.dto.*;
import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.*;
import com.xiaochengblog.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;
    private final PostTagMapper postTagMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Read helpers ──

    private void populatePost(Post post) {
        if (post == null) return;
        if (post.getCategoryId() != null) {
            post.setCategory(categoryMapper.selectById(post.getCategoryId()));
        }
        if (post.getAuthorId() != null) {
            post.setAuthor(userMapper.selectById(post.getAuthorId()));
        }
        post.setTags(new HashSet<>(postTagMapper.selectTagsByPostId(post.getId())));
    }

    private void populatePosts(List<Post> posts) {
        if (posts.isEmpty()) return;

        Set<Long> categoryIds = new HashSet<>();
        Set<Long> authorIds = new HashSet<>();
        for (Post p : posts) {
            if (p.getCategoryId() != null) categoryIds.add(p.getCategoryId());
            if (p.getAuthorId() != null) authorIds.add(p.getAuthorId());
        }

        Map<Long, Category> catMap = new HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<Category> cats = categoryMapper.selectBatchIds(categoryIds);
            for (Category c : cats) catMap.put(c.getId(), c);
        }

        Map<Long, User> userMap = new HashMap<>();
        if (!authorIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(authorIds);
            for (User u : users) userMap.put(u.getId(), u);
        }

        for (Post p : posts) {
            p.setCategory(catMap.get(p.getCategoryId()));
            p.setAuthor(userMap.get(p.getAuthorId()));
            p.setTags(new HashSet<>(postTagMapper.selectTagsByPostId(p.getId())));
        }
    }

    // ── Public API ──

    @Cacheable(value = "posts", key = "'all_page_' + #page + '_' + #size")
    public PaginatedResponse<PostDTO> getAllPosts(int page, int size) {
        Page<Post> pageObj = new Page<>(page, size);
        Page<Post> result = postMapper.selectPage(pageObj, new QueryWrapper<Post>().orderByDesc("created_at"));
        populatePosts(result.getRecords());
        List<PostDTO> dtos = result.getRecords().stream().map(this::toPostDTO).collect(Collectors.toList());
        return PaginatedResponse.of(dtos, result.getTotal(), page, size);
    }

    @Cacheable(value = "posts", key = "'featured'")
    public List<PostDTO> getFeaturedPosts() {
        List<Post> posts = postMapper.selectFeatured();
        populatePosts(posts);
        return posts.stream().map(this::toPostDTO).collect(Collectors.toList());
    }

    public PostDTO getPostBySlug(String slug) {
        Post post = postMapper.selectBySlug(slug);
        if (post == null) throw new BusinessException(404, "文章不存在");
        populatePost(post);
        return toPostDTO(post);
    }

    public List<PostDTO> getPostsByCategory(String categorySlug) {
        List<Post> posts = postMapper.selectByCategorySlug(categorySlug);
        populatePosts(posts);
        return posts.stream().map(this::toPostDTO).collect(Collectors.toList());
    }

    public List<PostDTO> getPostsByTag(String tagSlug) {
        List<Post> posts = postMapper.selectByTagSlug(tagSlug);
        populatePosts(posts);
        return posts.stream().map(this::toPostDTO).collect(Collectors.toList());
    }

    public PaginatedResponse<PostDTO> searchPosts(String query, int page, int size) {
        if (query == null || query.isBlank()) return getAllPosts(page, size);
        long total = postMapper.countSearch(query);
        List<Post> posts = postMapper.searchPaged(query, (page - 1) * size, size);
        populatePosts(posts);
        List<PostDTO> dtos = posts.stream().map(this::toPostDTO).collect(Collectors.toList());
        return PaginatedResponse.of(dtos, total, page, size);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public PostDTO createPost(PostRequest request, Long authorId) {
        if (postMapper.existsBySlug(request.getSlug())) {
            throw new BusinessException(400, "该链接已被使用");
        }

        QueryWrapper<Category> catQw = new QueryWrapper<>();
        catQw.eq("slug", request.getCategorySlug());
        Category category = categoryMapper.selectOne(catQw);
        if (category == null) throw new BusinessException(400, "分类不存在");

        Set<Tag> tagSet = new HashSet<>();
        if (request.getTagSlugs() != null) {
            for (String tagSlug : request.getTagSlugs()) {
                QueryWrapper<Tag> tagQw = new QueryWrapper<>();
                tagQw.eq("slug", tagSlug);
                Tag tag = tagMapper.selectOne(tagQw);
                if (tag != null) tagSet.add(tag);
            }
        }

        int readingTime = calculateReadingTime(request.getContent());

        Post post = Post.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .coverImage(request.getCoverImage())
                .categoryId(category.getId())
                .authorId(authorId)
                .featured(request.isFeatured())
                .readingTime(readingTime)
                .likes(0)
                .views(0)
                .build();

        postMapper.insert(post);

        for (Tag tag : tagSet) {
            postTagMapper.insert(post.getId(), tag.getId());
        }

        post.setCategory(category);
        post.setAuthor(userMapper.selectById(authorId));
        post.setTags(tagSet);

        return toPostDTO(post);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public PostDTO updatePost(String slug, PostRequest request, Long authorId) {
        Post post = postMapper.selectBySlug(slug);
        if (post == null) throw new BusinessException(404, "文章不存在");
        if (!post.getAuthorId().equals(authorId)) {
            throw new BusinessException(403, "只能编辑自己的文章");
        }

        if (!slug.equals(request.getSlug()) && postMapper.existsBySlug(request.getSlug())) {
            throw new BusinessException(400, "该链接已被使用");
        }

        QueryWrapper<Category> catQw = new QueryWrapper<>();
        catQw.eq("slug", request.getCategorySlug());
        Category category = categoryMapper.selectOne(catQw);
        if (category == null) throw new BusinessException(400, "分类不存在");

        Set<Tag> tagSet = new HashSet<>();
        if (request.getTagSlugs() != null) {
            for (String tagSlug : request.getTagSlugs()) {
                QueryWrapper<Tag> tagQw = new QueryWrapper<>();
                tagQw.eq("slug", tagSlug);
                Tag tag = tagMapper.selectOne(tagQw);
                if (tag != null) tagSet.add(tag);
            }
        }

        post.setTitle(request.getTitle());
        post.setSlug(request.getSlug());
        post.setExcerpt(request.getExcerpt());
        post.setContent(request.getContent());
        post.setCoverImage(request.getCoverImage());
        post.setCategoryId(category.getId());
        post.setFeatured(request.isFeatured());
        post.setReadingTime(calculateReadingTime(request.getContent()));

        postMapper.updateById(post);

        postTagMapper.deleteByPostId(post.getId());
        for (Tag tag : tagSet) {
            postTagMapper.insert(post.getId(), tag.getId());
        }

        post.setCategory(category);
        post.setAuthor(userMapper.selectById(authorId));
        post.setTags(tagSet);

        return toPostDTO(post);
    }

    @Transactional
    @CacheEvict(value = "posts", allEntries = true)
    public void deletePost(String slug, Long authorId) {
        Post post = postMapper.selectBySlug(slug);
        if (post == null) throw new BusinessException(404, "文章不存在");
        if (!post.getAuthorId().equals(authorId)) {
            throw new BusinessException(403, "只能删除自己的文章");
        }
        postTagMapper.deleteByPostId(post.getId());
        postMapper.deleteById(post.getId());
    }

    // ── Helpers ──

    private int calculateReadingTime(String content) {
        int chineseChars = 0;
        for (char c : content.toCharArray()) {
            if (c >= 0x4e00 && c <= 0x9fff) chineseChars++;
        }
        int words = content.split("\\s+").length;
        return Math.max(1, (int) Math.ceil((chineseChars + words) / 200.0));
    }

    PostDTO toPostDTO(Post post) {
        CategoryDTO catDTO = null;
        if (post.getCategory() != null) {
            Category c = post.getCategory();
            catDTO = new CategoryDTO(c.getId(), c.getName(), c.getSlug(), c.getDescription(), c.getColor());
        }

        List<TagDTO> tagDTOs = new ArrayList<>();
        if (post.getTags() != null) {
            for (Tag t : post.getTags()) {
                tagDTOs.add(new TagDTO(t.getId(), t.getName(), t.getSlug()));
            }
        }

        AuthorDTO authorDTO = null;
        if (post.getAuthor() != null) {
            User u = post.getAuthor();
            Map<String, String> links = new LinkedHashMap<>();
            if (u.getEmail() != null && !u.getEmail().isBlank()) {
                links.put("email", "mailto:" + u.getEmail());
            }
            String socialLinksJson = u.getSocialLinks();
            if (socialLinksJson != null && !socialLinksJson.isBlank()) {
                try {
                    Map<String, String> socialLinks = objectMapper.readValue(socialLinksJson,
                            new TypeReference<Map<String, String>>() {});
                    links.putAll(socialLinks);
                } catch (Exception ignored) {}
            }
            authorDTO = new AuthorDTO(
                    u.getUsername(),
                    u.getAvatar() != null ? u.getAvatar() : "",
                    u.getBio() != null ? u.getBio() : "",
                    links
            );
        }

        return new PostDTO(
                post.getId(), post.getTitle(), post.getSlug(),
                post.getExcerpt(), post.getContent(), post.getCoverImage(),
                post.isFeatured(), post.getLikes(), post.getViews(),
                post.getReadingTime(), post.getCreatedAt(), post.getUpdatedAt(),
                catDTO, tagDTOs, authorDTO
        );
    }
}
