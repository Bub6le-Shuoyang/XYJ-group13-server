package com.xyj.xyjserver;

import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.mapper.AdminMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DbTest {

    @Autowired
    private AdminMapper adminMapper;

    @Test
    public void testFind() {
        Admin admin = adminMapper.findByAccount("admin@example.com");
        System.out.println("ADMIN=" + admin);
        if (admin != null) {
            System.out.println("HASH=" + admin.getPasswordHash());
            boolean match = cn.hutool.crypto.digest.BCrypt.checkpw("MyPass123!", admin.getPasswordHash());
            System.out.println("MATCH=" + match);
        }
    }
}
