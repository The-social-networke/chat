package com.socialnetwork.chat.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Chat", version = "v1"))
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class SpringFoxConfig {

//    @Bean
//    public OperationCustomizer customize() {
//        return (operation, handlerMethod) -> operation.addParametersItem(
//            new Parameter()
//                .in("header")
//                .required(true)
//                .description("myCustomHeader")
//                .name("myCustomHeader"));
//    }
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//            .select()
//            .apis(RequestHandlerSelectors.basePackage("com.socialnetwork.chat"))
//            .paths(PathSelectors.any())
//            .build()
//            .securitySchemes(List.of(apiKey()))
//            .securityContexts(List.of(securityContext()))
//            .useDefaultResponseMessages(false)
//            .globalResponseMessage(RequestMethod.GET, List.of(
//                new ResponseMessage(401, "unauthorized", null, Map.of(), List.of()),
//                new ResponseMessage(403, "forbidden", null, Map.of(), List.of())
//            ));
//    }
//
//    private static ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//            .title("Chat service")
//            .description ("Chat docks")
//            .version("1.0")
//            .build();
//    }
//
//
//    @Bean
//    SecurityContext securityContext() {
//        return SecurityContext.builder()
//            .securityReferences(defaultAuth())
//            .forPaths(PathSelectors.any())
//            .build();
//    }
//
//    static List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope
//            = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return List.of(
//            new SecurityReference("JWT", authorizationScopes));
//    }
//
//    private static ApiKey apiKey() {
//        return new ApiKey("JWT", "Authorization", "header");
//    }
}
