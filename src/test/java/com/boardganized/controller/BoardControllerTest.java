package com.boardganized.controller;

import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.Column;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardControllerTest {

    @Test
    void shouldCreateBoardWithEmptyColumns() {
        Board board = BoardController.createDemoBoard();

        assertTrue(board.getColumns().size() >= 3);
        assertTrue(board.getColumns().stream().allMatch(column -> column.getCards().isEmpty()));
    }
}
