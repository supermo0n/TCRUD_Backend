package com.example.tcrud.dto;

import com.example.tcrud.model.Board;
import com.example.tcrud.model.Reply;
import com.example.tcrud.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReplyDto {

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    public static class Request {

        private Long id;

        @JsonProperty("boardId")
        private Long boardId;

        @JsonProperty("userId")
        private Long userId;

        private String content;
        //        대댓글 구현 추가
        private Long parentReplyId;

        //        대댓글 구현 추가 this.parentReplyId = parentReplyId;
        public Request(Long id, Long boardId, Long userId, String content, Long parentReplyId) {
            this.id = id;
            this.boardId = boardId;
            this.userId = userId;
            this.content = content;
            this.parentReplyId = parentReplyId;
        }

        public Long getBoardId() {
            return boardId;
        }

        public Long getUserId() {
            return userId;
        }

        //        대댓글 구현 추가 Reply parentReply
        public Reply toEntity(Board board, User user, Reply parentReply) {
            return Reply.builder()
                    .id(id)
                    .board(board)
                    .writer(user)
                    .content(content)
//                    대댓글 구현 추가
                    .parent(parentReply)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private UserDto.Writer writer;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime insertTime;
        private Long boardId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
        //        대댓글 구현 추가
        private List<Response> children;
        private Integer depth;
        private String hidden; // 자식댓글이 존재할 경우 hidden 으로 컨트롤

        public Response(Reply reply) {
            this.id = reply.getId();
            this.writer = new UserDto.Writer(reply.getWriter());
            this.content = reply.getContent();
            this.insertTime = reply.getInsertTime();
            this.boardId = reply.getBoard().getId();
            this.updateTime = reply.getUpdateTime();
            this.depth = reply.getDepth();
            this.children = new ArrayList<>();
            this.hidden = reply.getHidden();

            if (reply.getChildren() != null) {
                for (Reply child : reply.getChildren()) {
                    this.children.add(new Response(child));
                }
            }
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReplyDto.Response response = (ReplyDto.Response) o;
            return Objects.equals(id, response.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

    }
}
