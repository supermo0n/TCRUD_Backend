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
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("ID 또는 패스워드가 잘못되었습니다."));
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
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("사용중인 ID 입니다."));
            }

            if (userService.existsEmail(signupRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("사용중인 EMAIL 입니다."));
            }

            if (userService.exitsNickname(signupRequest.getNickname())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("사용중인 NICKNAME 입니다."));
            }

            boolean isRegistered = userService.registerUser(signupRequest);

            if (isRegistered) {
                return ResponseEntity.ok(new MessageResponse("회원 가입 성공"));
            } else {
                throw new RuntimeException("회원가입 실패");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("서버 오류 발생")); // 기타 예외 발생 시 500 에러 반환
        }
    }

    //  회원가입시 중복체크 -> ID, EMAIL, 닉네임
    @GetMapping("/check/{targetParam}/{keyword}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable String targetParam,
                                                 @PathVariable String keyword) {
        if (targetParam.isEmpty() || keyword.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        try {
            boolean result = userService.validationUser(targetParam, keyword);

            return ResponseEntity
                    .ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(false);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    //  비밀번호 변경시 현재 사용 비밀번호 확인절차, 통과시 비밀번호 변경 처리
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
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            } else {
                boolean updateUser = userService.updateUser(updateRequest);

                if (updateUser) {

                    Authentication authentication;
                    if (updateRequest.isChangePwd()) {
                        authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(updateRequest.getUsername(), updateRequest.getUpdatePwd()));
                    } else {
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
                    return new ResponseEntity<>(false, HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 회원탈퇴
    @DeleteMapping("/user/deletion/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        try {

            Optional<User> optionalUser = userService.findByUserId(id);

            if (optionalUser.isPresent()) {

                User tempUser = optionalUser.get();

                boolean authUserMatch = tempUser.getUsername().equals(principal.getName());

                if (authUserMatch) {
                    userService.deleteWrittenBoardReply(id);
                    userService.deleteUser(tempUser.getUsername());

                    return new ResponseEntity<>(true, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
















