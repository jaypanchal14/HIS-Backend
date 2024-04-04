package org.his.security;

import org.his.entity.Login;
import org.his.repo.LoginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginService implements UserDetailsService {


    @Autowired
    private LoginRepo loginRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Login> userDetail = loginRepo.findByUserDetail(username);

        return userDetail.map(UserLoginDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database" + username));
    }
}