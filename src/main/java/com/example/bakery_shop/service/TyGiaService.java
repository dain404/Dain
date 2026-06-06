package com.example.bakery_shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TyGiaService {

    private final RestTemplate restTemplate;

    @Value("${exchangerate.api.url}")
    private String apiUrl;

    @Value("${exchangerate.cache.ttl:3600}")
    private long cacheTtlSeconds;

    private final ConcurrentHashMap<String, CachedRate> cache = new ConcurrentHashMap<>();
    private static final String CACHE_KEY = "VND_RATE";
    private static final BigDecimal FALLBACK_RATE = new BigDecimal("25000.0");

    private static class CachedRate {
        final BigDecimal rate;
        final Instant timestamp;

        CachedRate(BigDecimal rate, Instant timestamp) {
            this.rate = rate;
            this.timestamp = timestamp;
        }
    }

    @SuppressWarnings("unchecked")
    public BigDecimal layTyGiaUSD() {
        CachedRate cached = cache.get(CACHE_KEY);
        if (cached != null && Instant.now().minusSeconds(cacheTtlSeconds).isBefore(cached.timestamp)) {
            return cached.rate;
        }

        try {
            Map response = restTemplate.getForObject(apiUrl, Map.class);
            if (response != null && "success".equals(response.get("result"))) {
                Map<String, Object> rates = (Map<String, Object>) response.get("rates");
                if (rates != null && rates.containsKey("VND")) {
                    Number vndRate = (Number) rates.get("VND");
                    BigDecimal rate = new BigDecimal(vndRate.toString());
                    cache.put(CACHE_KEY, new CachedRate(rate, Instant.now()));
                    log.info("Cập nhật tỷ giá mới: 1 USD = {} VND", rate);
                    return rate;
                }
            }
        } catch (Exception e) {
            log.warn("Lỗi khi lấy tỷ giá từ API: {}. Sử dụng tỷ giá mặc định.", e.getMessage());
        }

        // Return cached value if API fails but we have an expired cache, or fallback
        if (cached != null) {
            return cached.rate;
        }
        return FALLBACK_RATE;
    }

    public String formatGiaUSD(BigDecimal giaVND) {
        if (giaVND == null) return "$0.00";
        BigDecimal tyGia = layTyGiaUSD();
        BigDecimal usd = giaVND.divide(tyGia, 2, RoundingMode.HALF_UP);
        return "$" + usd.toString();
    }
}
