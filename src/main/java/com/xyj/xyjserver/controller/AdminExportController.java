package com.xyj.xyjserver.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.entity.User;
import com.xyj.xyjserver.mapper.AdminPackageMapper;
import com.xyj.xyjserver.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/export")
@Tag(name = "AdminExport 数据导出接口")
public class AdminExportController {

    @Autowired
    private AdminPackageMapper adminPackageMapper;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "导出包裹数据为Excel")
    @GetMapping("/packages")
    public void exportPackages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireAdmin(request);

        List<Map<String, Object>> packages = adminPackageMapper.findAllPackagesForExport();

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.addHeaderAlias("package_no", "包裹单号");
        writer.addHeaderAlias("name", "物品名称");
        writer.addHeaderAlias("receiver_name", "收件人");
        writer.addHeaderAlias("receiver_phone", "联系电话");
        writer.addHeaderAlias("address", "配送地址");
        writer.addHeaderAlias("status", "状态");
        writer.addHeaderAlias("created_at", "创建时间");

        // Write header row
        writer.writeHeadRow(List.of("包裹单号", "物品名称", "收件人", "联系电话", "配送地址", "状态", "创建时间"));

        // Write data rows
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String, Object> pkg : packages) {
            List<Object> row = new ArrayList<>();
            row.add(pkg.get("package_no"));
            row.add(pkg.get("name"));
            row.add(pkg.get("receiver_name"));
            row.add(pkg.get("receiver_phone"));
            row.add(pkg.get("address"));
            row.add(pkg.get("status"));
            Object createdAt = pkg.get("created_at");
            row.add(createdAt != null ? sdf.format(createdAt) : "");
            writer.writeRow(row);
        }

        setExcelResponseHeaders(response, "packages.xlsx");
        writer.flush(response.getOutputStream());
        writer.close();
    }

    @Operation(summary = "导出用户数据为Excel")
    @GetMapping("/users")
    public void exportUsers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requireAdmin(request);

        List<User> users = userMapper.findAll();

        ExcelWriter writer = ExcelUtil.getWriter(true);

        // Write header row
        writer.writeHeadRow(List.of("用户编号", "邮箱", "手机号", "昵称", "状态", "注册时间"));

        // Write data rows
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (User user : users) {
            List<Object> row = new ArrayList<>();
            row.add(user.getUserNo());
            row.add(user.getEmail());
            row.add(user.getPhone());
            row.add(user.getNickname());
            row.add(user.getStatus() != null && user.getStatus() == 1 ? "正常" : "禁用");
            row.add(user.getCreatedAt() != null ? sdf.format(user.getCreatedAt()) : "");
            writer.writeRow(row);
        }

        setExcelResponseHeaders(response, "users.xlsx");
        writer.flush(response.getOutputStream());
        writer.close();
    }

    private void setExcelResponseHeaders(HttpServletResponse response, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFilename);
    }

    private void requireAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
