package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.vo.UploadVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {

    @InjectMocks
    private UploadServiceImpl uploadService;

    private Path tempUploadDir;

    @BeforeEach
    void setUp() throws IOException {
        tempUploadDir = Files.createTempDirectory("xyj-upload-test");
        ReflectionTestUtils.setField(uploadService, "uploadPath", tempUploadDir.toString() + "/");
        ReflectionTestUtils.setField(uploadService, "urlPrefix", "/uploads/");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempUploadDir != null && Files.exists(tempUploadDir)) {
            Files.walk(tempUploadDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void uploadFile_emptyFile_shouldThrow() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> uploadService.uploadFile(empty, "avatar"));
        assertTrue(ex.getMessage().contains("不能为空"));
    }

    @Test
    void uploadFile_validFile_shouldSaveAndReturnUrl() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.png", "image/png", "fake-image".getBytes());

        UploadVO vo = uploadService.uploadFile(file, "avatar");

        assertNotNull(vo.getUrl());
        assertTrue(vo.getUrl().startsWith("/uploads/"));
        assertTrue(vo.getUrl().endsWith(".png"));
    }
}
