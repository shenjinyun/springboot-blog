package com.joey.blog.dao;

import com.joey.blog.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {

    Type findByName(String name);

    @Query("from Type t") //需要加上published这个属性
    List<Type> findTop(Pageable pageable);
}
