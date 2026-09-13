package com.boardganized.controller;

import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.Card;
import com.boardganized.domain.model.Column;

import java.util.UUID;

/**
 * Contrôleur principal du tableau.
 * Il centralise les actions métier liées au board, aux colonnes et aux cartes.
 */
public class BoardController {

    private final Board board;

    /**
     * Construit un contrôleur associé à un tableau donné.
     *
     * @param board tableau manipulé par le contrôleur
     */
    public BoardController(Board board) {
        this.board = board;
    }

    /**
     * Retourne le tableau courant.
     *
     * @return instance du board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Ajoute une nouvelle carte dans une colonne donnée.
     *
     * @param column colonne cible
     * @param title titre de la carte
     * @param description description de la carte
     */
    public void addCardToColumn(Column column, String title, String description) {
        String newTitle = title == null ? "" : title.trim();
        String newDescription = description == null ? "" : description.trim();

        if (!newTitle.isBlank()) {
            column.addCard(new Card(newTitle, newDescription));
        }
    }

    /**
     * Met à jour le titre et la description d'une carte existante.
     *
     * @param card carte à modifier
     * @param title nouveau titre
     * @param description nouvelle description
     */
    public void updateCard(Card card, String title, String description) {
        String newTitle = title == null ? "" : title.trim();
        String newDescription = description == null ? "" : description.trim();

        if (!newTitle.isBlank()) {
            card.setTitle(newTitle);
        }

        card.setDescription(newDescription);
    }

    /**
     * Déplace une carte vers une colonne cible, éventuellement avant une autre carte.
     *
     * @param cardIdValue identifiant de la carte à déplacer
     * @param targetColumn colonne de destination
     * @param insertBeforeCardId identifiant de la carte avant laquelle insérer, ou null pour la fin
     * @return true si le déplacement a été effectué, false sinon
     */
    public boolean moveCardToColumn(String cardIdValue, Column targetColumn, UUID insertBeforeCardId) {
        if (cardIdValue == null || cardIdValue.isBlank()) {
            return false;
        }

        UUID cardId;
        try {
            cardId = UUID.fromString(cardIdValue);
        } catch (IllegalArgumentException exception) {
            return false;
        }

        CardLocation location = findCardLocation(cardId);
        if (location == null) {
            return false;
        }

        if (location.column == targetColumn) {
            location.column.getCards().remove(location.card);
            insertCard(targetColumn, location.card, insertBeforeCardId);
            return true;
        }

        location.column.getCards().remove(location.card);
        insertCard(targetColumn, location.card, insertBeforeCardId);
        return true;
    }

    /**
     * Insère une carte dans une colonne à la position demandée.
     *
     * @param targetColumn colonne cible
     * @param card carte à insérer
     * @param insertBeforeCardId identifiant de la carte avant laquelle insérer, ou null pour ajouter à la fin
     */
    private void insertCard(Column targetColumn, Card card, UUID insertBeforeCardId) {
        if (insertBeforeCardId == null) {
            targetColumn.getCards().add(card);
            return;
        }

        int insertIndex = 0;
        for (Card existingCard : targetColumn.getCards()) {
            if (existingCard.getId().equals(insertBeforeCardId)) {
                targetColumn.getCards().add(insertIndex, card);
                return;
            }
            insertIndex++;
        }

        targetColumn.getCards().add(card);
    }

    /**
     * Recherche la colonne et la carte correspondant à un identifiant donné.
     *
     * @param cardId identifiant de la carte
     * @return emplacement de la carte dans le board, ou null si introuvable
     */
    private CardLocation findCardLocation(UUID cardId) {
        for (Column column : board.getColumns()) {
            for (Card card : column.getCards()) {
                if (card.getId().equals(cardId)) {
                    return new CardLocation(column, card);
                }
            }
        }
        return null;
    }

    /**
     * Crée un tableau de démonstration vide, prêt à être rempli par l'utilisateur.
     *
     * @return board initialisé avec trois colonnes vides
     */
    public static Board createDemoBoard() {
        Board board = new Board("Boardganized", "Un tableau vierge, prêt à être rempli par l'utilisateur.");

        Column aFaire = new Column("A faire");
        Column enCours = new Column("En cours");
        Column termine = new Column("Terminé");

        board.addColumns(aFaire);
        board.addColumns(enCours);
        board.addColumns(termine);

        return board;
    }

    /**
     * Représente la localisation d'une carte dans le board.
     */
    private static final class CardLocation {
        private final Column column;
        private final Card card;

        /**
         * Construit une localisation à partir d'une colonne et d'une carte.
         *
         * @param column colonne contenant la carte
         * @param card carte concernée
         */
        private CardLocation(Column column, Card card) {
            this.column = column;
            this.card = card;
        }
    }
}
