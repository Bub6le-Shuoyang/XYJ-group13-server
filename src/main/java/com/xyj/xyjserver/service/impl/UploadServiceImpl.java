package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.service.UploadService;
import com.xyj.xyjserver.vo.UploadVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Value("${xyj.upload.path:uploads/}")
    private String uploadPath;

    @Value("${xyj.upload.url-prefix:/uploads/}")
    private String urlPrefix;

    @Override
    public UploadVO uploadFile(MultipartFile file, String scene) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 生成唯一文件名，防止覆盖
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 构建存储目录 (支持相对路径和绝对路径)
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs(); // 如果目录不存在则创建
            }

            // 构建目标文件对象
            File dest = new File(directory.getAbsolutePath(), newFilename);
            
            // 将文件保存到本地磁盘
            file.transferTo(dest);

            // 拼接访问URL并返回
            UploadVO vo = new UploadVO();
            String accessUrl = urlPrefix + newFilename;
            vo.setUrl(accessUrl);
            return vo;

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}