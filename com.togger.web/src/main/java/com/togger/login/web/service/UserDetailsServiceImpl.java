package com.togger.login.web.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.togger.login.web.dao.AppRoleDAO;
import com.togger.login.web.dao.AppUserDAO;
import com.togger.login.web.entity.AppUser;
import com.togger.login.web.social.SocialUserDetailsImpl;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
  
    @Autowired
    private AppUserDAO appUserDAO;
  
    @Autowired
    private AppRoleDAO appRoleDAO;
  
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
  
        System.out.println("UserDetailsServiceImpl.loadUserByUsername=" + userName);
  
        AppUser appUser = this.appUserDAO.findAppUserByUserName(userName);
  
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
  
        System.out.println("Found User: " + appUser);
  
        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = this.appRoleDAO.getRoleNames(appUser.getUserId());
  
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }
  
        SocialUserDetailsImpl userDetails = new SocialUserDetailsImpl(appUser, roleNames);
  
        return userDetails;
    }
  
}
