package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CreateUserDTO;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.vo.UserListVO;

public interface AdminUserManageService {
    PageResult<UserListVO> getUserList(Long page, Long size, String keyword);
    User createUser(CreateUserDTO createDTO);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
}