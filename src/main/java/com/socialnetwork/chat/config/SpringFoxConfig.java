package com.socialnetwork.chat.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.Map;

@Configuration
@Import(BeanValidatorPluginsConfiguration.class)
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.socialnetwork.chat"))
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(Lists.newArrayList(apiKey()))
            .securityContexts(Lists.newArrayList(securityContext()))
            .useDefaultResponseMessages(false)
            .globalResponseMessage(RequestMethod.GET, List.of(
                new ResponseMessage(401, "unauthorized", null, Map.of(), List.of()),
                new ResponseMessage(403, "forbidden", null, Map.of(), List.of())
            ));
    }

    private static ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Chat service")
            .description ("Chat docks")
            .version("1.0")
            .build();
    }


    @Bean
    SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
    }

    static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
            = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(
            new SecurityReference("JWT", authorizationScopes));
    }

    private static ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}
