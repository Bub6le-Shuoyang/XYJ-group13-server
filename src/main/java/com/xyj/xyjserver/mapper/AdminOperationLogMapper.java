package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.AdminOperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminOperationLogMapper {

    @Insert("INSERT INTO admin_operation_logs(admin_id, admin_name, operation, target_type, target_id, detail, ip, created_at) " +
            "VALUES(#{adminId}, #{adminName}, #{operation}, #{targetType}, #{targetId}, #{detail}, #{ip}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AdminOperationLog log);

    @Select("<script>" +
            "SELECT * FROM admin_operation_logs" +
            "<if test='operation != null and operation != \"\"'>" +
            " WHERE operation = #{operation}" +
            "</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
            "</script>")
    List<AdminOperationLog> findByPage(@Param("offset") long offset, @Param("size") long size, @Param("operation") String operation);

    @Select("<script>" +
            "SELECT COUNT(*) FROM admin_operation_logs" +
            "<if test='operation != null and operation != \"\"'>" +
            " WHERE operation = #{operation}" +
            "</if>" +
            "</script>")
    Long countByFilter(@Param("operation") String operation);

    @Select("SELECT * FROM admin_operation_logs WHERE admin_id = #{adminId} ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<AdminOperationLog> findByAdminId(@Param("adminId") Long adminId, @Param("offset") long offset, @Param("size") long size);
}
