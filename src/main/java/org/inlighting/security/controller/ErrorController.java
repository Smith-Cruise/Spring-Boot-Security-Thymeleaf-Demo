package org.inlighting.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/404")
    public String handle404() {
        return "404";
    }

    @GetMapping("/403")
    public String handle403() {
        return "403";
    }

    @GetMapping("/500")
    public String handle500() {
        return "500";
    }
}
