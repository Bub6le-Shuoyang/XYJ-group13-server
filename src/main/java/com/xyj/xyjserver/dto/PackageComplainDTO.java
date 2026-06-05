package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "投诉包裹请求")
public class PackageComplainDTO {
    @Schema(description = "投诉原因", example = "包裹损坏", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "投诉原因不能为空")
    private String reason;

    @Schema(description = "投诉详细描述", example = "收到包裹时发现外包装严重破损，内部物品也有损坏...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "投诉描述不能为空")
    private String description;

    @Schema(description = "投诉图片URL列表")
    private List<String> images;
}
