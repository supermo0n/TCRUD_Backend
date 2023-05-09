package com.example.tcrud.dto;


import com.example.tcrud.model.Board;
import com.example.tcrud.model.User;
import lombok.*;

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
        private Integer replycnt;
        private Long viewcnt;
        private String insertTime;
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
        private String insertTime;
        private UserDto.Writer writer;
        private String updateTime;

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
