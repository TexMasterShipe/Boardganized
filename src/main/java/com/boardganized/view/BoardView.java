package com.boardganized.view;

import com.boardganized.controller.BoardController;
import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.Card;
import com.boardganized.domain.model.Column;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.UUID;

/**
 * Vue JavaFX du tableau Kanban.
 * Elle construit la structure visuelle du board, des colonnes et des cartes.
 */
public class BoardView extends BorderPane {

    private static final String APP_BACKGROUND = "#F4F6F8";
    private static final String SURFACE = "#FFFFFF";
    private static final String HEADER = "#243B53";
    private static final String SECONDARY = "#52616B";
    private static final String COLUMN_BACKGROUND = "#E8EDF3";
    private static final String CARD_BACKGROUND = "#FFFFFF";
    private static final String CARD_BORDER = "#D6DCE5";
    private static final String ACCENT = "#2F80ED";

    private final BoardController controller;
    private HBox columnsContainer;

    /**
     * Construit la vue du tableau à partir d'un contrôleur.
     *
     * @param controller contrôleur qui fournit les données et les actions du board
     */
    public BoardView(BoardController controller) {
        this.controller = controller;
        initializeView();
    }

    private void initializeView() {
        setStyle("-fx-background-color: " + APP_BACKGROUND + ";");
        setTop(buildHeader());
        setCenter(buildBoardArea());
    }

    private BorderPane buildHeader() {
        Board board = controller.getBoard();

        VBox headerText = new VBox(6);
        headerText.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(board.getTitle());
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: " + HEADER + ";");

        Label subtitle = new Label("Vue graphique du tableau");
        subtitle.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 13px;");

        Label description = new Label(board.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 14px;");

        headerText.getChildren().addAll(title, subtitle, description);

        Region accent = new Region();
        accent.setPrefSize(12, 12);
        accent.setStyle("-fx-background-color: " + ACCENT + "; -fx-background-radius: 999;");

        BorderPane header = new BorderPane();
        header.setPadding(new Insets(20, 24, 20, 24));
        header.setStyle("-fx-background-color: " + SURFACE + ";");
        header.setLeft(accent);
        BorderPane.setMargin(accent, new Insets(6, 16, 0, 0));
        header.setCenter(headerText);
        return header;
    }

    private ScrollPane buildBoardArea() {
        columnsContainer = new HBox(16);
        columnsContainer.setPadding(new Insets(16));
        columnsContainer.setAlignment(Pos.CENTER);

        refreshBoardColumns();

        ScrollPane scrollPane = new ScrollPane(columnsContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: " + APP_BACKGROUND + "; -fx-background-color: transparent;");
        return scrollPane;
    }

    private void refreshBoardColumns() {
        columnsContainer.getChildren().clear();

        for (Column column : controller.getBoard().getColumns()) {
            columnsContainer.getChildren().add(buildColumnPanel(column));
        }
    }

    private VBox buildColumnPanel(Column column) {
        VBox columnPanel = new VBox(12);
        columnPanel.setPrefWidth(300);
        columnPanel.setMinWidth(300);
        columnPanel.setMaxWidth(300);
        columnPanel.setPadding(new Insets(14));
        columnPanel.setStyle("-fx-background-color: " + COLUMN_BACKGROUND + "; -fx-background-radius: 14; -fx-border-color: #D0D7DE; -fx-border-radius: 14;");

        installColumnDropTarget(columnPanel, column);

        Label title = new Label(column.getTitle());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: " + HEADER + ";");

        Button addCardButton = new Button("+ Carte");
        addCardButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT + "; -fx-font-weight: bold;");
        addCardButton.setOnAction(event -> openCardCreator(column));

        HBox columnHeader = new HBox(8, title, new Region(), addCardButton);
        HBox.setHgrow(columnHeader.getChildren().get(1), Priority.ALWAYS);
        columnHeader.setAlignment(Pos.CENTER_LEFT);

        VBox cards = new VBox(12);
        if (column.getCards().isEmpty()) {
            Label empty = new Label("Aucune carte");
            empty.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-style: italic; -fx-font-size: 13px;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            cards.getChildren().add(empty);
        } else {
            for (Card card : column.getCards()) {
                cards.getChildren().add(buildCardPanel(card, column));
            }
        }

        installColumnDropTarget(columnPanel, column);

        columnPanel.getChildren().addAll(columnHeader, cards);
        VBox.setVgrow(cards, Priority.ALWAYS);
        return columnPanel;
    }

    private StackPane buildCardPanel(Card card, Column parentColumn) {
        VBox content = new VBox(6);
        content.setFillWidth(true);

        Label title = new Label(card.getTitle());
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: " + HEADER + ";");
        title.setWrapText(true);

        Label description = new Label(card.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: " + SECONDARY + "; -fx-font-size: 13px;");

        content.getChildren().addAll(title, description);

        StackPane cardPane = new StackPane(content);
        cardPane.setPadding(new Insets(12));
        cardPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-background-radius: 12; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 12;");
        cardPane.setMaxWidth(Double.MAX_VALUE);

        installCardDragSource(cardPane, card);
        installCardDropTarget(cardPane, parentColumn, card);
        installCardEditHandler(cardPane, card);
        return cardPane;
    }

    private void installCardEditHandler(Node cardNode, Card card) {
        cardNode.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openCardEditor(card);
            }
        });
    }

    private void openCardEditor(Card card) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier la carte");
        dialog.setHeaderText("Modifier le titre et la description");

        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField(card.getTitle());
        titleField.setPromptText("Titre");

        TextArea descriptionField = new TextArea(card.getDescription());
        descriptionField.setPromptText("Description");
        descriptionField.setWrapText(true);
        descriptionField.setPrefRowCount(6);

        VBox form = new VBox(10,
                new Label("Titre"),
                titleField,
                new Label("Description"),
                descriptionField);
        form.setPadding(new Insets(10));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return saveButtonType;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(buttonType -> {
            controller.updateCard(card, titleField.getText(), descriptionField.getText());
            refreshBoardColumns();
        });
    }

    private void openCardCreator(Column column) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nouvelle carte");
        dialog.setHeaderText("Créer une carte dans la colonne " + column.getTitle());

        ButtonType saveButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField titleField = new TextField();
        titleField.setPromptText("Titre");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Description");
        descriptionField.setWrapText(true);
        descriptionField.setPrefRowCount(6);

        VBox form = new VBox(10,
                new Label("Titre"),
                titleField,
                new Label("Description"),
                descriptionField);
        form.setPadding(new Insets(10));
        form.setPrefWidth(420);

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                return saveButtonType;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(buttonType -> {
            controller.addCardToColumn(column, titleField.getText(), descriptionField.getText());
            refreshBoardColumns();
        });
    }

    private void installCardDragSource(Node cardNode, Card card) {
        cardNode.setOnDragDetected(event -> {
            Dragboard dragboard = cardNode.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(card.getId().toString());
            dragboard.setContent(content);
            cardNode.setCursor(Cursor.MOVE);
            event.consume();
        });

        cardNode.setOnDragDone(event -> {
            cardNode.setCursor(Cursor.DEFAULT);
            event.consume();
        });
    }

    private void installColumnDropTarget(VBox columnPanel, Column targetColumn) {
        columnPanel.setOnDragOver(event -> {
            if (isCardDrag(event)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        columnPanel.setOnDragEntered(event -> {
            if (isCardDrag(event)) {
                columnPanel.setStyle("-fx-background-color: #DCEBFF; -fx-background-radius: 14; -fx-border-color: #2F80ED; -fx-border-radius: 14;");
            }
            event.consume();
        });

        columnPanel.setOnDragExited(event -> {
            columnPanel.setStyle("-fx-background-color: " + COLUMN_BACKGROUND + "; -fx-background-radius: 14; -fx-border-color: #D0D7DE; -fx-border-radius: 14;");
            event.consume();
        });

        columnPanel.setOnDragDropped(event -> {
            if (!isCardDrag(event)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            String cardIdValue = event.getDragboard().getString();
            boolean moved = controller.moveCardToColumn(cardIdValue, targetColumn, null);
            if (moved) {
                refreshBoardColumns();
            }

            event.setDropCompleted(moved);
            event.consume();
        });
    }

    private void installCardDropTarget(StackPane cardPane, Column targetColumn, Card targetCard) {
        cardPane.setOnDragOver(event -> {
            if (isCardDrag(event)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cardPane.setOnDragEntered(event -> {
            if (isCardDrag(event)) {
                cardPane.setStyle("-fx-background-color: #F8FBFF; -fx-background-radius: 12; -fx-border-color: #2F80ED; -fx-border-width: 2; -fx-border-radius: 12;");
            }
            event.consume();
        });

        cardPane.setOnDragExited(event -> {
            cardPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + "; -fx-background-radius: 12; -fx-border-color: " + CARD_BORDER + "; -fx-border-radius: 12;");
            event.consume();
        });

        cardPane.setOnDragDropped(event -> {
            if (!isCardDrag(event)) {
                event.setDropCompleted(false);
                event.consume();
                return;
            }

            String cardIdValue = event.getDragboard().getString();
            boolean moved = controller.moveCardToColumn(cardIdValue, targetColumn, targetCard.getId());
            if (moved) {
                refreshBoardColumns();
            }

            event.setDropCompleted(moved);
            event.consume();
        });
    }

    private boolean isCardDrag(DragEvent event) {
        return event.getDragboard().hasString();
    }
}
