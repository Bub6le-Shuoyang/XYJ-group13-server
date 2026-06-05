package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.mapper.ContentMapper;
import com.xyj.xyjserver.service.AdminContentService;
import com.xyj.xyjserver.vo.NewsPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminContentServiceImpl implements AdminContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Override
    public PageResult<NewsPostVO> getNews(Long page, Long size) {
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 20L : size;
        Long offset = (safePage - 1) * safeSize;
        List<NewsPostVO> records = contentMapper.findNews(offset, safeSize);
        Long total = contentMapper.countNews();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public NewsPostVO editNews(Long id, String title, String content, String tag, Boolean isUrgent) {
        contentMapper.updateNews(id, title, content, tag, isUrgent);
        return null;
    }

    @Override
    public void deleteNews(Long id) {
        contentMapper.deleteNews(id);
    }

    @Override
    public PageResult<Map<String, Object>> getComments(Long page, Long size) {
        Long safePage = page == null || page < 1 ? 1L : page;
        Long safeSize = size == null || size < 1 ? 20L : size;
        long offset = (safePage - 1) * safeSize;
        List<Map<String, Object>> records = contentMapper.findAllComments(offset, safeSize);
        Long total = contentMapper.countAllComments();
        return new PageResult<>(records, total, safeSize, safePage);
    }

    @Override
    public void toggleCommentStatus(Long id, Integer status) {
        contentMapper.toggleCommentStatus(id, status);
    }
}
