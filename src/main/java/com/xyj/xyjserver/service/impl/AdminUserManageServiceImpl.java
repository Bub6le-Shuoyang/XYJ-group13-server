package com.xyj.xyjserver.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.dto.CreateUserDTO;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.service.AdminUserManageService;
import com.xyj.xyjserver.vo.UserListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserManageServiceImpl implements AdminUserManageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PageResult<UserListVO> getUserList(Long page, Long size, String keyword) {
        long offset = (page - 1) * size;
        List<User> users = userMapper.searchByKeyword(keyword, offset, size);
        long total = userMapper.countByKeyword(keyword);

        List<UserListVO> voList = users.stream().map(this::toVO).collect(Collectors.toList());
        return new PageResult<>(voList, total, size, page);
    }

    @Override
    public User createUser(CreateUserDTO createDTO) {
        // 检查 email 是否已存在
        if (createDTO.getEmail() != null && !createDTO.getEmail().isBlank()) {
            long count = userMapper.countByEmailExclude(createDTO.getEmail(), 0L);
            if (count > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "邮箱已被注册");
            }
        }
        // 检查 phone 是否已存在
        if (createDTO.getPhone() != null && !createDTO.getPhone().isBlank()) {
            long count = userMapper.countByPhoneExclude(createDTO.getPhone(), 0L);
            if (count > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "手机号已被注册");
            }
        }

        User user = new User();
        user.setUserNo("U" + System.currentTimeMillis());
        // account 映射到 email：如果 email 为空则用 account 作为 email
        if (createDTO.getEmail() != null && !createDTO.getEmail().isBlank()) {
            user.setEmail(createDTO.getEmail());
        } else {
            user.setEmail(createDTO.getAccount());
        }
        user.setPhone(createDTO.getPhone());
        user.setNickname(createDTO.getNickname() != null ? createDTO.getNickname() : createDTO.getAccount());
        user.setPasswordHash(BCrypt.hashpw(createDTO.getPassword(), BCrypt.gensalt()));
        user.setStatus(1);

        userMapper.insert(user);
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户不存在");
        }

        // 如果更新了 email，检查唯一性
        if (user.getEmail() != null && !user.getEmail().equals(existing.getEmail())) {
            long count = userMapper.countByEmailExclude(user.getEmail(), id);
            if (count > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "邮箱已被其他用户使用");
            }
        }
        // 如果更新了 phone，检查唯一性
        if (user.getPhone() != null && !user.getPhone().equals(existing.getPhone())) {
            long count = userMapper.countByPhoneExclude(user.getPhone(), id);
            if (count > 0) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "手机号已被其他用户使用");
            }
        }

        user.setId(id);
        userMapper.update(user);
        return userMapper.findById(id);
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户不存在");
        }
        int rows = userMapper.softDelete(id);
        if (rows == 0) {
            throw new BusinessException(ResultCode.FAILED, "删除失败");
        }
    }

    /**
     * User 实体 → UserListVO
     */
    private UserListVO toVO(User u) {
        UserListVO vo = new UserListVO();
        vo.setId(u.getId());
        vo.setUserNo(u.getUserNo());
        vo.setAccount(u.getEmail() != null ? u.getEmail() : u.getPhone());
        vo.setEmail(u.getEmail());
        vo.setPhone(u.getPhone());
        vo.setNickname(u.getNickname());
        vo.setRole("USER"); // users 表无 role 字段，统一为 USER
        vo.setCreatedAt(u.getCreatedAt());
        return vo;
    }
}