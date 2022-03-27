package com.joey.blog.web;

import com.joey.blog.NotFindException;
import com.joey.blog.po.Blog;
import com.joey.blog.po.Tag;
import com.joey.blog.po.Type;
import com.joey.blog.service.BlogService;
import com.joey.blog.service.TagService;
import com.joey.blog.service.TypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    private Comparator<Type> typeComparator = new Comparator<Type>() {
        @Override
        public int compare(Type o1, Type o2) {
            return o2.getBlogs().size() - o1.getBlogs().size();
        }
    };

    private List<Type> getPublishedTypes(List<Type> types) {
        List<Type> publishedTypes = new ArrayList<>();
        if(types == null || types.size() == 0) {
            return publishedTypes;
        }

        for(Type t : types) {
            Type publishedType = new Type();
            List<Blog> publishedBlogs = new ArrayList<>();
            for(Blog b : t.getBlogs()) {
                if(b.isPublished()) {
                    publishedBlogs.add(b);
                }
            }
            BeanUtils.copyProperties(t, publishedType);
            publishedType.setBlogs(publishedBlogs);
            publishedTypes.add(publishedType);
        }

        Collections.sort(publishedTypes, typeComparator);

        return publishedTypes;
    }

    private Comparator<Tag> tagComparator = new Comparator<Tag>() {
        @Override
        public int compare(Tag o1, Tag o2) {
            return o2.getBlogs().size() - o1.getBlogs().size();
        }
    };

    private List<Tag> getPublishedTags(List<Tag> tags) {
        List<Tag> publishedTags = new ArrayList<>();
        if(tags == null || tags.size() == 0) {
            return publishedTags;
        }

        for(Tag t : tags) {
            Tag publishedTag = new Tag();
            List<Blog> publishedBlogs = new ArrayList<>();
            for(Blog b : t.getBlogs()) {
                if(b.isPublished()) {
                    publishedBlogs.add(b);
                }
            }
            BeanUtils.copyProperties(t, publishedTag);
            publishedTag.setBlogs(publishedBlogs);
            publishedTags.add(publishedTag);
        }
        Collections.sort(publishedTags, tagComparator);
        return publishedTags;
    }

    @GetMapping("/")
    public String index(@PageableDefault(size=8, sort={"updateTime"}, direction = Sort.Direction.DESC)Pageable pageable,
                        Model model) {
        model.addAttribute("page", blogService.listPublishedBlog(pageable));
        model.addAttribute("types", getPublishedTypes(typeService.listTypeTop(6)));
        model.addAttribute("tags", getPublishedTags(tagService.listTagTop(10)));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(10));
        return "index";
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size=1, sort={"updateTime"}, direction = Sort.Direction.DESC)Pageable pageable,
                         @RequestParam String query, Model model) {
        model.addAttribute("page", blogService.listSearchBlog(pageable, "%" + query + "%"));
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/search/{query}")
    public String searchPage(@PageableDefault(size=1, sort={"updateTime"}, direction = Sort.Direction.DESC)Pageable pageable,
                             @PathVariable String query, Model model) {
        model.addAttribute("page", blogService.listSearchBlog(pageable, "%" + query + "%"));
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getAndConvert(id));
//        Blog b =  blogService.getBlog(id);
//        b.setViews(b.getViews() + 1);
//        blogService.updateBlog(id, b);
        return "blog";
    }


    @GetMapping("/footer/newblog")
    public String newBlogs(Model model) {
        model.addAttribute("newBlogs" ,blogService.listRecommendBlogTop(3));

        return "_fragments :: newBlogList";
    }


}
