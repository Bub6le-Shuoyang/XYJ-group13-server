package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.service.UploadService;
import com.xyj.xyjserver.vo.UploadVO;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
@Tag(name = "Upload 接口")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传文件
     */
    @Operation(summary = "上传文件")
    @PostMapping
    public Result<UploadVO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "scene", required = false) String scene) {
        return Result.success(uploadService.uploadFile(file, scene));
    }
}