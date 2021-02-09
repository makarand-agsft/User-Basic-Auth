package com.user.auth.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
public class Swagger2Config {
        @Bean
        public Docket api() {
            ParameterBuilder aParameterBuilder = new ParameterBuilder();
            aParameterBuilder.name("Authorization")                 // name of header
                    .modelRef(new ModelRef("string"))
                    .parameterType("header");
            java.util.List<Parameter> aParameters = new ArrayList<>();
            aParameters.add(aParameterBuilder.build());
            return new Docket(DocumentationType.SWAGGER_2)  
              .select()                                  
              .apis(RequestHandlerSelectors.basePackage("com.user.auth.controller"))
              .paths(PathSelectors.any())
              .build().globalOperationParameters(aParameters);
        }
}
