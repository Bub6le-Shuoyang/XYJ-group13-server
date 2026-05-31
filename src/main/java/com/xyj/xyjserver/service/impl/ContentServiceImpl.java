package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CommentDTO;
import com.xyj.xyjserver.dto.NewsPostDTO;
import com.xyj.xyjserver.mapper.ContentMapper;
import com.xyj.xyjserver.service.ContentService;
import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

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
    public NewsPostVO publishNews(Long userId, String role, NewsPostDTO postDTO) {
        String authorType = "ADMIN".equals(role) ? "ADMIN" : "USER";
        Long stationId = findStationId(authorType, userId);
        String postNo = "NEWS-" + System.currentTimeMillis();
        contentMapper.insertNews(
                postNo,
                postDTO.getTitle(),
                postDTO.getContent(),
                postDTO.getTag(),
                userId,
                authorType,
                stationId,
                Boolean.TRUE.equals(postDTO.getIsUrgent())
        );
        return contentMapper.findNewsByPostNo(postNo);
    }

    @Override
    public Boolean likeNews(Long userId, Long newsId) {
        return contentMapper.increaseNewsLikes(newsId) > 0;
    }

    @Override
    public CommentVO commentNews(Long userId, Long newsId, CommentDTO commentDTO) {
        contentMapper.insertComment(newsId, userId, commentDTO.getContent());
        CommentVO vo = contentMapper.findLastInsertedComment();
        if (vo == null) {
            vo = new CommentVO();
            vo.setContent(commentDTO.getContent());
            vo.setAuthor("用户" + userId);
            vo.setTime(new Date());
        }
        return vo;
    }

    private Long findStationId(String authorType, Long userId) {
        if ("ADMIN".equals(authorType)) {
            Long stationId = contentMapper.findAdminStationId(userId);
            if (stationId != null) {
                return stationId;
            }
        }
        return contentMapper.findDefaultStationId();
    }
}
