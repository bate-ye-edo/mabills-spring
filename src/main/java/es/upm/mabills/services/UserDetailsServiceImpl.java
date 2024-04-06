package es.upm.mabills.services;

import es.upm.mabills.model.User;
import es.upm.mabills.persistence.UserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static es.upm.mabills.services.JwtService.ROLE_USER;

@Service
@Transactional
@Qualifier("users")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserPersistence userPersistence;

    @Autowired
    public UserDetailsServiceImpl(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) {
        User user = userPersistence.findUserByUsername(username);
        return this.userBuilder(user.getUsername(), user.getPassword());
    }

    private org.springframework.security.core.userdetails.User userBuilder(String username, String password) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        return new org.springframework.security.core.userdetails.User(username, password, true, true,
                true, true, authorities);
    }
}
