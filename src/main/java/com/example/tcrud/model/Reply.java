package com.example.tcrud.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name= "SQ_REPLY_GENERATOR"
        , sequenceName = "SQ_REPLY"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "TB_REPLY")
@Where(clause = "DELETE_YN = 'N'")
@SQLDelete(sql="UPDATE TB_REPLY SET DELETE_YN = 'Y', DELETE_TIME = TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') WHERE ID = ?")
public class Reply extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            ,generator = "SQ_REPLY_GENERATOR")
    private Long id; // 글번호

//    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board; // 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private User writer; // 작성자

    @Column(nullable = false)
    private String content; // 댓글 내용
}
