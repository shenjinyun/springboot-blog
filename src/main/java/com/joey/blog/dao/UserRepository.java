package com.joey.blog.dao;

import com.joey.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findAllByUsernameAndPassword(String username, String password);
}
