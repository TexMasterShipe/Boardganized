package com.boardganized.service;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.BoardWorkspace;
import com.boardganized.domain.model.Column;

class BoardPersistenceServiceTest {

    @Test
    void shouldSaveAndLoadBoard() throws Exception {
        Path tempFile = Files.createTempFile("boardganized", ".ser");
        Files.deleteIfExists(tempFile);

        BoardPersistenceService persistenceService = new BoardPersistenceService(tempFile);
        Board board = new Board("Board de test", "Board de test");
        board.addColumns(new Column("A faire"));
        board.rename("Board de test");

        persistenceService.save(board);

        Board loadedBoard = persistenceService.load();

        assertNotNull(loadedBoard);
        assertEquals("Board de test", loadedBoard.getTitle());
        assertEquals(board.getColumns().size(), loadedBoard.getColumns().size());
    }

    @Test
    void shouldReturnNullWhenStorageIsMissing() {
        BoardPersistenceService persistenceService = new BoardPersistenceService(Path.of("/tmp/boardganized-missing-file.ser"));

        assertNull(persistenceService.load());
    }

    @Test
    void shouldSaveAndLoadWorkspaceWithMultipleBoards() throws Exception {
        Path tempFile = Files.createTempFile("boardganized-workspace", ".ser");
        Files.deleteIfExists(tempFile);

        BoardPersistenceService persistenceService = new BoardPersistenceService(tempFile);
        BoardWorkspace workspace = new BoardWorkspace("alexis");

        Board firstBoard = new Board("Premier tableau", "Description");
        firstBoard.addColumns(new Column("A faire"));
        Board secondBoard = new Board("Second tableau", "Description");
        secondBoard.addColumns(new Column("A faire"));

        workspace.addBoard(firstBoard);
        workspace.addBoard(secondBoard);
        workspace.setActiveBoardId(secondBoard.getId());

        persistenceService.saveWorkspace(workspace);

        BoardWorkspace loadedWorkspace = persistenceService.loadWorkspace();

        assertNotNull(loadedWorkspace);
        assertEquals(2, loadedWorkspace.getBoards().size());
        assertEquals("Second tableau", loadedWorkspace.getActiveBoard().getTitle());
    }

    @Test
    void shouldHandleAccountNamesWithQuotes() throws Exception {
        Path tempFile = Files.createTempFile("boardganized-special-account", ".ser");
        Files.deleteIfExists(tempFile);

        BoardPersistenceService persistenceService = new BoardPersistenceService(tempFile);
        BoardWorkspace workspace = new BoardWorkspace("alexis\"");
        Board board = new Board("Tableau spécial", "Description");
        board.addColumns(new Column("A faire"));

        workspace.addBoard(board);

        persistenceService.saveWorkspace(workspace);

        BoardWorkspace loadedWorkspace = persistenceService.loadWorkspace();

        assertNotNull(loadedWorkspace);
        assertEquals("Tableau spécial", loadedWorkspace.getActiveBoard().getTitle());
    }
}