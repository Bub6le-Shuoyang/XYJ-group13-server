package com.xyj.xyjserver.service;

import com.xyj.xyjserver.entity.SplashAd;

import java.util.List;

public interface AdminSplashAdService {
    List<SplashAd> getAll();
    SplashAd create(SplashAd ad);
    SplashAd update(Long id, SplashAd ad);
    void delete(Long id);
}
