package com.agnelle.backend.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnalyticsService {

    private final Map<String, String> userDevices = new ConcurrentHashMap<>();
    private long desktopUsers = 0;
    private long mobileUsers = 0;

    public synchronized void registerVisit(String visitorId, String userAgent) {
        if (!userDevices.containsKey(visitorId)) {
            userDevices.put(visitorId, userAgent);

            if (isMobile(userAgent)) {
                mobileUsers++;
            } else {
                desktopUsers++;
            }
        }
    }

    public synchronized Map<String, Long> getUserCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("desktop", desktopUsers);
        counts.put("mobile", mobileUsers);
        return counts;
    }

    public boolean isMobile(String userAgent) {
        return userAgent.toLowerCase().matches(".*(android|iphone|ipad|mobile).*");
    }
}
