package com.example.tcrud.service;

import com.example.tcrud.dto.UserDto;
import com.example.tcrud.model.*;
import com.example.tcrud.repository.BoardRepository;
import com.example.tcrud.repository.ReplyRepository;
import com.example.tcrud.repository.RoleRepository;
import com.example.tcrud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    PasswordEncoder encoder;

    public Boolean registerUser(UserDto.Request signupRequest) {

        User user = new User(signupRequest.getUsername(),
                signupRequest.getNickname(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword())
        );

        List<String> strRoles = signupRequest.getRole();
        Set<UserRole> userRoles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseGet(() -> {
                        Role defaultUserRole = new Role();
                        defaultUserRole.setName(ERole.ROLE_USER);
                        return defaultUserRole;
                    });
            UserRole defaultUserRole = new UserRole(user, userRole);
            userRoles.add(defaultUserRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        UserRole adminUserRole = new UserRole(user, adminRole);
                        userRoles.add(adminUserRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        UserRole defaultUserRole = new UserRole(user, userRole);
                        userRoles.add(defaultUserRole);
                }
            });
        }
        user.setUserRoles(userRoles);
        userRepository.save(user);

        return true;
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        return optionalUser;
    }

    public Optional<User> findByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser;
    }

    public Boolean validationUser(String param, String keyword) {
        switch (param) {
            case "username":
                return existsUsername(keyword);

            case "email":
                return existsEmail(keyword);

            case "nickname":
                return exitsNickname(keyword);
            default:
                return false;
        }
    }

    public Boolean existsUsername(String keyword) {
        return userRepository.existsByUsernameAndDeleteYn(keyword, "N");
    }

    public Boolean existsEmail(String keyword) {
        return userRepository.existsByEmailAndDeleteYn(keyword, "N");
    }

    public Boolean exitsNickname(String keyword) {
        return userRepository.existsByNicknameAndDeleteYn(keyword, "N");
    }

    public Boolean matchPassword(UserDto.pwdCheck request, String username) {
        Optional<User> optionalUser = findByUsername(username);

        if (optionalUser.isPresent()) {
            return encoder.matches(request.getPassword(), optionalUser.get().getPassword());
        } else {
            throw new IllegalArgumentException("username : " + username + " 을 찾을 수 없습니다");
        }
    }

    @Transactional
    public Boolean updateUser(UserDto.Request request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (request.isChangePwd()) {

                if (encoder.matches(request.getPassword(), user.getPassword())) {
                    user.setPassword(encoder.encode(request.getUpdatePwd()));
                } else {
                    return false;
                }
            } else if (!request.getNickname().isEmpty() && !request.isChangePwd()) {
                user.setNickname(request.getNickname());
            } else {
                return false;
            }
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Boolean deleteUser(Long userId, String username) {

        List<Board> boards = boardRepository.findByWriterId(userId);
        List<Reply> replies = replyRepository.findByWriterId(userId);

        boardRepository.deleteAll(boards);
        replyRepository.deleteAll(replies);

        userRepository.deleteById(userId);
        return !userRepository.existsByUsernameAndDeleteYn(username, "N");
    }
}