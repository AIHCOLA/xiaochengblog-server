package com.xiaochengblog.service;

import com.xiaochengblog.mapper.FlashcardKnownMapper;
import com.xiaochengblog.model.FlashcardKnown;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FlashcardService {

    private final FlashcardKnownMapper flashcardKnownMapper;

    public List<Integer> getKnownWordIds(Long userId) {
        return flashcardKnownMapper.selectKnownWordIds(userId);
    }

    @Transactional
    public Map<String, Object> markKnown(Long userId, Integer wordId) {
        FlashcardKnown existing = flashcardKnownMapper.selectByUserAndWord(userId, wordId);
        if (existing == null) {
            FlashcardKnown fk = FlashcardKnown.builder()
                    .userId(userId)
                    .wordId(wordId)
                    .build();
            flashcardKnownMapper.insert(fk);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("wordId", wordId);
        result.put("known", true);
        return result;
    }

    @Transactional
    public Map<String, Object> markUnknown(Long userId, Integer wordId) {
        FlashcardKnown existing = flashcardKnownMapper.selectByUserAndWord(userId, wordId);
        if (existing != null) {
            flashcardKnownMapper.deleteById(existing.getId());
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("wordId", wordId);
        result.put("known", false);
        return result;
    }
}
