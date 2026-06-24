package com.xiaochengblog.service;

import com.xiaochengblog.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";

    private final RestTemplate restTemplate;

    public WeatherService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getWeather(Double lat, Double lon, HttpServletRequest request) {
        String ip = getClientIp(request);
        // Get city name from IP geolocation (always works, no reverse geocode needed)
        String cityName = getCityByIp(ip);

        // If no coords from browser, fall back to IP-based coords
        if (lat == null || lon == null) {
            double[] coords = getCoordsByIp(ip);
            lat = coords[0];
            lon = coords[1];
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(WEATHER_URL)
                .queryParam("latitude", lat)
                .queryParam("longitude", lon)
                .queryParam("current", "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m")
                .queryParam("daily", "temperature_2m_max,temperature_2m_min")
                .queryParam("timezone", "Asia/Shanghai")
                .queryParam("forecast_days", 1)
                .build().encode().toUri();

        Map<String, Object> resp = restTemplate.getForObject(uri, Map.class);
        if (resp == null) {
            throw new BusinessException(502, "天气服务不可用");
        }

        Map<String, Object> current = (Map<String, Object>) resp.get("current");
        Map<String, Object> daily = (Map<String, Object>) resp.get("daily");

        int weatherCode = ((Number) current.get("weather_code")).intValue();
        double windSpeed = ((Number) current.get("wind_speed_10m")).doubleValue();
        int windDir = ((Number) current.get("wind_direction_10m")).intValue();

        double high = 0, low = 0;
        List<Number> maxList = (List<Number>) daily.get("temperature_2m_max");
        List<Number> minList = (List<Number>) daily.get("temperature_2m_min");
        if (maxList != null && !maxList.isEmpty()) {
            high = maxList.get(0).doubleValue();
        }
        if (minList != null && !minList.isEmpty()) {
            low = minList.get(0).doubleValue();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("temp", Math.round(((Number) current.get("temperature_2m")).doubleValue()));
        result.put("condition", mapCondition(weatherCode));
        result.put("icon", mapIcon(weatherCode));
        result.put("humidity", ((Number) current.get("relative_humidity_2m")).intValue());
        result.put("wind", windDirToChinese(windDir) + " " + windSpeedToScale(windSpeed) + "级");
        result.put("location", cityName);
        result.put("high", Math.round(high));
        result.put("low", Math.round(low));

        return result;
    }

    @SuppressWarnings("unchecked")
    private String getCityByIp(String ip) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl("http://ip-api.com/json/" + ip)
                    .queryParam("fields", "city")
                    .queryParam("lang", "zh-CN")
                    .build().encode().toUri();
            Map<String, Object> resp = restTemplate.getForObject(uri, Map.class);
            if (resp != null && "success".equals(resp.get("status"))) {
                String city = (String) resp.get("city");
                if (city != null && !city.isBlank()) {
                    return city;
                }
            }
        } catch (Exception ignored) {}
        return "你的位置";
    }

    @SuppressWarnings("unchecked")
    private double[] getCoordsByIp(String ip) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl("http://ip-api.com/json/" + ip)
                    .queryParam("fields", "lat,lon")
                    .queryParam("lang", "zh-CN")
                    .build().encode().toUri();
            Map<String, Object> resp = restTemplate.getForObject(uri, Map.class);
            if (resp != null && "success".equals(resp.get("status"))) {
                double lat = ((Number) resp.get("lat")).doubleValue();
                double lon = ((Number) resp.get("lon")).doubleValue();
                return new double[]{lat, lon};
            }
        } catch (Exception ignored) {}
        return new double[]{22.5431, 114.0579};
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String mapIcon(int code) {
        if (code == 0) {
            return "sunny";
        }
        if (code >= 1 && code <= 3) {
            return "partly";
        }
        if (code >= 51 && code <= 99) {
            return "rain";
        }
        return "cloudy";
    }

    private String mapCondition(int code) {
        if (code == 0) {
            return "晴";
        }
        if (code == 1) {
            return "少云";
        }
        if (code == 2) {
            return "多云";
        }
        if (code == 3) {
            return "阴";
        }
        if (code == 45 || code == 48) {
            return "雾";
        }
        if (code == 51 || code == 53 || code == 55) {
            return "毛毛雨";
        }
        if (code == 61 || code == 63 || code == 65) {
            return "雨";
        }
        if (code == 71 || code == 73 || code == 75) {
            return "雪";
        }
        if (code == 80 || code == 81 || code == 82) {
            return "阵雨";
        }
        if (code == 95 || code == 96 || code == 99) {
            return "雷暴";
        }
        return "多云";
    }

    private String windDirToChinese(int degrees) {
        if (degrees >= 337 || degrees < 23) {
            return "北风";
        }
        if (degrees < 68) {
            return "东北风";
        }
        if (degrees < 113) {
            return "东风";
        }
        if (degrees < 158) {
            return "东南风";
        }
        if (degrees < 203) {
            return "南风";
        }
        if (degrees < 248) {
            return "西南风";
        }
        if (degrees < 293) {
            return "西风";
        }
        return "西北风";
    }

    private int windSpeedToScale(double kmh) {
        if (kmh < 1) {
            return 0;
        }
        if (kmh < 6) {
            return 1;
        }
        if (kmh < 12) {
            return 2;
        }
        if (kmh < 20) {
            return 3;
        }
        if (kmh < 29) {
            return 4;
        }
        if (kmh < 39) {
            return 5;
        }
        if (kmh < 50) {
            return 6;
        }
        if (kmh < 62) {
            return 7;
        }
        if (kmh < 75) {
            return 8;
        }
        return 9;
    }
}
