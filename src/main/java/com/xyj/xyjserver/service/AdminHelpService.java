package com.xyj.xyjserver.service;

import com.xyj.xyjserver.entity.HelpItem;

import java.util.List;

public interface AdminHelpService {
    List<HelpItem> getAll();
    HelpItem create(HelpItem item);
    HelpItem update(Long id, HelpItem item);
    void delete(Long id);
}
