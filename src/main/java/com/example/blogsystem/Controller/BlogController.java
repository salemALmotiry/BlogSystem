package com.example.blogsystem.Controller;


import com.example.blogsystem.Api.ApiResponse;
import com.example.blogsystem.Model.Blog;
import com.example.blogsystem.Model.MyUser;
import com.example.blogsystem.Service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
public class BlogController {


    private final BlogService blogService;


    @GetMapping("/get-all")
    public ResponseEntity getAllBlogs() {
        return ResponseEntity.ok(blogService.getBlogs());
    }



    @PostMapping("/add")
    public ResponseEntity add(@AuthenticationPrincipal MyUser myUser, @RequestBody @Valid Blog blog) {

        blogService.add(myUser.getId(), blog);
        return ResponseEntity.ok().body(new ApiResponse("blog added successfully"));
    }


    @GetMapping("/get-my-blogs")
    public ResponseEntity getMyBlogs(@AuthenticationPrincipal MyUser myUser) {

        return ResponseEntity.ok().body(blogService.getBlogsByUserId(myUser.getId()));

    }

    @PutMapping("/update")
    public ResponseEntity update(@AuthenticationPrincipal MyUser myUser,
                                 @RequestBody @Valid Blog blog) {
        blogService.update(myUser.getId(), blog);
        return ResponseEntity.ok(new ApiResponse("Blog updated successfully"));
    }

    @DeleteMapping("/delete/{blogId}")
    public ResponseEntity delete(@AuthenticationPrincipal MyUser myUser,
                                 @PathVariable Integer blogId) {
        blogService.delete(myUser.getId(), blogId);
        return ResponseEntity.ok(new ApiResponse("Blog deleted successfully"));
    }

    @GetMapping("/get-by-id/{blogId}")
    public ResponseEntity getBlogById(@AuthenticationPrincipal MyUser myUser,
                                      @PathVariable Integer blogId) {
        Blog blog = blogService.getBlogById(myUser.getId(), blogId);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/get-by-title/{title}")
    public ResponseEntity getBlogByTitle(@PathVariable String title) {
        Blog blog = blogService.getBlogByTitle(title);
        return ResponseEntity.ok(blog);
    }
}
