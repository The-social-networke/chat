package com.socialnetwork.chat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
@RequestMapping
public class SwaggerSocketController {

    @GetMapping("swagger-socket-ui.html")
    public ModelAndView renderFooList() {
        return new ModelAndView("socket_docs");
    }
}
