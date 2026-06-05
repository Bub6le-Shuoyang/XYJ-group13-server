package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.mapper.AnalyticsMapper;
import com.xyj.xyjserver.service.AdminAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    @Autowired
    private AnalyticsMapper analyticsMapper;

    @Override
    public List<Map<String, Object>> getPackageTrend(int days) {
        return analyticsMapper.getPackageTrend(days);
    }

    @Override
    public List<Map<String, Object>> getUserGrowth(int days) {
        return analyticsMapper.getUserGrowth(days);
    }

    @Override
    public List<Map<String, Object>> getPackageStatusDistribution() {
        return analyticsMapper.getPackageStatusDistribution();
    }

    @Override
    public List<Map<String, Object>> getCourierEfficiency(int limit) {
        return analyticsMapper.getCourierEfficiency(limit);
    }

    @Override
    public Map<String, Object> getOverallStats() {
        return analyticsMapper.getOverallStats();
    }
}
