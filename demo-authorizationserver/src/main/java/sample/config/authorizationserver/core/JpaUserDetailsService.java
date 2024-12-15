package sample.config.authorizationserver.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import sample.config.authorizationserver.entities.Authority;
import sample.config.authorizationserver.entities.User;
import sample.config.authorizationserver.repositories.AuthorityRepository;
import sample.config.authorizationserver.repositories.UserRepository;

import java.util.List;

@Component
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        System.out.println("----------> password: {}" + user.getPassword());

        List<Authority> authorities = authorityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Authorities not found: " + username));
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(grantedAuthorities)
                .build();
    }
}
