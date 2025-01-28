package com.example.blogsystem.Service;


import com.example.blogsystem.Api.ApiException;
import com.example.blogsystem.Model.Blog;
import com.example.blogsystem.Model.MyUser;
import com.example.blogsystem.Repository.AuthRepository;
import com.example.blogsystem.Repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final AuthRepository authRepository;
    private final BlogRepository blogRepository;

    public void add(Integer userId, Blog blog) {

        MyUser myUser = authRepository.findMyUserById(userId);

        if (myUser == null) throw new ApiException("Wrong username or password");

        blog.setMyUser(myUser);

        blogRepository.save(blog);
    }

    public List<Blog> getBlogsByUserId(Integer userId) {

        MyUser myUser = authRepository.findMyUserById(userId);
        if (myUser == null) throw new ApiException("Wrong username or password");

        return blogRepository.findBlogByMyUser(myUser);
    }


    public List<Blog> getBlogs() {

        return blogRepository.findAll();
    }


    public void update(Integer userId, Blog blog) {
        MyUser myUser = authRepository.findMyUserById(userId);
        if (myUser == null) throw new ApiException("Wrong username or password");

        Blog oldBlog = blogRepository.findBlogByIdAndMyUser(blog.getId(), myUser);

        oldBlog.setTitle(blog.getTitle());
        oldBlog.setBody(blog.getBody());
        blogRepository.save(oldBlog);
    }

    public void delete(Integer userId, Integer blogId) {
        MyUser myUser = authRepository.findMyUserById(userId);
        if (myUser == null) throw new ApiException("Wrong username or password");
        Blog blog = blogRepository.findBlogByIdAndMyUser(blogId, myUser);

        if (blog == null) throw new ApiException("blog not found");

        blogRepository.delete(blog);
    }


    public Blog getBlogById(Integer userId, Integer blogId) {

        MyUser myUser = authRepository.findMyUserById(userId);
        if (myUser == null) throw new ApiException("Wrong username or password");

        return blogRepository.findBlogByIdAndMyUser(blogId, myUser);
    }

    public Blog getBlogByTitle(String title) {

        return blogRepository.findBlogByTitle(title);
    }



}
