package com.boardganized.domain.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Représente une carte dans une colonne d'un tableau.
 */
public class Card implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private String title;
    private String description;

    /**
     * Crée une carte avec un titre et une description.
     *
     * @param title titre de la carte
     * @param description description de la carte
     */
    public Card(String title, String description) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
    }

    /**
     * Retourne l'identifiant unique de la carte.
     *
     * @return id de la carte
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retourne le titre de la carte.
     *
     * @return titre
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retourne la description de la carte.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Modifie le titre de la carte.
     *
     * @param title nouveau titre
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Modifie la description de la carte.
     *
     * @param description nouvelle description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}