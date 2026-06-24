package com.xiaochengblog.service;

import com.xiaochengblog.dto.GuestbookRequest;
import com.xiaochengblog.exception.BusinessException;
import com.xiaochengblog.mapper.GuestbookMapper;
import com.xiaochengblog.model.GuestbookEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {

    private final GuestbookMapper guestbookMapper;

    public List<Map<String, Object>> getAllEntries() {
        return guestbookMapper.selectAllOrdered().stream()
                .map(this::toMap)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addEntry(GuestbookRequest request) {
        GuestbookEntry entry = GuestbookEntry.builder()
                .author(request.getAuthor())
                .content(request.getContent())
                .build();

        guestbookMapper.insert(entry);
        return toMap(entry);
    }

    @Transactional
    public void deleteEntry(Long entryId) {
        GuestbookEntry entry = guestbookMapper.selectById(entryId);
        if (entry == null) throw new BusinessException(404, "留言不存在");
        guestbookMapper.deleteById(entryId);
    }

    private Map<String, Object> toMap(GuestbookEntry entry) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", entry.getId());
        map.put("author", entry.getAuthor());
        map.put("content", entry.getContent());
        map.put("createdAt", entry.getCreatedAt());
        return map;
    }
}
