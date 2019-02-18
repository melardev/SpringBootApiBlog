package com.melardev.spring.blogapi.dtos.request.users;

import javax.validation.constraints.*;

public class CreateUserDto {


    @Size(min = 4, max = 255, message = "{errors.username.username.size}")
    @NotNull(message = "{errors.username.username.null}")
    @NotEmpty(message = "{errors.username.username.empty}")
    private String username;


    @Email(message = "Email must be valid")
    @NotBlank
    @Size(max = 60)
    private String email;

    @Size(min = 2, max = 255)
    private
    String firstName;

    @Min(2)
    @Max(255)
    private
    String lastName;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}