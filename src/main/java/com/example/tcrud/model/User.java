package com.example.tcrud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "SQ_USER_GENERATOR"
        , sequenceName = "SQ_USER"
        , initialValue = 1
        , allocationSize = 1)
@Table(name="TB_USER"
        ,uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "nickname")
        // username(로그인ID, 이메일, 닉네임은 unique 제약)
    }
)
@Where(clause = "DELETE_YN = 'N'")
@SQLDelete(sql="UPDATE TB_USER SET DELETE_YN = 'Y', DELETE_TIME = TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') WHERE ID = ?")
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "SQ_USER_GENERATOR")
    @Column
    private Long id;

    // 사용자의 로그인id
    @Column(unique = true)
    @NotBlank

    @Size(min = 6, max = 20)
    private String username;

    // 사용자의 닉네임
    @Column(unique = true)
    @NotNull
    @Size(min = 3, max = 10)
    private String nickname;

    @Column(unique = true)
    @NotBlank
    @Email
    @Size(max=50)
    private String email;

    @NotBlank
    @Size(min=6, max=120)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    public List<String> getRoleNames() {
        return userRoles.stream()
                .map(userRole -> userRole.getRole().getName().toString())
                .collect(Collectors.toList());
    }

    //    @Builder
    public User(String username, String nickname, String email, String password) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public User update(String name) {
        this.username = username;

        return this;
    }

}
