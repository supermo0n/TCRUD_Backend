package com.example.tcrud.model;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "SQ_REPLY_GENERATOR"
        , sequenceName = "SQ_REPLY"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "TB_REPLY")
@Where(clause = "DELETE_YN = 'N'")
@SQLDelete(sql = "UPDATE TB_REPLY SET DELETE_YN = 'Y', DELETE_TIME = CURRENT_TIMESTAMP WHERE ID = ?")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            , generator = "SQ_REPLY_GENERATOR")
    private Long id; // 글번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOARD_ID")
    private Board board; // 게시글

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private User writer; // 작성자

    @Column(nullable = false)
    private String content; // 댓글 내용

    /*
    대댓글(계층형 댓글) 구현위한 추가
    댓글 - parent / 대댓글 - child
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Reply parent;

    @Builder.Default
    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Reply> children = new ArrayList<>();

    // 댓글의 깊이
    @Column(nullable = false)
    private Integer depth;

    // children 이 존재하는 댓글을 삭제할 경우 -> hidden 으로 처리
    @Column(name = "HIDDEN")
    private String hidden;
}
