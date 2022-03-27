package com.joey.blog.web;

import com.joey.blog.po.Blog;
import com.joey.blog.po.Tag;
import com.joey.blog.service.BlogService;
import com.joey.blog.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class TagShowController {

    @Autowired
    private TagService tagService;

    @Autowired
    private BlogService blogService;

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

    @GetMapping("/tags/{id}")  //当前活跃的type
    public String tags(@PageableDefault(size = 1, sort={"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model) {
        List<Tag> tags = tagService.listTagTop(10000);

        List<Tag> publishedTags = getPublishedTags(tags);

        if(id == -1) {
            id =tags.get(0).getId();
        }
        model.addAttribute("tags", publishedTags);
        model.addAttribute("page", blogService.listTagBlog(pageable, id));
        model.addAttribute("activeTagId", id);

        return "tags";
    }
}
