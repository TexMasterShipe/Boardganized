package com.boardganized.domain.model;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une colonne d'un tableau, contenant une liste de cartes.
 */
public class Column {

    private final UUID id;
    private String title;
    List<Card> cards;

    /**
     * Crée une colonne avec un titre donné.
     *
     * @param title titre de la colonne
     */
    public Column(String title) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.cards = new ArrayList<Card>();
    }

    /**
     * Retourne l'identifiant unique de la colonne.
     *
     * @return id de la colonne
     */
    public UUID getId() {
        return id;
    }

    /**
     * Retourne le titre de la colonne.
     *
     * @return titre
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retourne les cartes contenues dans la colonne.
     *
     * @return liste des cartes
     */
    public List<Card> getCards(){
        return cards;
    }

    /**
     * Modifie le titre de la colonne.
     *
     * @param aTitle nouveau titre
     */
    public void editTitle(String aTitle){
        this.title = aTitle;
    }

    /**
     * Ajoute une carte à la colonne.
     *
     * @param card carte à ajouter
     */
    public void addCard(Card card){
        this.cards.add(card);
    }

    /**
     * Supprime une carte de la colonne.
     *
     * @param card carte à retirer
     */
    public void removeCard(Card card){
        this.cards.remove(card);
    }
}