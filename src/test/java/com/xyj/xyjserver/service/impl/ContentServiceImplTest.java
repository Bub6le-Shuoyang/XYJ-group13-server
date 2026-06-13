package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.dto.CommentDTO;
import com.xyj.xyjserver.dto.NewsPostDTO;
import com.xyj.xyjserver.mapper.ContentMapper;
import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceImplTest {

    @Mock
    private ContentMapper contentMapper;

    @InjectMocks
    private ContentServiceImpl contentService;

    @Test
    void getNews_shouldNormalizePageAndSize() {
        when(contentMapper.findNews(0L, 20L)).thenReturn(Collections.emptyList());
        when(contentMapper.countNews()).thenReturn(0L);

        PageResult<NewsPostVO> result = contentService.getNews(null, null);

        assertEquals(1L, result.getCurrent());
        assertEquals(20L, result.getSize());
        assertEquals(0L, result.getTotal());
    }

    @Test
    void publishNews_adminAuthor_shouldUseAdminStation() {
        NewsPostDTO dto = new NewsPostDTO();
        dto.setTitle("公告");
        dto.setContent("内容");
        dto.setTag("NOTICE");
        dto.setIsUrgent(true);

        NewsPostVO vo = new NewsPostVO();
        vo.setTitle("公告");

        when(contentMapper.findAdminStationId(1L)).thenReturn(5L);
        when(contentMapper.findNewsByPostNo(anyString())).thenReturn(vo);

        NewsPostVO result = contentService.publishNews(1L, "ADMIN", dto);

        verify(contentMapper).insertNews(anyString(), eq("公告"), eq("内容"), eq("NOTICE"),
                eq(1L), eq("ADMIN"), eq(5L), eq(true));
        assertEquals("公告", result.getTitle());
    }

    @Test
    void likeNews_shouldReturnTrueWhenUpdated() {
        when(contentMapper.increaseNewsLikes(3L)).thenReturn(1);

        assertTrue(contentService.likeNews(1L, 3L));
    }

    @Test
    void commentNews_whenMapperReturnsNull_shouldBuildFallbackVo() {
        CommentDTO dto = new CommentDTO();
        dto.setContent("写得不错");

        when(contentMapper.findLastInsertedComment()).thenReturn(null);

        CommentVO vo = contentService.commentNews(8L, 2L, dto);

        verify(contentMapper).insertComment(2L, 8L, "写得不错");
        assertEquals("写得不错", vo.getContent());
        assertEquals("用户8", vo.getAuthor());
        assertNotNull(vo.getTime());
    }
}
