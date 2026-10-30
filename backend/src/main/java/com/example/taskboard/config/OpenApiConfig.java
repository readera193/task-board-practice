package com.example.taskboard.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 文件設定：Swagger UI 在 /swagger-ui.html，規格在 /v3/api-docs。 先呼叫 POST /api/users/register 與 POST
 * /api/auth/login 取得 accessToken，再按 Authorize 貼上即可試用。
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Task Board API")
                                .version("0.0.1")
                                .description(
                                        "任務看板：註冊、登入（JWT）與任務 CRUD。任務依登入使用者隔離，"
                                                + "錯誤一律為 RFC 9457 Problem Details，code 欄位為穩定的錯誤代碼。"))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER_SCHEME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
