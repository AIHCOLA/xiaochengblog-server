package com.xiaochengblog.controller;

import com.xiaochengblog.dto.ApiResponse;
import com.xiaochengblog.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/now")
    public ApiResponse<Map<String, Object>> getWeather(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            HttpServletRequest request) {
        return ApiResponse.success(weatherService.getWeather(lat, lon, request));
    }
}
