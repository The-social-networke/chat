package com.socialnetwork.chat.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Hidden
@RestController
@RequestMapping
public class SwaggerSocketController {

    @GetMapping("swagger-socket-ui.html")
    public ModelAndView renderFooList() {
        return new ModelAndView("socket_docs");
    }
}
