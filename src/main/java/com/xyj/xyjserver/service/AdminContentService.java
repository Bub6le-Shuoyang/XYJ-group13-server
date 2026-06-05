package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.vo.NewsPostVO;

import java.util.Map;

public interface AdminContentService {
    PageResult<NewsPostVO> getNews(Long page, Long size);
    NewsPostVO editNews(Long id, String title, String content, String tag, Boolean isUrgent);
    void deleteNews(Long id);
    PageResult<Map<String, Object>> getComments(Long page, Long size);
    void toggleCommentStatus(Long id, Integer status);
}
