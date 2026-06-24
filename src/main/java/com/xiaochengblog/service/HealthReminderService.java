package com.xiaochengblog.service;

import com.xiaochengblog.mapper.HealthReminderMapper;
import com.xiaochengblog.model.HealthReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HealthReminderService {

    private final HealthReminderMapper mapper;

    public Map<String, Boolean> getStates(Long userId) {
        List<HealthReminder> items = mapper.selectByUserId(userId);
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (HealthReminder h : items) {
            result.put(h.getReminderId(), h.getActive());
        }
        return result;
    }

    @Transactional
    public Map<String, Object> setState(Long userId, String reminderId, Boolean active) {
        HealthReminder existing = mapper.selectByUserAndReminder(userId, reminderId);
        if (existing != null) {
            existing.setActive(active);
            mapper.updateById(existing);
        } else {
            HealthReminder hr = HealthReminder.builder()
                    .userId(userId)
                    .reminderId(reminderId)
                    .active(active)
                    .build();
            mapper.insert(hr);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reminderId", reminderId);
        result.put("active", active);
        return result;
    }
}
