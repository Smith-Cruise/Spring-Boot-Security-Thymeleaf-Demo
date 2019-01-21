package org.inlighting.security.controller;

import org.inlighting.security.security.IsAdmin;
import org.inlighting.security.security.IsEditor;
import org.inlighting.security.security.IsReviewer;
import org.inlighting.security.security.IsUser;
import org.inlighting.security.service.CustomUser;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@IsUser
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/home")
    public String home(Model model) {
        CustomUser user = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "user/home";
    }

    @GetMapping("/editor")
    @IsEditor
    public String editor(Authentication authentication, Model model) {
        CustomUser user = (CustomUser)authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user/editor";
    }

    @GetMapping("/reviewer")
    @IsReviewer
    public String reviewer(Principal principal, Model model) {
        CustomUser user = (CustomUser) ((Authentication)principal).getPrincipal();
        model.addAttribute("user", user);
        return "user/reviewer";
    }

    @GetMapping("/admin")
    @IsAdmin
    public String admin() {
        return "user/admin";
    }
}
