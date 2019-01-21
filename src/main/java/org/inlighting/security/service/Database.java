package org.inlighting.security.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private Map<String, CustomUser> data;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Database() {
        data = new HashMap<>();

        CustomUser jack = new CustomUser(1, "jack", getPassword("jack123"), getGrants("ROLE_USER"));
        CustomUser danny = new CustomUser(2, "danny", getPassword("danny123"), getGrants("ROLE_EDITOR"));
        CustomUser alice = new CustomUser(3, "alice", getPassword("alice123"), getGrants("ROLE_REVIEWER"));
        CustomUser smith = new CustomUser(4, "smith", getPassword("smith123"), getGrants("ROLE_ADMIN"));
        data.put("jack", jack);
        data.put("danny", danny);
        data.put("alice", alice);
        data.put("smith", smith);
    }

    public Map<String, CustomUser> getDatabase() {
        return data;
    }

    private String getPassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    private Collection<GrantedAuthority> getGrants(String role) {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(role);
    }
}
