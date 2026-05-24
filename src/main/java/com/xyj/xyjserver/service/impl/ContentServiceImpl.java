package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CommentDTO;
import com.xyj.xyjserver.dto.NewsPostDTO;
import com.xyj.xyjserver.service.ContentService;
import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class ContentServiceImpl implements ContentService {

    @Override
    public PageResult<NewsPostVO> getNews(Long page, Long size) {
        NewsPostVO vo = new NewsPostVO();
        vo.setId(1L);
        vo.setTitle("生鲜包裹优先配送");
        vo.setContent("今天驿站生鲜包裹优先配送");
        vo.setTag("驿站动态");
        vo.setIsUrgent(false);
        vo.setPublishTime(new Date());
        vo.setLikes(15);
        vo.setCommentsCount(5);
        return new PageResult<>(Collections.singletonList(vo), 1L, size, page);
    }

    @Override
    public NewsPostVO publishNews(Long userId, NewsPostDTO postDTO) {
        NewsPostVO vo = new NewsPostVO();
        vo.setId(System.currentTimeMillis());
        vo.setTitle(postDTO.getTitle());
        vo.setContent(postDTO.getContent());
        vo.setTag(postDTO.getTag());
        vo.setIsUrgent(postDTO.getIsUrgent());
        vo.setPublishTime(new Date());
        vo.setLikes(0);
        vo.setCommentsCount(0);
        return vo;
    }

    @Override
    public Boolean likeNews(Long userId, Long newsId) {
        return true;
    }

    @Override
    public CommentVO commentNews(Long userId, Long newsId, CommentDTO commentDTO) {
        CommentVO vo = new CommentVO();
        vo.setId(System.currentTimeMillis());
        vo.setContent(commentDTO.getContent());
        vo.setAuthor("用户" + userId);
        vo.setTime(new Date());
        return vo;
    }
}