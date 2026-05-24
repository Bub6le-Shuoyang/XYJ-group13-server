package com.xyj.xyjserver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class PackageComplainDTO {
    @NotBlank(message = "投诉原因不能为空")
    private String reason;

    @NotBlank(message = "投诉描述不能为空")
    private String description;

    private List<String> images;
}