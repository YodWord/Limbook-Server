package com.daelim.Limbook.web.repository.boards;

import com.daelim.Limbook.domain.Board;
import com.daelim.Limbook.domain.User;
import com.daelim.Limbook.web.controller.dto.BoardDTO.CreateBoardDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class BoardRepositoryImpl implements BoardRepository{

    private final JdbcTemplate jdbcTemplate;

    public BoardRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Board saveBoard(CreateBoardDTO createBoardDTO, User user) throws Exception {

        Number key = null;

        Board board = new Board();

        board.setUser_id(user.getId());
        board.setBoard_title(createBoardDTO.getBoard_title());
        board.setBoard_contents(createBoardDTO.getBoard_contents());
        board.setBoard_create_date(Timestamp.valueOf(LocalDateTime.now()));

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("board");

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", board.getUser_id());
        params.put("board_title", board.getBoard_title());
        params.put("board_contents", board.getBoard_contents());
        params.put("board_create_date", board.getBoard_create_date());

        try {
            key = jdbcInsert.executeAndReturnKey(params);
        }catch (Exception e) {
            throw new Exception("DB 에러");
        }

        return findById(key.intValue()).get();
    }


    @Override
    public Optional<Board> findById(Integer id) throws Exception {

        String sql = "select * from board where board_number = ?";

        List<Board> result = jdbcTemplate.query(sql, boardRowMapper(), id);

        return result.stream().findAny();
    }

    private RowMapper<Board> boardRowMapper()  {
        return (rs, rowNum)  -> {
            Board board = new Board();
            board.setBoard_number(rs.getInt("board_number"));
            board.setUser_id(rs.getString("user_id"));
            board.setBoard_title(rs.getString("board_title"));
            board.setBoard_contents(rs.getString("board_contents"));
            board.setBoard_create_date(rs.getTimestamp("board_create_date"));
            return board;
        };
    }
}