package com.boardganized;

import com.boardganized.controller.BoardController;
import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.BoardWorkspace;
import com.boardganized.domain.model.Column;
import com.boardganized.service.BoardPersistenceService;
import com.boardganized.view.BoardView;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Boardganized.
 * Elle initialise le tableau, construit la vue et lance l'interface JavaFX.
 */
public class Main extends Application {

    private BoardPersistenceService persistenceService;
    private BoardController controller;
    private BoardWorkspace workspace;
    private String accountName;
    private BorderPane workspaceRoot;
    private BorderPane boardHost;
    private ComboBox<Board> boardSelector;

    /**
     * Point d'entrée de l'application.
     *
     * @param args arguments passés au programme
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Démarre l'interface utilisateur et affiche le tableau.
     *
     * @param stage fenêtre principale de l'application
     */
    @Override
    public void start(Stage stage) {
        accountName = requestAccountName();
        persistenceService = new BoardPersistenceService(accountName);

        workspace = loadWorkspace();
        workspace.setAccountName(accountName);
        ensureWorkspaceHasAtLeastOneBoard();

        workspaceRoot = new BorderPane();
        boardHost = new BorderPane();
        boardSelector = buildBoardSelector();
        refreshBoardSelectorItems();

        workspaceRoot.setTop(buildWorkspaceHeader());
        workspaceRoot.setCenter(boardHost);

        selectBoard(workspace.getActiveBoard());

        Scene scene = new Scene(workspaceRoot, 1280, 820);
        stage.setTitle("Boardganized - " + accountName);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (workspace != null) {
            persistenceService.saveWorkspace(workspace);
        }
    }

    private BoardWorkspace loadWorkspace() {
        BoardWorkspace loadedWorkspace = persistenceService.loadWorkspace();
        if (loadedWorkspace != null) {
            return loadedWorkspace;
        }

        return BoardWorkspace.createDefault(accountName);
    }

    private void ensureWorkspaceHasAtLeastOneBoard() {
        if (workspace.getBoards().isEmpty()) {
            workspace.addBoard(createBoardFromTitle("Boardganized"));
        }

        if (workspace.getActiveBoard() == null) {
            workspace.setActiveBoardId(workspace.getBoards().get(0).getId());
        }
    }

    private BorderPane buildWorkspaceHeader() {
        Label accountLabel = new Label("Compte : " + accountName);
        accountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #243B53;");

        HBox actions = new HBox(10, boardSelector, createBoardButton(), renameBoardButton(), deleteBoardButton());
        actions.setAlignment(Pos.CENTER_RIGHT);

        BorderPane header = new BorderPane();
        header.setPadding(new Insets(14, 18, 14, 18));
        header.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D6DCE5; -fx-border-width: 0 0 1 0;");
        header.setLeft(accountLabel);
        header.setRight(actions);
        return header;
    }

    private ComboBox<Board> buildBoardSelector() {
        ComboBox<Board> selector = new ComboBox<>();
        selector.setPrefWidth(260);
        selector.setCellFactory(listView -> createBoardCell());
        selector.setButtonCell(createBoardCell());
        selector.setConverter(new javafx.util.StringConverter<Board>() {
            @Override
            public String toString(Board board) {
                return board == null ? "" : board.getTitle();
            }

            @Override
            public Board fromString(String string) {
                return null;
            }
        });

        selector.valueProperty().addListener((ChangeListener<Board>) (observable, oldValue, newValue) -> selectBoard(newValue));
        return selector;
    }

    private ListCell<Board> createBoardCell() {
        return new ListCell<Board>() {
            @Override
            protected void updateItem(Board board, boolean empty) {
                super.updateItem(board, empty);
                setText(empty || board == null ? "" : board.getTitle());
            }
        };
    }

    private Button createBoardButton() {
        Button button = new Button("+ Tableau");
        button.setOnAction(event -> createBoard());
        return button;
    }

    private Button renameBoardButton() {
        Button button = new Button("Renommer");
        button.setOnAction(event -> renameSelectedBoard());
        return button;
    }

    private Button deleteBoardButton() {
        Button button = new Button("Supprimer");
        button.setOnAction(event -> deleteSelectedBoard());
        return button;
    }

    private void createBoard() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Nouveau tableau");
        dialog.setHeaderText("Nom du tableau");
        dialog.setContentText("Tableau");

        dialog.showAndWait()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .ifPresent(title -> {
                    Board board = createBoardFromTitle(title);
                    workspace.addBoard(board);
                    workspace.setActiveBoardId(board.getId());
                    refreshBoardSelectorItems();
                    selectBoard(board);
                });
    }

    private void renameSelectedBoard() {
        Board currentBoard = boardSelector.getValue();
        if (currentBoard == null) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog(currentBoard.getTitle());
        dialog.setTitle("Renommer le tableau");
        dialog.setHeaderText("Nouveau nom du tableau");
        dialog.setContentText("Tableau");

        dialog.showAndWait()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .ifPresent(newTitle -> {
                    currentBoard.rename(newTitle);
                    refreshBoardSelectorItems();
                    selectBoard(currentBoard);
                });
    }

    private void deleteSelectedBoard() {
        Board currentBoard = boardSelector.getValue();
        if (currentBoard == null) {
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Supprimer le tableau");
        confirmation.setHeaderText("Supprimer \"" + currentBoard.getTitle() + "\" ?");
        confirmation.setContentText("Cette action supprime le tableau courant. Les autres tableaux du compte restent intacts.");

        confirmation.showAndWait()
                .filter(buttonType -> buttonType == ButtonType.OK)
                .ifPresent(buttonType -> {
                    workspace.removeBoard(currentBoard.getId());

                    if (workspace.getBoards().isEmpty()) {
                        workspace.addBoard(createBoardFromTitle("Boardganized"));
                    }

                    refreshBoardSelectorItems();
                    selectBoard(workspace.getActiveBoard());
                });
    }

    private Board createBoardFromTitle(String title) {
        Board board = new Board(title, "Tableau du compte " + accountName);
        board.addColumns(new Column("A faire"));
        board.addColumns(new Column("En cours"));
        board.addColumns(new Column("Terminé"));
        return board;
    }

    private void refreshBoardSelectorItems() {
        if (boardSelector == null) {
            return;
        }

        boardSelector.setItems(FXCollections.observableArrayList(workspace.getBoards()));
        Board activeBoard = workspace.getActiveBoard();
        if (activeBoard != null) {
            boardSelector.getSelectionModel().select(activeBoard);
        }
    }

    private void selectBoard(Board board) {
        if (board == null) {
            return;
        }

        workspace.setActiveBoardId(board.getId());
        controller = new BoardController(board);
        boardHost.setCenter(new BoardView(controller));

        boardHost.setTop(null);

        if (boardSelector.getValue() != board) {
            boardSelector.getSelectionModel().select(board);
        }
    }

    private String requestAccountName() {
        String defaultAccountName = "invite";
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Ouvrir un compte");
        dialog.setHeaderText("Choisis le nom du compte");
        dialog.setContentText("Compte");

        return dialog.showAndWait()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .orElse(defaultAccountName);
    }
}
