package com.joey.blog.dao;

import com.joey.blog.po.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

    @Query("from Blog b where b.recommend=true and b.published=true")
    List<Blog> findTop(Pageable pageable);

    @Query("from Blog b where b.published=true")
    Page<Blog> findByPublished(Pageable pageable);


    //select * from blog where title like '%内容%'
    @Query("from Blog b where (b.title like ?1 or b.description like ?1) and b.published=true")
//    @Query("from Blog b where b.title like ?1 or b.description like ?1")
    Page<Blog> findByQuery(String query, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update Blog b set b.views = b.views + 1 where b.id = ?1")
    int updateViews(Long id);


    @Query("select function('date_format', b.updateTime, '%Y') as year from Blog b where b.published = true group by year order by function('date_format', b.updateTime, '%Y') desc ")
    List<String> findGroupYear();

    @Query("select b from Blog b where function('date_format', b.updateTime, '%Y') = ?1 and b.published = true")
    List<Blog> findByYear(String year);

    @Query("select count(b.id) from Blog b where b.published = true")
    Long countPublishedBlog();
}
