package fr.insa.eymin.gestion_atelier.vues;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vue pour gérer l'interface utilisateur liée à l'atelier.
 * Gère l'affichage des fenêtres, alertes et dialogues de progression.
 */
public class AtelierVue {

    /**
     * Affiche une fenêtre de progression modale pendant le calcul
     * 
     * @return La fenêtre de progression créée
     */
    public Stage afficherFenetreCalculEnCours() {
        // Création de la fenêtre modale qui bloque l'interaction avec l'application
        // principale
        Stage loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle("Calcul...");

        // Barre de progression pour indiquer visuellement que le traitement est en
        // cours
        ProgressBar progressBar = new ProgressBar();
        StackPane root = new StackPane(progressBar);
        root.setPrefSize(220, 50); // Dimensions de la fenêtre de progression

        Scene scene = new Scene(root);
        loadingStage.setScene(scene);

        // Affichage de la fenêtre
        loadingStage.show();

        return loadingStage;
    }

    /**
     * Lie une barre de progression à une tâche asynchrone
     */
    public void lierProgressionTache(Stage stage, Task<Void> task) {
        // Récupération de la barre de progression dans la fenêtre
        ProgressBar progressBar = (ProgressBar) ((StackPane) stage.getScene().getRoot()).getChildren().get(0);

        // Liaison de la barre de progression à l'état d'avancement de la tâche
        progressBar.progressProperty().bind(task.progressProperty());

        // Fermeture automatique de la fenêtre une fois le calcul terminé
        task.setOnSucceeded(event -> stage.close());
    }

    /**
     * Ferme une fenêtre donnée
     */
    public void fermerFenetre(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Affiche une alerte d'erreur
     */
    public void afficherErreur(String titre, String entete, String contenu) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(titre);
            alert.setHeaderText(entete);
            alert.setContentText(contenu);
            alert.showAndWait();
        });
    }

    /**
     * Affiche une alerte d'information
     * 
     * @param titre   Le titre de l'alerte
     * @param entete  L'en-tête de l'alerte
     * @param contenu Le contenu détaillé de l'alerte
     */
    public void afficherInfo(String titre, String entete, String contenu) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titre);
            alert.setHeaderText(entete);
            alert.setContentText(contenu);
            alert.showAndWait();
        });
    }

    /**
     * Tente d'ouvrir un fichier avec l'application par défaut du système
     * 
     * @param cheminFichier Le chemin du fichier à ouvrir
     */
    public void ouvrirFichier(String cheminFichier) {
        try {
            File fichier = new File(cheminFichier);
            if (fichier.exists()) {
                if (Desktop.isDesktopSupported()) {
                    // Ouverture du fichier avec l'application par défaut du système
                    Desktop.getDesktop().open(fichier);
                } else {
                    // Message d'erreur si l'API Desktop n'est pas supportée par le système
                    afficherErreur(
                            "Erreur",
                            "Erreur d'ouverture",
                            "Impossible d'ouvrir le fichier automatiquement (Desktop non supporté)");
                }
            } else {
                // Message d'erreur si le fichier n'a pas été créé correctement
                afficherErreur(
                        "Erreur",
                        "Fichier non trouvé",
                        "Le fichier n'a pas été trouvé à l'emplacement : " + cheminFichier);
            }
        } catch (IOException e) {
            // Gestion d'erreur lors de l'ouverture du fichier
            afficherErreur(
                    "Erreur",
                    "Erreur d'ouverture",
                    "Erreur lors de l'ouverture du fichier : " + e.getMessage());
        }
    }
}
