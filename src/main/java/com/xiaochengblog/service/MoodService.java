package com.xiaochengblog.service;

import com.xiaochengblog.mapper.MoodMapper;
import com.xiaochengblog.model.Mood;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodMapper moodMapper;

    public List<Map<String, Object>> getAllMoods(Long userId) {
        List<Mood> moods = moodMapper.selectByUserId(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Mood m : moods) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", m.getDate().toString());
            map.put("emoji", m.getEmoji());
            map.put("checkedIn", m.getCheckedIn());
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> getMoods(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Mood> moods = moodMapper.selectByUserIdAndDateRange(userId, startDate, endDate);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Mood m : moods) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", m.getDate().toString());
            map.put("emoji", m.getEmoji());
            map.put("checkedIn", m.getCheckedIn());
            result.add(map);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> upsertMood(Long userId, LocalDate date, String emoji, Boolean checkedIn) {
        Mood existing = moodMapper.selectByUserIdAndDate(userId, date);

        if (existing != null) {
            if (emoji != null) {
                existing.setEmoji(emoji.isEmpty() ? null : emoji);
            }
            if (checkedIn != null) {
                existing.setCheckedIn(checkedIn);
            }
            moodMapper.updateById(existing);
        } else {
            existing = Mood.builder()
                    .userId(userId)
                    .date(date)
                    .emoji(emoji)
                    .checkedIn(checkedIn != null && checkedIn)
                    .build();
            moodMapper.insert(existing);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("emoji", existing.getEmoji());
        result.put("checkedIn", existing.getCheckedIn());
        return result;
    }

    @Transactional
    public Map<String, Object> deleteMood(Long userId, LocalDate date) {
        Mood existing = moodMapper.selectByUserIdAndDate(userId, date);
        if (existing != null) {
            moodMapper.deleteById(existing.getId());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("deleted", true);
        return result;
    }
}
