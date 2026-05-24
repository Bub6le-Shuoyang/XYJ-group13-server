package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.UserMapper;
import com.xyj.xyjserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }
}