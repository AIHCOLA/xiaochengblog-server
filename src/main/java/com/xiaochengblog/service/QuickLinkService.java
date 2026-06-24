package com.xiaochengblog.service;

import com.xiaochengblog.mapper.QuickLinkMapper;
import com.xiaochengblog.model.QuickLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class QuickLinkService {

    private final QuickLinkMapper mapper;

    public List<Map<String, Object>> getAll(Long userId) {
        List<QuickLink> links = mapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (QuickLink l : links) {
            result.add(toMap(l));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> create(Long userId, String name, String url, String icon) {
        QuickLink link = QuickLink.builder()
                .userId(userId)
                .name(name)
                .url(url)
                .icon(icon)
                .sortOrder(0)
                .build();
        mapper.insert(link);
        return toMap(link);
    }

    @Transactional
    public Map<String, Object> update(Long userId, Long id, String name, String url, String icon) {
        QuickLink link = mapper.selectById(id);
        if (link == null || !link.getUserId().equals(userId)) {
            throw new RuntimeException("Link not found");
        }
        if (name != null) link.setName(name);
        if (url != null) link.setUrl(url);
        if (icon != null) link.setIcon(icon);
        mapper.updateById(link);
        return toMap(link);
    }

    @Transactional
    public Map<String, Object> delete(Long userId, Long id) {
        QuickLink link = mapper.selectById(id);
        if (link == null || !link.getUserId().equals(userId)) {
            throw new RuntimeException("Link not found");
        }
        mapper.deleteById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    private Map<String, Object> toMap(QuickLink l) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", l.getId());
        map.put("name", l.getName());
        map.put("url", l.getUrl());
        map.put("icon", l.getIcon());
        map.put("sortOrder", l.getSortOrder());
        return map;
    }
}
