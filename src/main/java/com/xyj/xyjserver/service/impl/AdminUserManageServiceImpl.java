package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CreateUserDTO;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.service.AdminUserManageService;
import com.xyj.xyjserver.vo.UserListVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class AdminUserManageServiceImpl implements AdminUserManageService {

    @Override
    public PageResult<UserListVO> getUserList(Long page, Long size, String keyword) {
        UserListVO vo = new UserListVO();
        vo.setId(1L);
        vo.setUserNo("U20260524001");
        vo.setAccount("villager_01");
        vo.setEmail("v1@example.com");
        vo.setPhone("13800000001");
        vo.setNickname("村民张三");
        vo.setRole("VILLAGER");
        vo.setCreatedAt(new Date());
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public User createUser(CreateUserDTO createDTO) {
        User user = new User();
        user.setId(System.currentTimeMillis());
        user.setUserNo("U" + System.currentTimeMillis());
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setNickname(createDTO.getNickname());
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        user.setPasswordHash(null);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        // Mock delete logic
    }
}