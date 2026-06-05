package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.entity.CustomerServiceConfig;
import com.xyj.xyjserver.mapper.CustomerServiceConfigMapper;
import com.xyj.xyjserver.service.AdminCustomerServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminCustomerServiceServiceImpl implements AdminCustomerServiceService {

    @Autowired
    private CustomerServiceConfigMapper customerServiceConfigMapper;

    @Override
    public CustomerServiceConfig getActive() {
        return customerServiceConfigMapper.findActive();
    }

    @Override
    public List<CustomerServiceConfig> getAll() {
        return customerServiceConfigMapper.findAll();
    }

    @Override
    public CustomerServiceConfig save(CustomerServiceConfig config) {
        if (config.getId() != null) {
            CustomerServiceConfig existing = customerServiceConfigMapper.findById(config.getId());
            if (existing != null) {
                customerServiceConfigMapper.update(config);
                return customerServiceConfigMapper.findById(config.getId());
            }
        }
        if (config.getStatus() == null) {
            config.setStatus(1);
        }
        customerServiceConfigMapper.insert(config);
        return customerServiceConfigMapper.findById(config.getId());
    }
}
