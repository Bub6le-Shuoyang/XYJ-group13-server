package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.entity.HelpItem;
import com.xyj.xyjserver.mapper.HelpItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminHelpServiceImplTest {

    @Mock
    private HelpItemMapper helpItemMapper;

    @InjectMocks
    private AdminHelpServiceImpl adminHelpService;

    @Test
    void getAll_shouldDelegateToMapper() {
        HelpItem item = new HelpItem();
        item.setId(1L);
        item.setTitle("FAQ");
        when(helpItemMapper.findAll()).thenReturn(List.of(item));

        List<HelpItem> result = adminHelpService.getAll();

        assertEquals(1, result.size());
        assertEquals("FAQ", result.get(0).getTitle());
    }

    @Test
    void create_shouldGenerateHelpNoAndDefaultStatus() {
        HelpItem input = new HelpItem();
        input.setTitle("如何取件");
        input.setContent("凭取件码取件");

        HelpItem saved = new HelpItem();
        saved.setId(2L);
        saved.setHelpNo("HELP-123");
        saved.setTitle(input.getTitle());
        saved.setStatus(1);

        doAnswer(invocation -> {
            HelpItem item = invocation.getArgument(0);
            item.setId(2L);
            return 1;
        }).when(helpItemMapper).insert(any(HelpItem.class));
        when(helpItemMapper.findById(2L)).thenReturn(saved);

        HelpItem result = adminHelpService.create(input);

        ArgumentCaptor<HelpItem> captor = ArgumentCaptor.forClass(HelpItem.class);
        verify(helpItemMapper).insert(captor.capture());
        assertTrue(captor.getValue().getHelpNo().startsWith("HELP-"));
        assertEquals(1, captor.getValue().getStatus());
        assertEquals(2L, result.getId());
    }

    @Test
    void update_notFound_shouldThrow() {
        when(helpItemMapper.findById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> adminHelpService.update(99L, new HelpItem()));
        assertEquals(ResultCode.VALIDATE_FAILED.getCode(), ex.getErrorCode().getCode());
    }

    @Test
    void update_existing_shouldPersistChanges() {
        HelpItem existing = new HelpItem();
        existing.setId(1L);
        existing.setTitle("旧标题");
        when(helpItemMapper.findById(1L)).thenReturn(existing, existing);

        HelpItem update = new HelpItem();
        update.setTitle("新标题");
        update.setContent("新内容");

        HelpItem result = adminHelpService.update(1L, update);

        verify(helpItemMapper).update(update);
        assertEquals(1L, update.getId());
        assertNotNull(result);
    }

    @Test
    void delete_notFound_shouldThrow() {
        when(helpItemMapper.findById(5L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> adminHelpService.delete(5L));
    }

    @Test
    void delete_existing_shouldCallMapper() {
        HelpItem existing = new HelpItem();
        existing.setId(5L);
        when(helpItemMapper.findById(5L)).thenReturn(existing);

        adminHelpService.delete(5L);

        verify(helpItemMapper).deleteById(5L);
    }
}
