package com.xyj.xyjserver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "管理员创建/编辑站点请求")
public class CreateStationDTO {
    @Schema(description = "站点编号", example = "ST-002", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "站点编号不能为空")
    private String stationNo;

    @Schema(description = "站点名称", example = "清河村驿站", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "站点名称不能为空")
    private String name;

    @Schema(description = "站点地址", example = "清河村村委会旁", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "站点地址不能为空")
    private String address;

    @Schema(description = "纬度", example = "39.954300")
    private BigDecimal lat;

    @Schema(description = "经度", example = "116.345200")
    private BigDecimal lng;

    @Schema(description = "联系电话", example = "0319-1234567")
    private String phone;

    @Schema(description = "营业时间", example = "08:00-18:00")
    private String openingHours;
}
