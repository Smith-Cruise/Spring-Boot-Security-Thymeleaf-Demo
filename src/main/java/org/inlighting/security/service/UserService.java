package org.inlighting.security.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Database database = new Database();

    public CustomUser getUserByUsername(String username) {
        CustomUser originUser = database.getDatabase().get(username);
        if (originUser == null) {
            return null;
        }

        /*
         * 此处有坑，之所以这么做是因为Spring Security获得到User后，会把User中的password字段置空，以确保安全。
         * 因为Java类是引用传递，为防止Spring Security修改了我们的源头数据，所以我们复制一个对象提供给Spring Security。
         * 如果通过真实数据库的方式获取，则没有这种问题需要担心。
          */
        return new CustomUser(originUser.getId(), originUser.getUsername(), originUser.getPassword(), originUser.getAuthorities());
    }
}
