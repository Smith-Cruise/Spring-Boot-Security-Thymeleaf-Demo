# Spring-Boot-Security-Thymeleaf-Demo
因为有一个项目需采用MVC构架，所以学习了Spring Security并记录下来，希望大家一起学习提供意见

GitHub地址：[https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo]( https://github.com/Smith-Cruise/Spring-Boot-Shiro)

**如果有疑问，请在GitHub中发布issue，我有空会为大家解答的**

本项目基于Spring Boot 2 + Spring Security 5 + Thymeleaf 2 + JDK11（你也可以用8，应该区别不大）

实现了以下功能：

* 基于注解的权限控制
* 在Thymeleaf中使用Security的标签
* 自定义权限注解
* 记住密码功能

如果需要前后端分离的安全框架搭建教程可以参考：[Shiro+JWT+Spring Boot Restful简易教程]( https://github.com/Smith-Cruise/Spring-Boot-Shiro)

## 目录

[TOC]



## 项目演示

如果想要直接体验，直接`clone`项目，运行`mvn spring-boot:run`命令即可进行访问。网址规则自行看教程后面。

**首页**

![首页](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/index.png?raw=true)

**登入**

![登入](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/login.png?raw=true)

**登出**

![登出](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/logout.png?raw=true)

**Home页面**

![登出](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/home.png?raw=true)

**Admin页面**

![登出](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/admin.png?raw=true)

**403无权限页面**

![登出](https://github.com/Smith-Cruise/Spring-Boot-Security-Thymeleaf-Demo/blob/master/file/403.png?raw=true)

## Spring Security 基本原理

###### Spring Security 过滤器链

Spring Security实现了一系列的过滤器链，就按照下面顺序一个一个执行下去。

1. `....class`一些自定义过滤器（在配置的时候你可以自己选择插到哪个过滤器之前），因为这个需求因人而异，本文不探讨，大家可以自己研究
2. `UsernamePasswordAithenticationFilter.class`Spring Security 自带的表单登入验证过滤器，也是本文主要使用的过滤器
3. `BasicAuthenticationFilter.class`
4. `ExceptionTranslation.class` 异常解释器
5. `FilterSecurityInterceptor.class` 拦截器最终决定请求能否通过
6. `Controller` 我们最后的控制器

###### 相关类说明

* `User.class` 注意这个类不是我们自己写的，而是Spring Security官方提供的，他提供了一些基础的功能，我们可以通过继承这个类来扩充方法。详见代码中的`CustomUser.java`
* `UserDetailsService` 也是Spring Security官方提供的一个接口，里面只有一个方法`loadUserByUsername()` ，Spring Security会调用这个方法来获取数据库中存在的数据，然后和用户POST的用户名密码进行比对，从而判断用户的用户名密码是否正确。所以我们需要自己实现`loadUserByUsername()`这个方法。详见代码中的`CustomUserDetailsService.java`。

## 项目逻辑

为了体现权限区别，我们通过HashMap构造了一个数据库，里面包含了4个用户

| ID   | 用户名 | 密码     | 权限     |
| ---- | ------ | -------- | -------- |
| 1    | jack   | jack123  | user     |
| 2    | danny  | danny123 | editor   |
| 3    | alice  | alice123 | reviewer |
| 4    | smith  | smith123 | admin    |

先说明下权限，`user`是最基础的权限，只要是登入用户就算有了`user`权限，`editor`是在`user`的权限上面增加了`editor`的权限，`reviewer`同理，`editor`和`reviewer`属于同一级的权限，`admin`则包含所有权限。获取用户通过我们编些的`userService.getUserByUsername()` 方法。

为了检验权限，我们提供若干个页面

| 网址           | 说明                                | 可访问权限                  |
| -------------- | ----------------------------------- | --------------------------- |
| /              | 首页                                | 所有人均可访问（anonymous） |
| /login         | 登入页面                            | 所有人均可访问（anonymous） |
| /logout        | 退出页面                            | 所有人均可访问（anonymous） |
| /user/home     | 用户中心                            | user                        |
| /user/editor   |                                     | editor, admin               |
| /user/reviewer |                                     | reviewer, admin             |
| /user/admin    |                                     | admin                       |
| /403           | 403错误页面，美化过，大家可以直接用 | 所有人均可访问（anonymous） |
| /404           | 404错误页面，美化过，大家可以直接用 | 所有人均可访问（anonymous） |
| /500           | 500错误页面，美化过，大家可以直接用 | 所有人均可访问（anonymous） |

## 代码配置

###### Maven 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.1.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>org.inlighting</groupId>
    <artifactId>security-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>security-demo</name>
    <description>Demo project for Spring Boot &amp; Spring Security</description>

    <!--指定JDK版本，大家可以改成自己的-->
    <properties>
        <java.version>11</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--对Thymeleaf添加Spring Security标签支持-->
        <dependency>
            <groupId>org.thymeleaf.extras</groupId>
            <artifactId>thymeleaf-extras-springsecurity5</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!--开发的热加载配置-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

###### application.properties配置

为了使热加载（这样修改模板后无需重启Tomcat）生效，我们需要在Spring Boot的配置文件上面加上一段话

```properties
spring.thymeleaf.cache=false
```

如果需要详细了解热加载，请看官方文档：[https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-hotswapping](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-hotswapping)

## Spring Security 配置

首先我们开启方法注解支持：只需要在类上添加`@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)`注解，我们设置`prePostEnabled = true`是为了支持`hasRole()`这类表达式。如果想进一步了解方法注解可以看 [Introduction to Spring Method Security](https://www.baeldung.com/spring-security-method-security) 这篇文章。

###### SecurityConfig.java

```java
/**
 * 开启方法注解支持，我们设置prePostEnabled = true是为了后面能够使用hasRole()这类表达式
 * 进一步了解可看教程：https://www.baeldung.com/spring-security-method-security
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * TokenBasedRememberMeServices的生成密钥，
     * 算法实现详见文档：https://docs.spring.io/spring-security/site/docs/5.1.3.RELEASE/reference/htmlsingle/#remember-me-hash-token
     */
    private final String SECRET_KEY = "123456";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * 必须有此方法，Spring Security官方规定必须要有一个密码加密方式。
     * 注意：例如这里用了BCryptPasswordEncoder()的加密方法，那么在保存用户密码的时候也必须使用这种方法，确保前后一致。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置Spring Security，下面说明几点注意事项。
     * 1. Spring Security 默认是开启了CSRF的，此时我们提交的POST表单必须有隐藏的字段来传递CSRF，
     * 而且在logout中，我们必须通过POST到 /logout 的方法来退出用户，详见我们的login.html和logout.html.
     * 2. 开启了rememberMe()功能后，如果我们自定义了rememberMeServices()方法，例如下面，我们只能在
     * TokenBasedRememberMeServices中设置cookie名称、过期时间等相关配置,
     * 如果如 .and().rememberMe().rememberMeServices(getRememberMeServices()).rememberMeCookieName("cookie-name")这样
     * 程序会报错。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login") // 自定义用户登入页面
                .failureUrl("/login?error") // 自定义登入失败页面，前端可以通过url中是否有error来提供友好的用户登入提示
                .and()
                .logout()
                .logoutUrl("/logout")// 自定义用户登出页面
                .logoutSuccessUrl("/")
                .and()
                .rememberMe() // 开启记住密码功能
                .rememberMeServices(getRememberMeServices())
                .key(SECRET_KEY) // 此SECRET需要和生成密钥的Token相同
                .and()
                /*
                 * 默认允许所有路径所有人都可以访问，确保静态资源的正常访问。
                 * 后面再通过方法注解的方式来控制权限。
                 */
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403"); // 权限不足自动跳转403
    }

    /**
     * 如果要设置cookie过期时间或其他相关配置，请在下方自行配置
     */
    private TokenBasedRememberMeServices getRememberMeServices() {
        TokenBasedRememberMeServices services = new TokenBasedRememberMeServices(SECRET_KEY, customUserDetailsService);
        services.setCookieName("remember-cookie");
        services.setTokenValiditySeconds(100); // 默认14天
        return services;
    }
}
```

######  UserService.java

自己模拟数据库操作的`Service`，用于向自己通过`HashMap`模拟的数据源获取数据。

```java
@Service
public class UserService {

    private Database database = new Database();

    public CustomUser getUserByUsername(String username) {
        CustomUser originUser = database.getDatabase().get(username);
        if (originUser == null) {
            return null;
        }

        /*
         * 此处有坑，之所以这么做是因为Spring Security获得到User后，会把User中的password字段置空，以确保安全，
         * 为了防止Spring Security修改了我们的源头数据，所以我们复制一个对象提供给Spring Security。如果通过数据库
         * 方式获取，则没有这种问题需要担心。
          */
        return new CustomUser(originUser.getId(), originUser.getUsername(), originUser.getPassword(), originUser.getAuthorities());
    }
}
```

###### CustomUserDetailsService.java

```java
/**
 * 实现官方提供的UserDetailsService接口即可
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = userService.getUserByUsername(username);
        if (user == null) {
            throw new  UsernameNotFoundException("该用户不存在");
        }
        LOGGER.info("用户名："+username+" 角色："+user.getAuthorities().toString());
        return user;
    }
}
```



## 自定义权限注解

我们在开发网站的过程中，比如 `GET /user/editor `这个请求角色为`EDITOR`和`ADMIN`肯定都可以，如果我们在每一个这样的方法上面写一长串的表达式判断，一定很复杂。但是通过自定义权限注解，我们可以通过`@IsEditor`这样的方法来判断，这样一来就简单了很多。进一步了解可以看：[Introduction to Spring Method Security](https://www.baeldung.com/spring-security-method-security)

###### IsUser.java

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_EDITOR', 'ROLE_REVIEWER', 'ROLE_ADMIN')")
public @interface IsUser {
}
```

###### IsEditor.java

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_EDITOR', 'ROLE_ADMIN')")
public @interface IsEditor {
}
```

###### IsReviewer.java

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_REVIEWER', 'ROLE_ADMIN')")
public @interface IsReviewer {
}
```

###### IsAdmin.java

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
public @interface IsAdmin { 
}
```

###### Spring Security自带表达式

- `hasRole()`，是否拥有某一个权限

-  `hasAnyRole()`，多个权限中有一个即可，如`hasAnyRole("ADMIN","USER")`
- `hasAuthority()`，`Authority`和`Role`很像，唯一的区别就是Authority前缀多了`ROLE_`，如`hasAuthority("ROLE_ADMIN")`等价于`hasRole("ADMIN")`
-  `hasAnyAuthority()`，同上，多个权限中有一个即可
- `permitAll()`, `denyAll()`,`isAnonymous()`, `isRememberMe()`，通过字面意思可以理解
- `isAuthenticated()`, `isFullyAuthenticated()`，这两个区别就是`isFullyAuthenticated()`对认证的安全要求更高。例如用户通过记住密码功能登入到系统进行敏感操作，`isFullyAuthenticated()`会返回`false`，此时我们让用户再一次输入密码确保安全，而`isAuthenticated()`只要是登入用户均返回`true`。
- `principal()`, `authentication()`，例如我们想获取登入用户的id，可以通过`principal()`得到的`Object`获取，实际上`principal()`获取的`Object`基本上可以等同我们自己编写的`CustomUser`。而`authentication()`得到的`Authentication`是`Principal的父类`，相关操作可看`Authentication`的源码。进一步了解可以看后面*获取用户数据*
- `hasPermission()`，参考字面意思即可

如果想进一步了解，可以参考[Intro to Spring Security Expressions](https://www.baeldung.com/spring-security-expressions)

## 添加Thymeleaf支持

我们通过`thymeleaf-extras-springsecurity`来添加Thymeleaf对Spring Security的支持。

###### Maven配置

```xml
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity5</artifactId>
</dependency>
```

###### 使用例子

注意我们在html中添加了`xmlns:sec`的支持

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <title>Admin</title>
</head>
<body>
<p>This is a home page.</p>
<p>Id: <th:block sec:authentication="principal.id"></th:block></p>
<p>Username: <th:block sec:authentication="principal.username"></th:block></p>
<p>Role: <th:block sec:authentication="principal.authorities"></th:block></p>
</body>
</html>
```

如果想进一步了解请看文档 [thymeleaf-extras-springsecurity](https://github.com/thymeleaf/thymeleaf-extras-springsecurity)

## Controller的编写

###### IndexController.java

```java
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index/index";
    }

    @GetMapping("/login")
    public String login() {
        return "index/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "index/logout";
    }
}
```

###### UserController.java

在这个控制器中，我综合展示了自定义注解的使用和4种获取用户信息的方式

```java
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
```

## Html的编写

在编写html的时候，基本上就是大同小异了，就是注意一点，**如果开启了CSRF，在编写表单POST请求的时候添加上隐藏字段，如`<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>`，不过大家其实不用加也没事，因为Thymeleaf自动会加上去的:)**

## 总结

教程粗糙，欢迎指正！

如需深入了解，推荐看看 [Security with Spring](https://www.baeldung.com/security-spring)