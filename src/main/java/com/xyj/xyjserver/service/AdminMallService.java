package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.entity.MallItem;

public interface AdminMallService {
    PageResult<MallItem> getItems(Long page, Long size, String keyword);
    MallItem createItem(MallItem item);
    MallItem updateItem(Long id, MallItem item);
    void deleteItem(Long id);
    void adjustStock(Long id, int delta);
}
