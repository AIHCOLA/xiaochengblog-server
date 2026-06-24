package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@RestController
public class MusicController {

    private static final Logger log = LoggerFactory.getLogger(MusicController.class);

    // All music platforms go through the unified music API on port 3100.
    // The unified server handles ncm (NetEase) by proxying internally to port 3000,
    // and handles qq/kugou/qishui directly.
    private static final String MUSIC_API_BASE = "http://localhost:3100";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Proxy all music API requests to the unified music service on port 3100.
     */
    @RequestMapping("/api/music/**")
    public ApiResponse<Object> proxy(HttpServletRequest request, @RequestBody(required = false) String body) {
        String path = request.getRequestURI().replace("/api/music", "");

        String query = request.getQueryString();
        String rawUrl = MUSIC_API_BASE + path + (query != null ? "?" + query : "");
        URI uri = URI.create(rawUrl);
        log.info("Music proxy: {} -> {}", request.getRequestURI(), uri);

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(name -> {
            if (!"host".equalsIgnoreCase(name) && !"content-length".equalsIgnoreCase(name)) {
                headers.addAll(name, Collections.list(request.getHeaders(name)));
            }
        });

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(uri, method, entity, Object.class);
            Object result = response.getBody();
            if (result == null) {
                return ApiResponse.error(502, "音乐服务返回空数据");
            }
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error(500, "音乐服务暂时不可用");
        }
    }
}
