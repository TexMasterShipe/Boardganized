package com.boardganized.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Locale;

import com.boardganized.domain.model.Board;
import com.boardganized.domain.model.BoardWorkspace;

/**
 * Gère la sauvegarde et le chargement local du board.
 */
public class BoardPersistenceService {

    private final Path storageFile;

    /**
     * Crée un service de persistance avec le chemin local par défaut.
     */
    public BoardPersistenceService() {
        this(Path.of(System.getProperty("user.home"), ".boardganized", "accounts", "invite.ser"));
    }

    /**
     * Crée un service de persistance dédié à un compte.
     *
     * @param accountName nom du compte
     */
    public BoardPersistenceService(String accountName) {
        this(resolveAccountStorageFile(accountName));
    }

    /**
     * Crée un service de persistance avec un chemin explicite.
     *
     * @param storageFile fichier de stockage du board
     */
    public BoardPersistenceService(Path storageFile) {
        this.storageFile = storageFile;
    }

    /**
     * Charge un board depuis le stockage local.
     *
     * @return board chargé, ou null si aucun état ne peut être restauré
     */
    public Board load() {
        BoardWorkspace workspace = loadWorkspace();
        if (workspace == null) {
            return null;
        }

        return workspace.getActiveBoard();
    }

    /**
     * Charge l'espace de travail complet depuis le stockage local.
     *
     * @return espace de travail chargé, ou null si aucun état ne peut être restauré
     */
    public BoardWorkspace loadWorkspace() {
        if (!Files.exists(storageFile)) {
            return null;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(storageFile))) {
            Object object = inputStream.readObject();
            if (BoardWorkspace.class.isInstance(object)) {
                return BoardWorkspace.class.cast(object);
            }

            if (Board.class.isInstance(object)) {
                Board legacyBoard = Board.class.cast(object);
                BoardWorkspace workspace = new BoardWorkspace("invite");
                workspace.addBoard(legacyBoard);
                workspace.setActiveBoardId(legacyBoard.getId());
                return workspace;
            }
        } catch (IOException | ClassNotFoundException exception) {
            return null;
        }

        return null;
    }

    /**
     * Sauvegarde le board courant sur disque.
     *
     * @param board board à persister
     */
    public void save(Board board) {
        BoardWorkspace workspace = new BoardWorkspace("invite");
        workspace.addBoard(board);
        saveWorkspace(workspace);
    }

    /**
     * Sauvegarde l'espace de travail complet sur disque.
     *
     * @param workspace espace de travail à persister
     */
    public void saveWorkspace(BoardWorkspace workspace) {
        try {
            Files.createDirectories(storageFile.getParent());
            try (ObjectOutputStream outputStream = new ObjectOutputStream(Files.newOutputStream(storageFile))) {
                outputStream.writeObject(workspace);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Impossible de sauvegarder le board", exception);
        }
    }

    private static String sanitizeAccountName(String accountName) {
        if (accountName == null) {
            return "invite";
        }

        String normalizedName = Normalizer.normalize(accountName.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]+", "_")
                .replaceAll("_+", "_");

        String cleanedName = normalizedName.replaceAll("^[_.-]+|[_.-]+$", "");
        if (cleanedName.isBlank()) {
            return "invite";
        }

        return cleanedName;
    }

    private static Path resolveAccountStorageFile(String accountName) {
        Path accountsDirectory = Path.of(System.getProperty("user.home"), ".boardganized", "accounts");
        Path legacyFile = accountsDirectory.resolve(sanitizeAccountName(accountName) + ".ser");
        Path encodedFile = accountsDirectory.resolve(encodeAccountKey(accountName) + ".ser");

        if (Files.exists(legacyFile)) {
            return legacyFile;
        }

        if (Files.exists(encodedFile)) {
            return encodedFile;
        }

        return encodedFile;
    }

    private static String encodeAccountKey(String accountName) {
        if (accountName == null || accountName.isBlank()) {
            return "invite";
        }

        String normalizedName = Normalizer.normalize(accountName.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(normalizedName.getBytes(StandardCharsets.UTF_8));
    }
}