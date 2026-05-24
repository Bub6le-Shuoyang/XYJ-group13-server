package com.xyj.xyjserver.service;

import com.xyj.xyjserver.entity.Admin;

public interface AdminProfileService {
    Admin getCurrentAdmin(Long adminId);
    Admin updateCurrentAdmin(Long adminId, Admin admin);
}