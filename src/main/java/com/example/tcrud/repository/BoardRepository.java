package com.example.tcrud.repository;

import com.example.tcrud.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 제목으로 검색
    @Query(value = "SELECT b FROM Board b WHERE UPPER(b.title) LIKE UPPER(CONCAT('%', :title, '%'))")
    Page<Board> findAllByTitleContaining(@Param("title") String title, Pageable pageable);

    // 내용으로 검색
    @Query(value = "SELECT b FROM Board b WHERE UPPER(b.content) LIKE UPPER(CONCAT('%', :content, '%'))")
    Page<Board> findAllByContentContaining(@Param("content") String content, Pageable pageable);

    // 작성자 - 닉네임 검색
    @Query(value = "SELECT b FROM Board b WHERE UPPER(b.writer.nickname) LIKE UPPER(CONCAT('%', :nickname, '%'))")
    Page<Board> findAllByWriter_NicknameContaining(String nickname, Pageable pageable);


    // 작성자 - ID(USERNAME) 검색
    @Query(value = "SELECT b FROM Board b WHERE UPPER(b.writer.username) LIKE UPPER(CONCAT('%', :username, '%'))")
    Page<Board> findAllByWriter_UsernameContaining(String username, Pageable pageable);


    @Query("SELECT b FROM Board b WHERE UPPER(b.title) LIKE UPPER(CONCAT('%', :keyword, '%')) OR UPPER(b.content) LIKE UPPER(CONCAT('%', :keyword, '%'))")
    Page<Board> findAllByTitleContainingOrContentContaining(@Param("keyword") String keyword, Pageable pageable);


    Optional<Board> findById(Long id);

    //    viewcnt ++ query
    @Modifying
    @Query("update Board b set b.viewcnt = b.viewcnt + 1 where b.id = :id")
    void incrementViewcnt(@Param("id") Long id);

    //    제목에 댓글 개수 표시위한 count query
    @Query(value = "SELECT COUNT(*) FROM TB_REPLY WHERE BOARD_ID = :id AND DELETE_YN = 'N'", nativeQuery = true)
    int getReplyCountByBoardId(@Param("id") Long boardId);
}
