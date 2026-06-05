package com.xyj.xyjserver.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("乡驿家（XYJ）API 文档")
                        .version("1.0.0")
                        .description("乡村驿站末端配送管理系统 — 接口文档。支持包裹管理、配送任务、用户管理、积分商城等功能。")
                        .contact(new Contact().name("XYJ Team")))
                .addSecurityItem(new SecurityRequirement().addList("BearerToken"))
                .components(new Components()
                        .addSecuritySchemes("BearerToken",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Token，格式：Bearer <token>")));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("1-认证模块")
                .pathsToMatch("/api/v1/auth/**", "/api/v1/admin/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("2-管理后台")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("3-用户模块")
                .pathsToMatch("/api/v1/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi courierApi() {
        return GroupedOpenApi.builder()
                .group("4-配送员模块")
                .pathsToMatch("/api/v1/courier/**")
                .build();
    }

    @Bean
    public GroupedOpenApi contentApi() {
        return GroupedOpenApi.builder()
                .group("5-资讯与内容")
                .pathsToMatch("/api/v1/content/**")
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("6-公共服务")
                .pathsToMatch("/api/v1/stations/**", "/api/v1/sys/**", "/api/v1/upload/**")
                .build();
    }
}
