package com.example.tcrud.repository;

import com.example.tcrud.model.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findAll(Pageable pageable);

    @Query("SELECT r FROM Reply r WHERE r.board.id = :boardId ORDER BY r.insertTime DESC")
    Page<Reply> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);
}
