package com.agnelle.backend.controller;

import com.agnelle.backend.service.AnalyticsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/analytics")
public class AnalyticsController {

    @Autowired
    private final AnalyticsService analyticsService;

    private AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/track")
    public ResponseEntity<Map<String, Long>> trackUser(@CookieValue(value = "visitorId", required = false) String visitorId,
                                                       @RequestHeader(value = "User-Agent") String userAgent,
                                                       @RequestParam String pageUrl,
                                                       HttpServletResponse response) {
        boolean isNewUser = (visitorId == null);
        if (isNewUser) {
            visitorId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("visitorId", visitorId);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        analyticsService.registerVisit(visitorId, userAgent);
        Map<String, Long> counts = analyticsService.getUserCounts();
        return ResponseEntity.ok(counts);
    }

}
