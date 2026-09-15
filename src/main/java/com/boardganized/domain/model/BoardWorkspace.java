package com.boardganized.domain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Représente l'ensemble des tableaux d'un compte.
 */
public class BoardWorkspace implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String accountName;
    private final List<Board> boards;
    private UUID activeBoardId;

    /**
     * Crée un espace de travail vide pour un compte donné.
     *
     * @param accountName nom du compte
     */
    public BoardWorkspace(String accountName) {
        this.id = UUID.randomUUID();
        this.accountName = accountName;
        this.boards = new ArrayList<>();
    }

    /**
     * Crée un espace de travail initialisé avec un tableau de démonstration.
     *
     * @param accountName nom du compte
     * @return espace de travail prêt à l'emploi
     */
    public static BoardWorkspace createDefault(String accountName) {
        BoardWorkspace workspace = new BoardWorkspace(accountName);
        workspace.addBoard(com.boardganized.controller.BoardController.createDemoBoard());
        return workspace;
    }

    /**
     * Retourne l'identifiant unique de l'espace de travail.
     *
     * @return identifiant
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retourne le nom du compte associé.
     *
     * @return nom du compte
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Retourne les tableaux enregistrés pour ce compte.
     *
     * @return liste des tableaux
     */
    public List<Board> getBoards() {
        return boards;
    }

    /**
     * Retourne le tableau actuellement sélectionné.
     *
     * @return tableau actif ou null si aucun n'existe
     */
    public Board getActiveBoard() {
        if (activeBoardId != null) {
            for (Board board : boards) {
                if (board.getId().equals(activeBoardId)) {
                    return board;
                }
            }
        }

        if (boards.isEmpty()) {
            return null;
        }

        return boards.get(0);
    }

    /**
     * Retourne l'identifiant du tableau actif.
     *
     * @return identifiant du tableau actif
     */
    public UUID getActiveBoardId() {
        return activeBoardId;
    }

    /**
     * Met à jour le tableau actif.
     *
     * @param activeBoardId identifiant du tableau actif
     */
    public void setActiveBoardId(UUID activeBoardId) {
        if (activeBoardId == null) {
            this.activeBoardId = null;
            return;
        }

        for (Board board : boards) {
            if (board.getId().equals(activeBoardId)) {
                this.activeBoardId = activeBoardId;
                return;
            }
        }
    }

    /**
     * Ajoute un nouveau tableau au workspace.
     *
     * @param board tableau à ajouter
     */
    public void addBoard(Board board) {
        if (board == null) {
            return;
        }

        boards.add(board);
        if (activeBoardId == null) {
            activeBoardId = board.getId();
        }
    }

    /**
     * Retire un tableau du workspace.
     *
     * @param boardId identifiant du tableau à retirer
     * @return true si le tableau a été retiré
     */
    public boolean removeBoard(UUID boardId) {
        if (boardId == null) {
            return false;
        }

        for (int index = 0; index < boards.size(); index++) {
            Board board = boards.get(index);
            if (!board.getId().equals(boardId)) {
                continue;
            }

            boards.remove(index);
            if (boardId.equals(activeBoardId)) {
                activeBoardId = boards.isEmpty() ? null : boards.get(0).getId();
            }
            return true;
        }

        return false;
    }

    /**
     * Renomme le compte associé à ce workspace.
     *
     * @param accountName nouveau nom de compte
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}