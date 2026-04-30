package com.finconnect.auth_service.service;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.finconnect.auth_service.entity.Users;
import com.finconnect.auth_service.repository.UsersRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.emptyList()
        );
    }


}
