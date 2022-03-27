package com.joey.blog.service;

import com.joey.blog.dao.UserRepository;
import com.joey.blog.po.User;
import com.joey.blog.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findAllByUsernameAndPassword(username, MD5Utils.code(password));
        return user;
    }

}
