package com.example.blogsystem.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Title is mandatory")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Column( nullable = false, length = 100)
    private String title;

    @NotEmpty(message = "Body is mandatory")
    @Size(max = 1000, message = "Body must not exceed 1000 characters")
    @Column( nullable = false, length = 1000)
    private String body;



    @ManyToOne
    @JsonIgnore
    private MyUser myUser;
}
