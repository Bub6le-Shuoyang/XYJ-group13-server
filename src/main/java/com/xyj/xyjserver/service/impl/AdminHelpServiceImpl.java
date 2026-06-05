package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.HelpItem;
import com.xyj.xyjserver.mapper.HelpItemMapper;
import com.xyj.xyjserver.service.AdminHelpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminHelpServiceImpl implements AdminHelpService {

    @Autowired
    private HelpItemMapper helpItemMapper;

    @Override
    public List<HelpItem> getAll() {
        return helpItemMapper.findAll();
    }

    @Override
    public HelpItem create(HelpItem item) {
        item.setHelpNo("HELP-" + System.currentTimeMillis());
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        helpItemMapper.insert(item);
        return helpItemMapper.findById(item.getId());
    }

    @Override
    public HelpItem update(Long id, HelpItem item) {
        HelpItem existing = helpItemMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "帮助项不存在");
        }
        item.setId(id);
        helpItemMapper.update(item);
        return helpItemMapper.findById(id);
    }

    @Override
    public void delete(Long id) {
        HelpItem existing = helpItemMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "帮助项不存在");
        }
        helpItemMapper.deleteById(id);
    }
}
