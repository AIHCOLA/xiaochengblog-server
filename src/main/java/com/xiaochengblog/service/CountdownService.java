package com.xiaochengblog.service;

import com.xiaochengblog.mapper.CountdownMapper;
import com.xiaochengblog.model.Countdown;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CountdownService {

    private final CountdownMapper countdownMapper;

    public List<Map<String, Object>> getCountdowns(Long userId) {
        List<Countdown> items = countdownMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Countdown c : items) {
            result.add(toMap(c));
        }
        return result;
    }

    @Transactional
    public Map<String, Object> createCountdown(Long userId, String name, LocalDateTime targetDate) {
        Countdown cd = Countdown.builder()
                .userId(userId)
                .name(name)
                .targetDate(targetDate)
                .build();
        countdownMapper.insert(cd);
        return toMap(cd);
    }

    @Transactional
    public Map<String, Object> deleteCountdown(Long userId, Long id) {
        Countdown cd = countdownMapper.selectById(id);
        if (cd == null || !cd.getUserId().equals(userId)) {
            throw new RuntimeException("Countdown not found");
        }
        countdownMapper.deleteById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", id);
        result.put("deleted", true);
        return result;
    }

    private Map<String, Object> toMap(Countdown c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("name", c.getName());
        map.put("targetDate", c.getTargetDate().toString());
        map.put("createdAt", c.getCreatedAt());
        return map;
    }
}
