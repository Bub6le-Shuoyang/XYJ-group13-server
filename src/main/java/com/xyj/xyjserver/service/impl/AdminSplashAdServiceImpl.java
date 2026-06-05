package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.SplashAd;
import com.xyj.xyjserver.mapper.SplashAdMapper;
import com.xyj.xyjserver.service.AdminSplashAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSplashAdServiceImpl implements AdminSplashAdService {

    @Autowired
    private SplashAdMapper splashAdMapper;

    @Override
    public List<SplashAd> getAll() {
        return splashAdMapper.findAll();
    }

    @Override
    public SplashAd create(SplashAd ad) {
        ad.setAdNo("AD-" + System.currentTimeMillis());
        if (ad.getStatus() == null) {
            ad.setStatus(1);
        }
        splashAdMapper.insert(ad);
        return splashAdMapper.findById(ad.getId());
    }

    @Override
    public SplashAd update(Long id, SplashAd ad) {
        SplashAd existing = splashAdMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "广告不存在");
        }
        ad.setId(id);
        splashAdMapper.update(ad);
        return splashAdMapper.findById(id);
    }

    @Override
    public void delete(Long id) {
        SplashAd existing = splashAdMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "广告不存在");
        }
        splashAdMapper.deleteById(id);
    }
}
