package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.MallItem;
import com.xyj.xyjserver.mapper.MallItemMapper;
import com.xyj.xyjserver.service.AdminMallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminMallServiceImpl implements AdminMallService {

    @Autowired
    private MallItemMapper mallItemMapper;

    @Override
    public PageResult<MallItem> getItems(Long page, Long size, String keyword) {
        long safePage = page == null || page < 1 ? 1L : page;
        long safeSize = size == null || size < 1 ? 10L : size;
        long offset = (safePage - 1) * safeSize;

        List<MallItem> records;
        long total;
        if (keyword != null && !keyword.trim().isEmpty()) {
            records = mallItemMapper.searchByKeyword(keyword.trim(), offset, safeSize);
            total = mallItemMapper.countByKeyword(keyword.trim());
        } else {
            records = mallItemMapper.findAll(offset, safeSize);
            total = mallItemMapper.countAll();
        }
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public MallItem createItem(MallItem item) {
        item.setItemNo("ITEM-" + System.currentTimeMillis());
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        mallItemMapper.insert(item);
        return item;
    }

    @Override
    public MallItem updateItem(Long id, MallItem item) {
        MallItem existing = mallItemMapper.findByIdAny(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品不存在");
        }
        item.setId(id);
        mallItemMapper.update(item);
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        int rows = mallItemMapper.deleteById(id);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品不存在或已删除");
        }
    }

    @Override
    public void adjustStock(Long id, int delta) {
        int rows = mallItemMapper.adjustStock(id, delta);
        if (rows <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "库存调整失败，商品不存在或调整后库存为负");
        }
    }
}
