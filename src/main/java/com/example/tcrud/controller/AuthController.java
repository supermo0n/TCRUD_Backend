package com.example.tcrud.controller;

import com.example.tcrud.dto.JwtResponse;
import com.example.tcrud.dto.MessageResponse;
import com.example.tcrud.dto.UserDto;
import com.example.tcrud.model.User;
import com.example.tcrud.security.jwt.JwtUtils;
import com.example.tcrud.security.services.UserDetailsImpl;
import com.example.tcrud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//  origins = "*" : all url core 통과
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    //    로그인
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        try {
            if (!userService.existsUsername(loginRequest.getUsername())) {
                /*
                기존 BadRequest 에서 OK 응답으로 변경, dtoResponse 를 이용하여 handling
                메소드 및 URL 노출을 최소화(취약점 비노출) 하기 위해 v0.2에서 수정 시도
                */
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                loginRequest.getUsername(), false, "존재하지 않는 ID 입니다"));
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getNickname(),
                    userDetails.getEmail(),
                    roles
            ));
        } catch (AuthenticationException e) {
             /*
               기존 HTTPStatus UNAUTHORIZED 에서 OK 응답으로 변경, dtoResponse 를 이용하여 handling
               메소드 및 URL 노출을 최소화(취약점 비노출) 하기 위해 v0.2에서 수정 시도
               */
//            패스워드 불일치 시 exception
            return ResponseEntity
                    .ok()
                    .body(new UserDto.userStateMsgResponse(
                            loginRequest.getUsername(), false, "패스워드가 잘못되었습니다."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("서버 오류 발생"));
        }
    }

    //    회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto.Request signupRequest) {
        try {
            if (userService.existsUsername(signupRequest.getUsername())) {
                 /*
                기존 BadRequest 에서 OK 응답으로 변경, dtoResponse 를 이용하여 handling
                메소드 및 URL 노출을 최소화(취약점 비노출) 하기 위해 v0.2에서 수정 시도
                */
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                signupRequest.getUsername(), false, "사용중인 ID 입니다"));
            }

            if (userService.exitsNickname(signupRequest.getNickname())) {
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                signupRequest.getNickname(), false, "사용중인 NICKNAME 입니다"));
            }

            if (userService.existsEmail(signupRequest.getEmail())) {
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                signupRequest.getEmail(), false, "사용중인 EMAIL 입니다"));
            }

            boolean isRegistered = userService.registerUser(signupRequest);

            if (isRegistered) {
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                signupRequest.getUsername(), isRegistered, "회원가입 성공"));
            } else {
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(
                                signupRequest.getUsername(), isRegistered, "회원가입 실패"));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse("서버 오류 발생"));
        }
    }

    //  front에서 개별적인 회원정보(unique 제약 조건) 중복 체크 요청
    @GetMapping("/check/{targetParam}/{keyword}")
    public ResponseEntity<?> checkUserData(@PathVariable String targetParam,
                                           @PathVariable String keyword) {
        if (targetParam.isEmpty() || keyword.isEmpty()) {
            return ResponseEntity
                    .ok()
                    .body(new UserDto.userStateMsgResponse("null", false, "비정상적인 요청입니다"));
        }

        try {
            boolean result = userService.validationUser(targetParam, keyword);

            String message;

            if (!result) {
                if (targetParam.equals("username")) {
                    message = "[" + keyword + "]는 사용가능한 ID 입니다.";
                } else if (targetParam.equals("email")) {
                    message = "[" + keyword + "]는 사용가능한 이메일 입니다.";
                } else {
                    message = "[" + keyword + "]는 사용가능한 닉네임입니다.";
                }
                 /*
                기존 OK 에서 dtoResponse 를 이용하여 handling
                메소드 및 URL 노출을 최소화(취약점 비노출) 하기 위해 v0.2에서 수정 시도
                */
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(targetParam, result, message));
            } else {
                switch (targetParam) {
                    case "username":
                        message = "[" + keyword + "]는 이미 사용중인 ID 입니다.";
                        break;
                    case "email":
                        message = "[" + keyword + "]는 이미 사용중인 이메일입니다.";
                        break;
                    case "nickname":
                        message = "[" + keyword + "]는 이미 사용중인 닉네임입니다.";
                        break;
                    default:
                        message = "유효하지 않은 요청입니다.";
                        break;
                }
                 /*
                기존 BadRequest 에서 dtoResponse 를 이용하여 handling
                메소드 및 URL 노출을 최소화(취약점 비노출) 하기 위해 v0.2에서 수정 시도
                */
                return ResponseEntity
                        .ok()
                        .body(new UserDto.userStateMsgResponse(targetParam, result, message));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .ok()
                    .body(new UserDto.userStateMsgResponse(targetParam, false, "비정상적인 요청입니다"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생");
        }
    }

    //  회원정보 수정 전, 비밀번호 확인절차. 통과시 회원정보 수정 접근
    @PostMapping("/matchpwd")
    public ResponseEntity<?> matchPassword(@RequestBody UserDto.pwdCheck request,
                                           Principal principal) {
        try {
            if (!principal.getName().isEmpty()) {
                boolean matchPwd =
                        userService.matchPassword(request, principal.getName());
                if (matchPwd) {
                    return new ResponseEntity<>(true, HttpStatus.OK);

                } else {
                    return new ResponseEntity<>(false, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    회원정보 수정(업데이트)
    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto.Request updateRequest, Principal principal) {
        try {

            if (!principal.getName().matches(updateRequest.getUsername())) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("유효하지 않은 요청입니다"));
            } else {
                boolean updateUser = userService.updateUser(updateRequest);

                if (updateUser) {

                    Authentication authentication;

                    if (updateRequest.isChangePwd()) {
//                        새로 인증 시작 -> 비밀번호 변경(isChangePwd)의 경우 비밀번호 변경 후 새로운 비밀번호로 authenticate
                        authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(updateRequest.getUsername(), updateRequest.getUpdatePwd()));
                    } else {
//                        새로 인증 시작 -> 비밀번호 변경을(!isChangePwd) 의 경우 기존 비밀번호로 authenticate
                        authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(updateRequest.getUsername(), updateRequest.getPassword()));
                    }

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    String jwt = jwtUtils.generateJwtToken(authentication);

                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                    List<String> roles = userDetails.getAuthorities()
                            .stream()
                            .map(item -> item.getAuthority())
                            .collect(Collectors.toList());

                    return ResponseEntity.ok(new JwtResponse(jwt,
                            userDetails.getId(),
                            userDetails.getUsername(),
                            userDetails.getNickname(),
                            userDetails.getEmail(),
                            roles
                    ));
                } else {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("회원정보 수정 실패"));
                }
            }
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(false);
        }
    }

    // 회원탈퇴
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        try {
            Optional<User> optionalUser = userService.findByUserId(id);

            if (optionalUser.isPresent()) {
                User tempUser = optionalUser.get();
    //         현재 login 유져 == 회원탈퇴 요청 계정 유져 일치여부
                boolean authUserMatch = tempUser.getUsername().equals(principal.getName());

                if (authUserMatch) {
                    boolean deleteSuccess = userService.deleteUser(id, tempUser.getUsername());

                    if (deleteSuccess) {
                        return ResponseEntity
                                .ok()
                                .body(new MessageResponse("회원 탈퇴가 완료되었습니다."));
                    } else {
                        return ResponseEntity
                                .badRequest()
                                .body(new MessageResponse("회원 탈퇴 실패"));
                    }
                } else {
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new MessageResponse("회원 탈퇴 권한이 없습니다."));
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("일치하는 회원이 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new MessageResponse("서버 오류 발생"));
        }
    }
}
















