package com.example.tcrud.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
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
@DynamicInsert
@DynamicUpdate
@SequenceGenerator(
        name= "SQ_BOARD_GENERATOR"
        , sequenceName = "SQ_BOARD"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "TB_BOARD")
@Where(clause = "DELETE_YN = 'N'")
@SQLDelete(sql="UPDATE TB_BOARD SET DELETE_YN = 'Y', DELETE_TIME = TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') WHERE ID = ?")
public class Board extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE
            ,generator = "SQ_BOARD_GENERATOR")
    private Long id; // 글번호

    @Column(length = 50, nullable = false, name = "TITLE")
    private String title;

    @Column(nullable = false, name = "CONTENT")
    private String content;

    @Column(columnDefinition = "long default 0", nullable = false, name = "VIEWCNT")
    private Long viewcnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User writer;

//    @JsonManagedReference
    @JsonIgnoreProperties({"board"})
    @Builder.Default
    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replyList = new ArrayList<>();

    public void viewCntUp() {
        this.viewcnt++;
    }

    public int replyCnt()
    {
        return this.getReplyList().size();
    }

}
