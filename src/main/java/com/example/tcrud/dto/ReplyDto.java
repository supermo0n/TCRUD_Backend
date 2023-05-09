package com.example.tcrud.dto;

import com.example.tcrud.model.Board;
import com.example.tcrud.model.Reply;
import com.example.tcrud.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class ReplyDto {

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    public static class Request
    {
        private Long id;
        @JsonProperty("boardId")
        private Long boardId;
        @JsonProperty("userId")
        private Long userId;
        private String content;

        public Request(Long id, Long boardId, Long userId, String content) {
            this.id = id;
            this.boardId = boardId;
            this.userId = userId;
            this.content = content;
        }

        public Long getBoardId() {
            return boardId;
        }

        public Long getUserId() {
            return userId;
        }

        public Reply toEntity(Board board, User user) {
            return Reply.builder()
                    .id(id)
                    .board(board)
                    .content(content)
                    .writer(user)
                    .build();
        }
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response
    {
        private Long id;
        private UserDto.Writer writer;
        private String content;
        private String insertTime;
        private Long boardId;

        public Response(Reply reply) {
            this.id = reply.getId();
            this.writer = new UserDto.Writer(reply.getWriter());
            this.content = reply.getContent();
            this.insertTime = reply.getInsertTime();
            this.boardId = reply.getBoard().getId();
        }
    }
}
