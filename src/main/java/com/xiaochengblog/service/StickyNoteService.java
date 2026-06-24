package com.xiaochengblog.service;

import com.xiaochengblog.mapper.StickyNoteMapper;
import com.xiaochengblog.model.StickyNote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StickyNoteService {

    private final StickyNoteMapper mapper;

    public Map<String, Object> get(Long userId) {
        StickyNote sn = mapper.selectByUserId(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        if (sn != null) {
            result.put("content", sn.getContent() != null ? sn.getContent() : "");
            result.put("colorIndex", sn.getColorIndex() != null ? sn.getColorIndex() : 0);
        } else {
            result.put("content", "");
            result.put("colorIndex", 0);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> save(Long userId, String content, Integer colorIndex) {
        StickyNote sn = mapper.selectByUserId(userId);
        if (sn != null) {
            if (content != null) sn.setContent(content);
            if (colorIndex != null) sn.setColorIndex(colorIndex);
            mapper.updateById(sn);
        } else {
            sn = StickyNote.builder()
                    .userId(userId)
                    .content(content != null ? content : "")
                    .colorIndex(colorIndex != null ? colorIndex : 0)
                    .build();
            mapper.insert(sn);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", sn.getContent());
        result.put("colorIndex", sn.getColorIndex());
        return result;
    }
}
