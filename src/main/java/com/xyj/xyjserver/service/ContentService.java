package com.xyj.xyjserver.service;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CommentDTO;
import com.xyj.xyjserver.dto.NewsPostDTO;
import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;

public interface ContentService {
    PageResult<NewsPostVO> getNews(Long page, Long size);
    NewsPostVO publishNews(Long userId, NewsPostDTO postDTO);
    Boolean likeNews(Long userId, Long newsId);
    CommentVO commentNews(Long userId, Long newsId, CommentDTO commentDTO);
}