package com.boardganized;

import com.boardganized.controller.BoardController;
import com.boardganized.view.BoardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application Boardganized.
 * Elle initialise le tableau, construit la vue et lance l'interface JavaFX.
 */
public class Main extends Application {

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
        BoardController controller = new BoardController(BoardController.createDemoBoard());
        BoardView view = new BoardView(controller);

        Scene scene = new Scene(view, 1280, 820);
        stage.setTitle("Boardganized");
        stage.setScene(scene);
        stage.show();
    }
}
