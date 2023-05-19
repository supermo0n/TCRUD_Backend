package com.example.tcrud.controller;

import com.example.tcrud.dto.BoardDto;
import com.example.tcrud.dto.ReplyDto;
import com.example.tcrud.model.Board;
import com.example.tcrud.model.User;
import com.example.tcrud.service.BoardService;
import com.example.tcrud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;


@RestController
@Slf4j
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BoardController {

    @Autowired
    BoardService boardService;

    @Autowired
    UserService userService;


    //  TODO : get Boards (paging) /board
    @GetMapping("/board")
    public ResponseEntity<Object> readBoards(@RequestParam String searchSelect,
                                             @RequestParam(required = false) String searchKeyword,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        try {

            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<BoardDto.BoardListDto> boardDtoResponsePage;

            if (searchSelect.isEmpty()) {
                boardDtoResponsePage = boardService.readAllBoard(pageable);
            } else {
                boardDtoResponsePage = boardService.getBoardList(searchSelect, searchKeyword, pageable);
            }

            if (boardDtoResponsePage.hasContent()) {

                Map<String, Object> response = new HashMap<>();
                response.put("boards", boardDtoResponsePage.getContent());
                response.put("currentPage", boardDtoResponsePage.getNumber());
                response.put("totalItems", boardDtoResponsePage.getTotalElements());
                response.put("totalPages", boardDtoResponsePage.getTotalPages());

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Create Board /board/add
    @PostMapping("/board/add")
    public ResponseEntity<Object> createBoard(@Valid @RequestBody BoardDto.Request request, Principal principal) {

        try {
            Optional<User> checkUser = userService.findByUsername(principal.getName());

            if (checkUser.isPresent()) {
                request.setWriter(checkUser.get());
                BoardDto.getBoardDto response = boardService.saveBoard(request);

                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : get Board /board/{boardId}
    @GetMapping("/board/{boardId}")
    public ResponseEntity<Object> readBoard(@PathVariable Long boardId) {
        try {
            Optional<BoardDto.getBoardDto> optionalResponse = boardService.optionalfindById(boardId);

            if (optionalResponse.isPresent()) {

                //  /board/{id} 조회수++
                boardService.upcnt(boardId);

                return new ResponseEntity<>(optionalResponse.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Update Board /board/{boardId}
    @PutMapping("/board/{boardId}")
    public ResponseEntity<Object> updateBoard(@PathVariable Long boardId,
                                              @RequestBody BoardDto.Request request,
                                              Principal principal) {
        try {
            Optional<User> optionalUser = userService.findByUsername(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isAuthor = boardService.isAuthorBoard(boardId, principal);

                if (isAuthor) {
                    request.setWriter(user);
                    BoardDto.getBoardDto response = boardService.updateBoard(boardId, request);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Delete Board /board/{boardId}
    @DeleteMapping("/board/{boardId}")
    public ResponseEntity<Object> deleteBoard(@PathVariable Long boardId, Principal principal) {
        try {
            Optional<User> optionalUser = userService.findByUsername(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isAuthor = boardService.isAuthorBoard(boardId, principal);

                if (isAuthor) {
                    boolean bSuccess = boardService.deleteBoard(boardId);
                    if (bSuccess == true) {
                        return new ResponseEntity<>(HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    --------------- [ REPLY ] ---------------

    //    TODO : GETMAPPING /board/{boardId}/reply
    @GetMapping("/board/{boardId}/reply")
    public ResponseEntity<Object> readReply(@PathVariable Long boardId) {
        try {
            boolean targetBoard = boardService.optionalfindById(boardId).isPresent();

            if (targetBoard) {
                List<ReplyDto.Response> responseList = boardService.readBoardIdReplies(boardId);
                Long replyCnt = boardService.countBoardIdReplies(boardId);
                Map<String, Object> responseBody = new HashMap<>();

                responseBody.put("replies", responseList);
                responseBody.put("totalReply", replyCnt);

                return new ResponseEntity<>(responseBody, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Create Reply /board/{boardId}/reply/
    @PostMapping("/board/{boardId}/reply")
    public ResponseEntity<Object> createReply(@PathVariable Long boardId,
                                              @RequestBody ReplyDto.Request request,
                                              Principal principal) {
        try {
            Optional<User> optionalUser = userService.findByUsername(principal.getName());
            if (!optionalUser.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Optional<Board> optionalBoard = boardService.findById(boardId);
            if (!optionalBoard.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            boardService.createReply(request);
            return new ResponseEntity<>(HttpStatus.CREATED);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Update Reply /board/{boardId}/reply/{replyId}
    @PutMapping("/board/{boardId}/reply/{replyId}")
    public ResponseEntity<Object> updateReply(@PathVariable Long boardId,
                                              @PathVariable Long replyId,
                                              @RequestBody ReplyDto.Request request,
                                              Principal principal) {
        try {

            Optional<User> optionalUser = userService.findByUsername(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                boolean isAuthor = boardService.isAuthorReply(replyId, principal);

                boolean crCheck = boardService.crossCheckBR(boardId, replyId);

                if (isAuthor && crCheck) {
                    ReplyDto.Response response = boardService.updateReply(replyId, request);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  TODO : Delete Reply /board/{boardId}/reply/{replyId}
    @DeleteMapping("/board/{boardId}/reply/{replyId}")
    public ResponseEntity<Object> deleteReply(@PathVariable Long boardId,
                                              @PathVariable Long replyId,
                                              Principal principal) {
        try {

            if (!boardService.isAuthorReply(replyId, principal)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            boolean bSuccess = boardService.removeReply(boardId, replyId);

            if (bSuccess == true) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
