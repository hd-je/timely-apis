package io.github.timely.timelyapi.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .servers(
                listOf(
                    Server()
                        .url("/")
                        .description("Current server")
                )
            )
            .info(
                Info()
                    .title("Timely API")
                    .description("Timely project management API")
                    .version("v1")
            )
    }
}
