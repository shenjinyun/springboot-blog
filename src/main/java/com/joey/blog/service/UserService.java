package com.joey.blog.service;

import com.joey.blog.po.User;

public interface UserService {
    User checkUser(String username, String password);
}
