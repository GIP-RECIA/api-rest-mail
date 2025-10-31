package fr.recia.widget.api.apiRestMail.config.custom.impl;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class UserCustomImplementation extends User {
    public UserCustomImplementation(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(username, password, authorities);
        this.setAttributes(attributes);
    }

    public UserCustomImplementation(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.setAttributes(attributes);
    }

    Map<String, Object> attributes;

    @Override
    public String toString() {
        return "UserCustomImplementation{" +
                "attributes=" + attributes +
                '}';
    }
}
