package com.xyj.xyjserver.service.impl;

import com.xyj.xyjserver.service.UploadService;
import com.xyj.xyjserver.vo.UploadVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadServiceImpl implements UploadService {

    @Override
    public UploadVO uploadFile(MultipartFile file, String scene) {
        UploadVO vo = new UploadVO();
        // Mock upload logic
        vo.setUrl("/uploads/mock/" + file.getOriginalFilename());
        return vo;
    }
}