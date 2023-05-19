package com.example.tcrud.dto;

import com.example.tcrud.model.User;
import com.example.tcrud.model.UserRole;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;


public class UserDto {

    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
//    @Builder
    public static class Request
    {
        private Long id;

        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Z]+$")
        @Size(min = 6, max = 20)
        private String username;

        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ-가-힣]+$")
        @Size(min = 3, max = 10)
        private String nickname;

        @NotBlank
        @Size(max=50)
        @Email
        private String email;

        @NotBlank
        @Pattern(regexp = "^[A-Za-z\\d$@$!%*?&]{6,20}$")
        @Size(min=6, max=120)
        private String password;

        private List<String> role;

        private boolean changePwd;

        @Pattern(regexp = "^[A-Za-z\\d$@$!%*?&]{6,20}$")
        @Size(min=6, max=120)
        private String updatePwd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response
    {
        private Long id;
        private String username;
        private String nickname;
        private String email;
        private String password;
        private Set<UserRole> role;

        public Response(User user)
        {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.role = user.getUserRoles();
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest
    {
        @NotBlank
        @Pattern(regexp = "^[0-9a-zA-Z]+$")
        @Size(min = 6, max = 20)
        private String username;

        @Pattern(regexp = "^[A-Za-z\\d$@$!%*?&]{6,20}$")
        @NotBlank
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Writer
    {
        private Long id;
        private String username;
        private String nickname;

        public Writer(User user)
        {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class pwdCheck
    {
        @Pattern(regexp = "^[A-Za-z\\d$@$!%*?&]{6,20}$")
        @NotBlank
        private String password;

        public pwdCheck(String password)
        {
            this.password = password;
        }
    }


    @Getter
    public static class userStateMsgResponse
    {
        private Boolean answer;
        private String param;
        private String message;

        public userStateMsgResponse(String param, Boolean answer, String message)
        {
            this.param = param;
            this.answer = answer;
            this.message = message;
        }
    }

}
