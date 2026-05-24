package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.Admin;
import com.xyj.xyjserver.mapper.AdminMapper;
import com.xyj.xyjserver.service.AdminProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminProfileServiceImpl implements AdminProfileService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getCurrentAdmin(Long adminId) {
        Admin admin = adminMapper.findById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "管理员不存在");
        }
        admin.setPasswordHash(null);
        return admin;
    }

    @Override
    public Admin updateCurrentAdmin(Long adminId, Admin admin) {
        admin.setId(adminId);
        adminMapper.updateProfile(admin);
        return getCurrentAdmin(adminId);
    }
}