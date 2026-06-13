package com.xyj.xyjserver.common.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResultTest {

    @Test
    void constructor_shouldCalculatePages_whenTotalIsDivisibleBySize() {
        List<String> records = Arrays.asList("a", "b");
        PageResult<String> result = new PageResult<>(records, 20L, 10L, 1L);

        assertEquals(records, result.getRecords());
        assertEquals(20L, result.getTotal());
        assertEquals(10L, result.getSize());
        assertEquals(1L, result.getCurrent());
        assertEquals(2L, result.getPages());
    }

    @Test
    void constructor_shouldRoundUpPages_whenTotalHasRemainder() {
        PageResult<String> result = new PageResult<>(Collections.singletonList("x"), 21L, 10L, 2L);

        assertEquals(3L, result.getPages());
    }

    @Test
    void constructor_shouldReturnZeroPages_whenSizeIsZero() {
        PageResult<String> result = new PageResult<>(Collections.emptyList(), 5L, 0L, 1L);

        assertEquals(0L, result.getPages());
    }
}
