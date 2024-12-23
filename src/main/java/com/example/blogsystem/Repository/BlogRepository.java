package com.example.blogsystem.Repository;

import com.example.blogsystem.Model.Blog;
import com.example.blogsystem.Model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlogRepository  extends JpaRepository<Blog, Integer> {

    List<Blog> findBlogByMyUser(MyUser user);

    Blog findBlogByIdAndMyUser(Integer id, MyUser user);

    Blog findBlogByTitle(String title);

}
