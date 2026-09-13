package com.boardganized.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Représente un tableau de travail composé de colonnes et de cartes.
 */
public class Board {

    private final UUID id;
    private String title;
    private String description;
    private List<Column> columns;

    /**
     * Crée un nouveau tableau.
     *
     * @param title titre du tableau
     * @param description description du tableau
     */
    public Board(String title, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.columns = new ArrayList<Column>();
    }

    /**
     * Retourne l'identifiant unique du tableau.
     *
     * @return id du board
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retourne le titre du tableau.
     *
     * @return titre
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retourne la description du tableau.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne la liste des colonnes du tableau.
     *
     * @return colonnes du board
     */
    public List<Column> getColumns(){
        return columns;
    }

    /**
     * Renomme le tableau.
     *
     * @param newTitle nouveau titre
     * @throws IllegalArgumentException si le titre est vide ou nul
     */
    public void rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide");
        }

        this.title = newTitle;
    }

    /**
     * Modifie la description du tableau.
     *
     * @param newDesc nouvelle description
     */
    public void changeDescription(String newDesc){
        this.description = newDesc;
    }

    /**
     * Ajoute une colonne au tableau.
     *
     * @param column colonne à ajouter
     */
    public void addColumns(Column column){
        this.columns.add(column);
    }

    /**
     * Supprime une colonne du tableau.
     *
     * @param column colonne à retirer
     */
    public void removeColumns(Column column){
        this.columns.remove(column);
    }
}