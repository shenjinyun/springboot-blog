package com.joey.blog.service;

import com.joey.blog.po.Blog;
import com.joey.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {

    Blog getBlog(Long id);

    Blog getAndConvert(Long id);

    Page<Blog> listSearchBlog(Pageable pageable, BlogQuery blogQuery);

    Page<Blog> listSearchBlog(Pageable pageable, String query);

    Page<Blog> listPublishedBlog(Pageable pageable);

    List<Blog> listRecommendBlogTop(Integer size);

    Page<Blog> listTagBlog(Pageable pageable, Long tagId);

    Map<String, List<Blog>> archiveBlog();

    Long countBlog();

    Blog saveBlog(Blog blog);

    Blog updateBlog(Long id, Blog blog);

    void deleteBlog(Long id);
}
