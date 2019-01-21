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

        return new CustomUser(originUser.getId(), originUser.getUsername(), originUser.getPassword(), originUser.getAuthorities());
    }
}
