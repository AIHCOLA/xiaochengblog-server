package com.xiaochengblog.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaochengblog.mapper.*;
import com.xiaochengblog.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PostMapper postMapper;
    private final PostTagMapper postTagMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userMapper.selectCount(null) > 0) {
            log.info("Database already seeded, skipping...");
            return;
        }

        log.info("Seeding database...");

        // Create demo user
        User demoUser = User.builder()
                .username("Xiao Cheng")
                .email("demo@xiaocheng.dev")
                .password(passwordEncoder.encode("demo123"))
                .role("ADMIN")
                .bio("全栈工程师，专注于 Web 技术与分布式系统。")
                .build();
        userMapper.insert(demoUser);

        // Create categories
        Category frontend = insertCategory("前端开发", "frontend", "React, Vue, CSS, TypeScript 等前端技术", "#6c5ce7");
        Category backend = insertCategory("后端架构", "backend", "Node.js, Python, 数据库, API 设计等", "#00d4aa");
        Category ai = insertCategory("AI 与机器学习", "ai-ml", "深度学习, LLM, 模型部署等", "#e17055");
        Category sysDesign = insertCategory("系统设计", "system-design", "分布式系统, 微服务, 架构模式等", "#0984e3");
        Category devtools = insertCategory("开发工具", "devtools", "VSCode, Git, Docker, CI/CD 等", "#fdcb6e");
        Category essays = insertCategory("思考随笔", "essays", "技术思考, 职业成长, 行业观察", "#a29bfe");

        // Create tags
        Map<String, Tag> tagMap = new HashMap<>();
        for (String[] t : new String[][]{
                {"React", "react"}, {"TypeScript", "typescript"}, {"Node.js", "nodejs"},
                {"Python", "python"}, {"CSS", "css"}, {"Docker", "docker"},
                {"GraphQL", "graphql"}, {"Rust", "rust"}, {"WebAssembly", "wasm"},
                {"LLM", "llm"}, {"Git", "git"}, {"性能优化", "performance"},
                {"安全", "security"}, {"设计模式", "design-patterns"}, {"数据库", "database"},
                {"API 设计", "api-design"}
        }) {
            Tag tag = Tag.builder().name(t[0]).slug(t[1]).build();
            tagMapper.insert(tag);
            tagMap.put(tag.getSlug(), tag);
        }

        // Create posts
        createPost("使用 React 18 构建高性能前端应用 — 并发特性深度解析",
                "react-18-concurrent-features",
                "React 18 引入了并发渲染机制，彻底改变了 React 应用的性能模型。",
                REACT18_CONTENT, frontend.getId(),
                Set.of(tagMap.get("react"), tagMap.get("typescript"), tagMap.get("git")),
                demoUser.getId(), true, 342, 12800, 8,
                "2025-12-15T08:00:00", "2025-12-20T10:30:00");

        createPost("Rust 与 WebAssembly：在浏览器中运行高性能计算",
                "rust-webassembly-browser",
                "WebAssembly 正在改变 Web 的能力边界。",
                RUST_WASM_CONTENT, frontend.getId(),
                Set.of(tagMap.get("rust"), tagMap.get("wasm"), tagMap.get("git")),
                demoUser.getId(), true, 256, 9400, 6,
                "2025-11-28T14:20:00", "2025-12-01T09:00:00");

        createPost("设计一个可扩展的 API 网关 — 从单体到微服务的平滑演进",
                "api-gateway-design",
                "API 网关是微服务架构中的关键组件。",
                API_GATEWAY_CONTENT, sysDesign.getId(),
                Set.of(tagMap.get("database"), tagMap.get("performance"), tagMap.get("api-design")),
                demoUser.getId(), false, 189, 7200, 7,
                "2025-11-10T16:00:00", "2025-11-15T11:00:00");

        createPost("搭建个人 AI 开发环境：Ollama + Continue + 本地 LLM 完全指南",
                "local-ai-dev-environment",
                "不想依赖云端 AI？本文教你如何在本地搭建完整的 AI 辅助开发环境。",
                AI_DEV_CONTENT, devtools.getId(),
                Set.of(tagMap.get("llm"), tagMap.get("typescript"), tagMap.get("python")),
                demoUser.getId(), false, 478, 15600, 5,
                "2025-10-22T09:30:00", "2025-10-25T14:00:00");

        createPost("TypeScript 类型体操实战：从入门到放弃再到精通",
                "typescript-type-gymnastics",
                "类型体操不只是面试题——它在实际项目中能显著提升代码的类型安全。",
                TYPESCRIPT_CONTENT, frontend.getId(),
                Set.of(tagMap.get("typescript"), tagMap.get("design-patterns")),
                demoUser.getId(), false, 312, 10800, 7,
                "2025-10-05T11:00:00", "2025-10-08T16:00:00");

        createPost("工程师的写作之道：为什么技术博客是你最好的投资",
                "why-engineers-should-write",
                "写作是工程师最被低估的技能之一。",
                WRITING_CONTENT, essays.getId(),
                Set.of(tagMap.get("typescript"), tagMap.get("design-patterns")),
                demoUser.getId(), true, 523, 18900, 5,
                "2025-09-18T08:00:00", "2025-09-20T12:00:00");

        log.info("Database seeding complete! Demo account: demo@xiaocheng.dev / demo123");
    }

    private Category insertCategory(String name, String slug, String desc, String color) {
        Category cat = Category.builder().name(name).slug(slug).description(desc).color(color).build();
        categoryMapper.insert(cat);
        return cat;
    }

    private void createPost(String title, String slug, String excerpt, String content,
                            Long categoryId, Set<Tag> tags, Long authorId,
                            boolean featured, int likes, int views, int readingTime,
                            String createdAt, String updatedAt) {
        Post post = Post.builder()
                .title(title).slug(slug).excerpt(excerpt).content(content)
                .categoryId(categoryId).authorId(authorId)
                .featured(featured).likes(likes).views(views)
                .readingTime(readingTime)
                .createdAt(LocalDateTime.parse(createdAt.replace("T", "T")))
                .updatedAt(LocalDateTime.parse(updatedAt.replace("T", "T")))
                .build();
        postMapper.insert(post);

        // Insert post_tags
        for (Tag tag : tags) {
            postTagMapper.insert(post.getId(), tag.getId());
        }
    }

    // ===== Article content =====

    private static final String REACT18_CONTENT = """
# 使用 React 18 构建高性能前端应用

## 引言

React 18 的发布标志着 React 生态系统的重大转折。并发特性（Concurrent Features）的引入不仅仅是性能优化——它是一种全新的思维方式。

## 并发渲染的核心概念

React 18 引入了**并发渲染**，这是一种新的底层机制，允许 React 同时准备多个版本的 UI。

```tsx
import { useTransition } from 'react';

function SearchResults() {
  const [isPending, startTransition] = useTransition();
  const [query, setQuery] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    startTransition(() => {
      setQuery(e.target.value);
    });
  };

  return (
    <div>
      <input onChange={handleChange} placeholder="搜索文章..." />
      {isPending && <Spinner />}
      <ResultsList query={query} />
    </div>
  );
}
```

## 总结

React 18 的并发特性为我们提供了更精细的渲染控制。
""";

    private static final String RUST_WASM_CONTENT = """
# Rust 与 WebAssembly：在浏览器中运行高性能计算

## 为什么选择 Rust + WASM？

WebAssembly（WASM）为浏览器带来了接近原生的执行速度。

## 性能对比

- **纯 JavaScript**: 1200ms（处理 4K 图像）
- **Rust + WASM**: 45ms（处理同样图像）
- **提升**: 约 **26 倍**

> WebAssembly 不是要取代 JavaScript，而是填补了 Web 平台上高性能计算的空白。
""";

    private static final String API_GATEWAY_CONTENT = """
# 设计一个可扩展的 API 网关

## 网关的核心职责

1. **路由转发** — 将请求准确路由到对应的服务
2. **认证授权** — 统一鉴权
3. **限流熔断** — 保护后端服务
4. **协议转换** — REST 到 gRPC
5. **日志监控** — 集中采集请求链路数据

## 总结

一个好的 API 网关设计应该遵循**关注点分离**原则。
""";

    private static final String AI_DEV_CONTENT = """
# 搭建个人 AI 开发环境

## 为什么选择本地 LLM？

- 🔒 **隐私安全** — 代码不会离开你的机器
- 💰 **零成本** — 无需 API 费用
- ⚡ **低延迟** — 本地推理，无网络延迟
- 🎯 **可定制** — 自由选择模型和参数

> 本地 LLM 的体验正在快速接近云端服务。
""";

    private static final String TYPESCRIPT_CONTENT = """
# TypeScript 类型体操实战

## 从实际需求出发

类型体操不只是面试题——它在实际项目中能显著提升代码的类型安全。

```typescript
type DeepReadonly<T> = {
  readonly [K in keyof T]: T[K] extends object
    ? DeepReadonly<T[K]>
    : T[K];
};
```

> 类型体操的本质是用类型系统表达业务约束。
""";

    private static final String WRITING_CONTENT = """
# 工程师的写作之道

## 写作的复利效应

技术写作是少数具有**复利效应**的工程师活动之一。

- 面试机会
- 社区认可
- 演讲邀请
- 咨询合作

> 写作不是为了炫耀你知道多少，而是为了让你知道自己不知道什么。

## 结语

开始写作最好的时间是五年前，其次是现在。
""";
}
