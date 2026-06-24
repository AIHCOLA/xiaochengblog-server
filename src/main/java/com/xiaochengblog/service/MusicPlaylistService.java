package com.xiaochengblog.service;

import com.xiaochengblog.mapper.MusicPlaylistMapper;
import com.xiaochengblog.model.MusicPlaylist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MusicPlaylistService {

    private final MusicPlaylistMapper musicPlaylistMapper;

    public List<Map<String, Object>> getPlaylist(Long userId) {
        List<MusicPlaylist> list = musicPlaylistMapper.selectByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (MusicPlaylist m : list) {
            result.add(toMap(m));
        }
        return result;
    }

    @Transactional
    public List<Map<String, Object>> savePlaylist(Long userId, List<Map<String, Object>> songs) {
        musicPlaylistMapper.deleteByUserId(userId);
        List<MusicPlaylist> entities = new ArrayList<>();
        int order = 0;
        for (Map<String, Object> s : songs) {
            MusicPlaylist mp = MusicPlaylist.builder()
                    .userId(userId)
                    .songId(String.valueOf(s.get("songId")))
                    .title(s.get("title").toString())
                    .artist(s.getOrDefault("artist", "").toString())
                    .fee(s.get("fee") != null ? Integer.valueOf(s.get("fee").toString()) : 0)
                    .platform(s.getOrDefault("platform", "ncm").toString())
                    .sortOrder(order++)
                    .build();
            entities.add(mp);
        }
        if (!entities.isEmpty()) {
            for (MusicPlaylist mp : entities) {
                musicPlaylistMapper.insert(mp);
            }
        }
        return getPlaylist(userId);
    }

    @Transactional
    public Map<String, Object> clearPlaylist(Long userId) {
        musicPlaylistMapper.deleteByUserId(userId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("cleared", true);
        return result;
    }

    private Map<String, Object> toMap(MusicPlaylist m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", m.getId());
        map.put("songId", m.getSongId());
        map.put("title", m.getTitle());
        map.put("artist", m.getArtist());
        map.put("fee", m.getFee());
        map.put("platform", m.getPlatform() != null ? m.getPlatform() : "ncm");
        map.put("sortOrder", m.getSortOrder());
        return map;
    }
}
