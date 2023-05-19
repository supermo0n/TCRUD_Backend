package com.example.tcrud.service;

import com.example.tcrud.dto.BoardDto;
import com.example.tcrud.dto.ReplyDto;
import com.example.tcrud.model.Board;
import com.example.tcrud.model.Reply;
import com.example.tcrud.model.User;
import com.example.tcrud.repository.BoardRepository;
import com.example.tcrud.repository.ReplyRepository;
import com.example.tcrud.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    ReplyRepository replyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;


    //    TODO : 조회 로직
    public Page<BoardDto.BoardListDto> getBoardList(String searchSelect, String searchKeyword, Pageable pageable) {
        switch (searchSelect) {
            case "title":
                return findByTitle(searchKeyword, pageable);

            case "content":
                return findByContent(searchKeyword, pageable);

            case "titleorcontent":
                return findByTitleOrContent(searchKeyword, pageable);

            case "username":
                return findByUsername(searchKeyword, pageable);

            case "nickname":
                return findByNickname(searchKeyword, pageable);

            default:
                return readAllBoard(pageable);
        }
    }

    public Page<BoardDto.BoardListDto> readAllBoard(Pageable pageable) {
        Page<Board> page = boardRepository.findAll(pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    //    게시글 상세조회
    public Optional<BoardDto.getBoardDto> optionalfindById(long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        return optionalBoard.map(Board -> modelMapper.map(Board, BoardDto.getBoardDto.class));
    }

    //    CREATE
    public BoardDto.getBoardDto saveBoard(BoardDto.Request request) {
        Board board = request.toEntity();
        boardRepository.save(board);
        return modelMapper.map(board, BoardDto.getBoardDto.class);
    }

    //    조회수++
    @Transactional
    public void upcnt(Long id) {
        boardRepository.incrementViewcnt(id);
    }

    public Optional<Board> findById(long id) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        return optionalBoard;
    }

    //    제목(title) 검색
    public Page<BoardDto.BoardListDto> findByTitle(String searchKeyword, Pageable pageable) {
        Page<Board> page = boardRepository.findAllByTitleContaining(searchKeyword, pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    //    내용(content) 검색
    public Page<BoardDto.BoardListDto> findByContent(String searchKeyword, Pageable pageable) {
        Page<Board> page = boardRepository.findAllByContentContaining(searchKeyword, pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    //    제목(title) + 내용(content) 검색
    public Page<BoardDto.BoardListDto> findByTitleOrContent(String searchKeyword, Pageable pageable) {
        Page<Board> page = boardRepository.findAllByTitleContainingOrContentContaining(
                searchKeyword, pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    //    글쓴이(username) 검색
    public Page<BoardDto.BoardListDto> findByUsername(String searchKeyword, Pageable pageable) {
        Page<Board> page = boardRepository.findAllByWriter_UsernameContaining(searchKeyword, pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    //        글쓴이(nickname) 검색
    public Page<BoardDto.BoardListDto> findByNickname(String searchKeyword, Pageable pageable) {
        Page<Board> page = boardRepository.findAllByWriter_NicknameContaining(searchKeyword, pageable);
        Page<BoardDto.BoardListDto> responseList =
                page.map(board -> {
                    BoardDto.BoardListDto boardListDto = modelMapper.map(board, BoardDto.BoardListDto.class);
                    long replyCount = countBoardIdReplies(board.getId());
                    boardListDto.setReplycnt(replyCount);
                    return boardListDto;
                });
        return responseList;
    }

    public boolean deleteBoard(Long no) {
        if (boardRepository.existsById(no) == true) {
            boardRepository.deleteById(no);
            return true;
        } else {
            return false;
        }
    }

    //    BOARD - UPDATE
    public BoardDto.getBoardDto updateBoard(Long id, BoardDto.Request request) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            board.setTitle(request.getTitle());
            board.setContent(request.getContent());
            boardRepository.save(board);
            BoardDto.getBoardDto response = modelMapper.map(board, BoardDto.getBoardDto.class);
            return response;
        } else {
            throw new RuntimeException("게시글이 존재하지 않습니다.");
        }
    }

    //    TODO : 대댓글 LIST처리용 댓글카운트
    public Long countBoardIdReplies(Long id)
    {
        return replyRepository.countByBoardId(id);
    }

//    TODO :---------------------- REPLY --------------------------------

    //    REPLY LIST
    public List<ReplyDto.Response> readBoardIdReplies(Long boardId) {
//        List<Reply> rootReplies = replyRepository.findAllWithMemberAndParentByPostIdOrderByParentIdAscNullsFirstCommentIdAsc(boardId);
        List<Reply> rootReplies = replyRepository.findAllByBoardId(boardId);
        List<ReplyDto.Response> responseList = new ArrayList<>();

        for (Reply rootReply : rootReplies) {
            traverseReplyHierarchy(rootReply, responseList);
        }

        return responseList;
    }


    private void traverseReplyHierarchy(Reply reply, List<ReplyDto.Response> responseList) {
        if (reply.getParent() == null) { // 최상위 댓글만 추가
            ReplyDto.Response response = new ReplyDto.Response(reply);
            if (!responseList.contains(response)) {
                responseList.add(response);
            }
        }

        List<Reply> children = reply.getChildren();
        if (children != null) {
            for (Reply child : children) {
                traverseReplyHierarchy(child, responseList);
            }
        }
    }


    // Reply CREATE
    public ReplyDto.Response createReply(ReplyDto.Request request) {
        Optional<Board> optionalBoard = boardRepository.findById(request.getBoardId());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());

        if (!optionalBoard.isPresent() || !optionalUser.isPresent()) {
            throw new EntityNotFoundException("게시글 또는 사용자를 찾을 수 없습니다.");
        }

        Reply parentReply = null;

        if (request.getParentReplyId() != null) {
            Optional<Reply> optionalParentReply = replyRepository.findById(request.getParentReplyId());

            if (!optionalParentReply.isPresent()) {
                throw new EntityNotFoundException("댓글을 찾을 수 없습니다.");
            }
            parentReply = optionalParentReply.get();
        }

        Board board = optionalBoard.get();
        User user = optionalUser.get();
        Reply reply = request.toEntity(board, user, parentReply);


        if (parentReply != null) {
            reply.setParent(parentReply);
            reply.setDepth(parentReply.getDepth() + 1);
        } else {
            reply.setDepth(0);
        }

        replyRepository.save(reply);
        ReplyDto.Response response = new ReplyDto.Response(reply);
        return response;
    }

    //    Reply DELETE
    @Transactional
    public boolean removeReply(Long boardid, Long replyid)
    {
        if ( crossCheckBR(boardid, replyid) )
        {
//            boardId가 존재하는지, replyId가 존재하는지, reply의 boardId가 매개변수와 일치하는지.
//            board의 replyList에 replyId에 해당하는 댓글이 존재하는지

            Optional<Reply> getReply = replyRepository.findById(replyid);
            Reply tempReply = getReply.get();

            if(tempReply.getChildren() != null && !tempReply.getChildren().isEmpty())
            {
                tempReply.setContent("삭제된 댓글입니다");
                tempReply.setHidden("Y");
                replyRepository.save(tempReply);
            }
            else
            {
                replyRepository.deleteById(replyid);
            }
            return true;

        } else {
            return false;
        }
    }

    //    Reply UPDATE
    public ReplyDto.Response updateReply(Long id, ReplyDto.Request request) {
        Optional<Reply> optionalReply = replyRepository.findById(id);
        if (optionalReply.isPresent()) {
            Reply reply = optionalReply.get();
            reply.setContent(request.getContent());
            replyRepository.save(reply);
            ReplyDto.Response response = modelMapper.map(reply, ReplyDto.Response.class);
            return response;
        } else {
            throw new RuntimeException("reply is not exits!");
        }
    }


    public boolean crossCheckBR(Long boardid, Long replyid) {
        Optional<Reply> optionalReply = replyRepository.findById(replyid);
        Optional<Board> optionalBoard = boardRepository.findById(boardid);

        if (optionalReply.isPresent() && optionalBoard.isPresent()) {
            Reply reply = optionalReply.get();
            Board board = optionalBoard.get();

            boolean boardsw = reply.getBoard().getId() == boardid;
            boolean replysw = board.getReplyList().contains(reply);

            if (boardsw && replysw) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

//    ----------------------------------------------------------------------------------

    //   TODO : 수정/삭제를 위한 로그인 사용자와 게시글 작성자가 일치하는지 확인. - BOARD
    public boolean isAuthorBoard(Long id, Principal principal) {
        Optional<Board> optionalBoard = boardRepository.findById(id);
        if (optionalBoard.isPresent()) {
            User author = optionalBoard.get().getWriter();
            return author.getUsername().equals(principal.getName());
        }
        return false;
    }

    //   TODO : 수정/삭제를 위한 로그인 사용자와 게시글 작성자가 일치하는지 확인. - REPLY
    public boolean isAuthorReply(Long id, Principal principal) {
        Optional<Reply> optionalReply = replyRepository.findById(id);
        if (optionalReply.isPresent()) {
            User author = optionalReply.get().getWriter();
            return author.getUsername().equals(principal.getName());
        }
        return false;
    }


}
