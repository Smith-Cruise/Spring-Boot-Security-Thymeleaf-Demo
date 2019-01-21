package org.inlighting.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

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
