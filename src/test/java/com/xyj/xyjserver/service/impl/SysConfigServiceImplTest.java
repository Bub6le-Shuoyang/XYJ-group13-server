package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.entity.SplashAd;
import com.xyj.xyjserver.mapper.SplashAdMapper;
import com.xyj.xyjserver.vo.SplashAdVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysConfigServiceImplTest {

    @Mock
    private SplashAdMapper splashAdMapper;

    @InjectMocks
    private SysConfigServiceImpl sysConfigService;

    @Test
    void getRandomSplashAd_noAds_shouldReturnNull() {
        when(splashAdMapper.findAllActiveAds()).thenReturn(Collections.emptyList());

        assertNull(sysConfigService.getRandomSplashAd());
    }

    @Test
    void getRandomSplashAd_withWeightedAds_shouldReturnValidAd() {
        SplashAd ad1 = buildAd("AD-001", "广告A", 3);
        SplashAd ad2 = buildAd("AD-002", "广告B", 7);
        when(splashAdMapper.findAllActiveAds()).thenReturn(Arrays.asList(ad1, ad2));

        SplashAdVO vo = sysConfigService.getRandomSplashAd();

        assertNotNull(vo);
        assertTrue(vo.getAdNo().equals("AD-001") || vo.getAdNo().equals("AD-002"));
    }

    @Test
    void getRandomSplashAd_zeroWeight_shouldStillReturnAd() {
        SplashAd ad = buildAd("AD-003", "默认广告", 0);
        when(splashAdMapper.findAllActiveAds()).thenReturn(List.of(ad));

        SplashAdVO vo = sysConfigService.getRandomSplashAd();

        assertEquals("AD-003", vo.getAdNo());
    }

    @Test
    void getRandomSplashAd_shouldCleanTargetUrl() {
        SplashAd ad = buildAd("AD-004", "跳转广告", 1);
        ad.setTargetUrl(" `https://example.com` ");
        when(splashAdMapper.findAllActiveAds()).thenReturn(List.of(ad));

        SplashAdVO vo = sysConfigService.getRandomSplashAd();

        assertEquals("https://example.com", vo.getTargetUrl());
    }

    private SplashAd buildAd(String adNo, String name, int weight) {
        SplashAd ad = new SplashAd();
        ad.setAdNo(adNo);
        ad.setName(name);
        ad.setImageUrl("/uploads/ad.png");
        ad.setWeight(weight);
        return ad;
    }
}
