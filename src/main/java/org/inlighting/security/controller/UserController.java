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

@IsUser // 表明该控制器下所有请求都需要登入后才能访问
@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/home")
    public String home(Model model) {
        // 方法一：通过SecurityContextHolder获取
        CustomUser user = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        return "user/home";
    }

    @GetMapping("/editor")
    @IsEditor
    public String editor(Authentication authentication, Model model) {
        // 方法二：通过方法注入的形式获取Authentication
        CustomUser user = (CustomUser)authentication.getPrincipal();
        model.addAttribute("user", user);
        return "user/editor";
    }

    @GetMapping("/reviewer")
    @IsReviewer
    public String reviewer(Principal principal, Model model) {
        // 方法三：同样通过方法注入的方法，注意要转型，此方法很二，不推荐
        CustomUser user = (CustomUser) ((Authentication)principal).getPrincipal();
        model.addAttribute("user", user);
        return "user/reviewer";
    }

    @GetMapping("/admin")
    @IsAdmin
    public String admin() {
        // 方法四：通过Thymeleaf的Security标签进行，详情见admin.html
        return "user/admin";
    }
}
