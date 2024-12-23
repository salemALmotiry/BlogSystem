package com.example.blogsystem.Controller;


import com.example.blogsystem.Api.ApiResponse;
import com.example.blogsystem.Model.MyUser;
import com.example.blogsystem.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class MyUserController {


    private final AuthService authService;

    public MyUserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid MyUser myUser) {

        authService.register(myUser);
        return ResponseEntity.ok().body(new ApiResponse("Registered Successfully"));

    }
}
