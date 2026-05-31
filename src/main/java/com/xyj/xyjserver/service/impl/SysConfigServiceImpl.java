package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.entity.SplashAd;
import com.xyj.xyjserver.mapper.SplashAdMapper;
import com.xyj.xyjserver.service.SysConfigService;
import com.xyj.xyjserver.vo.SplashAdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Autowired
    private SplashAdMapper splashAdMapper;

    @Override
    public SplashAdVO getRandomSplashAd() {
        List<SplashAd> ads = splashAdMapper.findAllActiveAds();
        if (ads == null || ads.isEmpty()) {
            return null;
        }

        // 计算总权重
        int totalWeight = ads.stream().mapToInt(SplashAd::getWeight).sum();
        if (totalWeight <= 0) {
            // 如果所有权重都是 0，直接随机返回一个
            return convertToVO(ads.get(new Random().nextInt(ads.size())));
        }

        // 随机一个介于 [0, totalWeight) 之间的数值
        int randomValue = new Random().nextInt(totalWeight);
        int currentSum = 0;

        // 根据权重区间匹配广告
        for (SplashAd ad : ads) {
            currentSum += ad.getWeight();
            if (randomValue < currentSum) {
                return convertToVO(ad);
            }
        }

        // 兜底返回第一个
        return convertToVO(ads.get(0));
    }

    private SplashAdVO convertToVO(SplashAd ad) {
        SplashAdVO vo = new SplashAdVO();
        vo.setAdNo(ad.getAdNo());
        vo.setName(ad.getName());
        vo.setImageUrl(ad.getImageUrl());
        vo.setTargetUrl(cleanUrl(ad.getTargetUrl()));
        return vo;
    }

    private String cleanUrl(String url) {
        if (url == null) {
            return null;
        }
        return url.trim().replace("`", "");
    }
}
