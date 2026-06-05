package com.xyj.xyjserver.service;

import com.xyj.xyjserver.entity.CustomerServiceConfig;

import java.util.List;

public interface AdminCustomerServiceService {
    CustomerServiceConfig getActive();
    List<CustomerServiceConfig> getAll();
    CustomerServiceConfig save(CustomerServiceConfig config);
}
