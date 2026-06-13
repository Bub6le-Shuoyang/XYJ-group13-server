package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.service.SysConfigService;
import com.xyj.xyjserver.vo.SplashAdVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SysConfigController.class)
class SysConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SysConfigService sysConfigService;

    @Test
    void getRandomSplashAd_shouldReturnAd() throws Exception {
        SplashAdVO ad = new SplashAdVO();
        ad.setAdNo("AD-001");
        ad.setName("开屏广告");
        ad.setImageUrl("/uploads/ad.png");
        ad.setTargetUrl("https://example.com");
        when(sysConfigService.getRandomSplashAd()).thenReturn(ad);

        mockMvc.perform(get("/api/v1/sys/ads/splash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.ad_no").value("AD-001"));
    }

    @Test
    void getRandomSplashAd_noAdConfigured_shouldReturnNullData() throws Exception {
        when(sysConfigService.getRandomSplashAd()).thenReturn(null);

        mockMvc.perform(get("/api/v1/sys/ads/splash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
