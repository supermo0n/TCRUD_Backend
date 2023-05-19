package com.example.tcrud.dto;


import com.example.tcrud.model.Board;
import com.example.tcrud.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

public class BoardDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request
    {
        private Long id;
        private String title;
        private String content;
        private User writer;

        public Board toEntity()
        {
            Board board = Board.builder()
                    .id(id)
                    .title(title)
                    .content(content)
                    .writer(writer)
                    .build();
            return board;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BoardListDto {
        private Long id;
        private String title;
        private String content;
        private Long replycnt;
        private Long viewcnt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime insertTime;
        private UserDto.Writer writer;

        public BoardListDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.viewcnt = board.getViewcnt();
            this.insertTime = board.getInsertTime();
            this.writer = new UserDto.Writer(board.getWriter());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class getBoardDto {
        private Long id;
        private String title;
        private String content;
        private Long viewcnt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime insertTime;
        private UserDto.Writer writer;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        public getBoardDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.viewcnt = board.getViewcnt();
            this.insertTime = board.getInsertTime();
            this.writer = new UserDto.Writer(board.getWriter());
            this.updateTime = (board.getUpdateTime());
        }
    }
}
