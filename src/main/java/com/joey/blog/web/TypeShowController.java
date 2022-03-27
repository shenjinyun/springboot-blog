package com.joey.blog.web;

import com.joey.blog.po.Blog;
import com.joey.blog.po.Type;
import com.joey.blog.service.BlogService;
import com.joey.blog.service.TypeService;
import com.joey.blog.vo.BlogQuery;
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
public class TypeShowController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private BlogService blogService;

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

    @GetMapping("/types/{id}")  //当前活跃的type
    public String types(@PageableDefault(size = 1, sort={"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        @PathVariable Long id, Model model) {
        List<Type> types = typeService.listTypeTop(10000);

        List<Type> publishedTypes = getPublishedTypes(types);

        if(id == -1) {
            id =types.get(0).getId();
        }
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setTypeId(id);
        blogQuery.setPublished(true);
        model.addAttribute("types", publishedTypes);
        model.addAttribute("page", blogService.listSearchBlog(pageable, blogQuery));

        model.addAttribute("activeTypeId", id);

        return "types";
    }
}
