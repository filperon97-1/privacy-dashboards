package com.privacydashboard.application.security;

import com.privacydashboard.application.data.entity.User;
import com.privacydashboard.application.data.service.UserRepository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                    getAuthorities(user));
        }
    }

    /*private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

    }*/

    // Per com'è fatto, c'è solo un elemento Authority (User può essere solo uno tra Subject, Controller e DPO)
    // springframework...User però vuole una lista
    private static List<GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> list=new LinkedList<>();
        list.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return list;

    }

    public boolean registerUser(User user){
        userRepository.save(user);
        return true;
    }

}
