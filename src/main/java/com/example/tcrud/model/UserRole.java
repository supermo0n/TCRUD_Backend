package com.example.tcrud.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@SequenceGenerator(name = "SQ_USERROLE_GENERATOR"
        , sequenceName = "SQ_USERROLE"
        , initialValue = 1
        , allocationSize = 1)
@Table(name = "TB_USER_ROLE")
public class UserRole extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY
            , generator = "SQ_USERROLE_GENERATOR")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    public UserRole() {}

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
