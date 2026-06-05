package com.xyj.xyjserver.common.config;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.mapper.AdminMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 系统启动时自动初始化默认管理员账号
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public void run(String... args) {
        Admin existing = adminMapper.findByAccount("admin");
        if (existing == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPasswordHash(BCrypt.hashpw("123456", BCrypt.gensalt()));
            admin.setRealName("超级管理员");
            admin.setEmail("admin@xyj.com");
            admin.setPhone("13800000000");
            admin.setRole(3); // 超级管理员
            admin.setStatus(1);

            // 尝试绑定默认站点
            try {
                adminMapper.insert(admin);
                log.info("✅ 默认管理员账号已创建 — 用户名: admin, 密码: 123456");
            } catch (Exception e) {
                log.warn("⚠️ 创建默认管理员失败: {}", e.getMessage());
            }
        } else {
            log.info("ℹ️ 默认管理员账号已存在，跳过初始化");
        }
    }
}
