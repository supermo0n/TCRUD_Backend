package com.example.tcrud.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@SequenceGenerator(
        name= "SQ_ROLE_GENERATOR"
        , sequenceName = "SQ_ROLE"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "TB_ROLE")
@Getter
@Setter
@NoArgsConstructor
@Where(clause = "DELETE_YN = 'N'")
@SQLDelete(sql="UPDATE TB_ROLE SET DELETE_YN = 'Y', DELETE_TIME = TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') WHERE RID = ?")
public class Role extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "SQ_ROLE_GENERATOR")
    @Column
    private Long id;

    // EnumType - ORDINAL - 순서  , STRING - 문자열
    @Enumerated(EnumType.STRING)
    @Column
    private ERole name;

    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoles = new HashSet<>();

}








