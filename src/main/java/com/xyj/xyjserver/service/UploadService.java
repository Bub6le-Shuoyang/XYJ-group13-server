package com.xyj.xyjserver.service;

import com.xyj.xyjserver.vo.UploadVO;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    UploadVO uploadFile(MultipartFile file, String scene);
}