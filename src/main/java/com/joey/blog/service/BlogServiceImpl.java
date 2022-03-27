package com.joey.blog.service;

import com.joey.blog.NotFindException;
import com.joey.blog.dao.BlogRepository;
import com.joey.blog.po.Blog;
import com.joey.blog.po.Type;
import com.joey.blog.util.MarkdownUtils;
import com.joey.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;


@Service
public class BlogServiceImpl implements BlogService{

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.getById(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.getById(id);
        if(blog == null) {
            throw new NotFindException("该博客不存在！");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        blogRepository.updateViews(id);

        return b;
    }

    @Override
    public Page<Blog> listSearchBlog(Pageable pageable, BlogQuery blogQuery) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!"".equals(blogQuery.getTitle()) && blogQuery.getTitle() != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%" + blogQuery.getTitle()));
                }
                if(blogQuery.getTypeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blogQuery.getTypeId()));
                }

                if(blogQuery.isPublished()) {
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), true));
                }

                if(blogQuery.isRecommend()) {
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), true));
                }
//                if(blogQuery.isRecommend() && blogQuery.isPublished()) {
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), true));
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), true));
//                } else if(blogQuery.isRecommend() && !blogQuery.isPublished()) {
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), true));
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), false));
//                } else if(!blogQuery.isRecommend() && blogQuery.isPublished()) {
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), false));
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), "true"));
//                } else {
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), "false"));
//                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), "false"));
//                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listSearchBlog(Pageable pageable, String query) {
        return blogRepository.findByQuery(query, pageable);
    }

    @Override
    public Page<Blog> listPublishedBlog(Pageable pageable) {
        return blogRepository.findByPublished(pageable);
    }


    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Page<Blog> listTagBlog(Pageable pageable, Long tagId) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Join join = root.join("tags");
                //root是blog表
                predicates.add(criteriaBuilder.equal(join.get("id"), tagId));
                predicates.add(criteriaBuilder.equal(root.<Boolean>get("published"), true));
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();

        Map<String, List<Blog>> map = new LinkedHashMap<>();
        for(String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.countPublishedBlog();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if(blog.getId() == null) { //新增
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else { //修改
            blog.setUpdateTime(new Date());
        }

        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.getById(id);
        if(b == null) {
            throw new NotFindException("该博客不存在！");
        }
        BeanUtils.copyProperties(blog, b);
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
