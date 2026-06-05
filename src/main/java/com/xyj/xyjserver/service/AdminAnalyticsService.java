package com.xyj.xyjserver.service;

import java.util.List;
import java.util.Map;

public interface AdminAnalyticsService {
    List<Map<String, Object>> getPackageTrend(int days);
    List<Map<String, Object>> getUserGrowth(int days);
    List<Map<String, Object>> getPackageStatusDistribution();
    List<Map<String, Object>> getCourierEfficiency(int limit);
    Map<String, Object> getOverallStats();
}
