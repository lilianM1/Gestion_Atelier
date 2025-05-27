package fr.insa.eymin.gestion_atelier.vues;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.AtelierControleur;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Vue pour gérer l'interface utilisateur liée à l'atelier.
 * Gère l'affichage des fenêtres, alertes et dialogues de progression.
 */
public class AtelierVue {

    private AtelierControleur controleur;
    private PrincipalVue principalVue; // Référence à la vue principale, si nécessaire

    /**
     * Constructeur de la vue Atelier
     * 
     * @param controleur   Le contrôleur associé à cette vue
     * @param principalVue La vue principale de l'application
     */
    public AtelierVue(AtelierControleur controleur, PrincipalVue principalVue) {
        this.controleur = controleur;
        this.principalVue = principalVue; // Initialisation de la vue principale
    }

    public AtelierVue() {
        // Constructeur par défaut, peut être utilisé pour des tests ou des
        // initialisations
    }

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
     * Affiche la fenetre de création d'atelier
     */
    public void afficherFenetreCreation() {
        // Création de la fenêtre
        Stage creerAtelierStage = new Stage();
        creerAtelierStage.setTitle("Nouvel atelier");
        creerAtelierStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerAtelierStage.setResizable(false);
        creerAtelierStage.initModality(Modality.APPLICATION_MODAL);

        // Création des champs de texte
        TextField nomAtelierField = new TextField();
        nomAtelierField.setPromptText("Nom de l'atelier");

        Spinner<Integer> longXSpinner = new Spinner<Integer>(1, 1000, 10);
        longXSpinner.setPromptText("Longueur X (m)");
        longXSpinner.setEditable(true); // Permet la saisie manuelle

        // Configuration pour sélectionner tout le texte au focus
        TextField longXEditor = longXSpinner.getEditor();
        longXEditor.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(() -> longXEditor.selectAll());
            }
        });

        Spinner<Integer> longYSpinner = new Spinner<Integer>(1, 1000, 10);
        longYSpinner.setPromptText("Longueur Y (m)");
        longYSpinner.setEditable(true); // Permet la saisie manuelle

        // Configuration pour sélectionner tout le texte au focus
        TextField longYEditor = longYSpinner.getEditor();
        longYEditor.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(() -> longYEditor.selectAll());
            }
        });

        // Création des labels pour les champs
        Label nomAtelierLabel = new Label("Nom de l'atelier :");
        Label longXLabel = new Label("Longueur X (m) :");
        Label longYLabel = new Label("Longueur Y (m) :");

        // Création des boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button annulerButton = new Button("Annuler");

        // Action pour le bouton "Créer"
        creerButton.setOnAction(event -> {
            // Validation des champs
            if (nomAtelierField.getText().isEmpty() || longXSpinner.getValue() <= 0 || longYSpinner.getValue() <= 0) {
                afficherErreur("Erreur", "Champs manquants", "Veuillez remplir tous les champs correctement.");
                return;
            }

            // Vérification de l'existence d'un atelier avec le même nom
            java.io.File saveDir = new java.io.File("src/main/ressources/data/atelier_saves/");
            if (saveDir.exists() && saveDir.isDirectory()) {
                java.io.File[] files = saveDir.listFiles();
                if (files != null) {
                    String nomAtelier = nomAtelierField.getText();
                    for (java.io.File file : files) {
                        if (file.getName().equals(nomAtelier + ".txt") || file.getName().equals(nomAtelier + ".dat")) {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Atelier existant");
                            alert.setHeaderText("Atelier déjà existant");
                            alert.setContentText("Un atelier avec ce nom existe déjà. Veuillez choisir un autre nom.");
                            alert.showAndWait();
                            return; // Arrêter l'exécution ici
                        }
                    }
                }
            }

            // Si on arrive ici, on peut créer l'atelier
            try {
                controleur.creationAtelier(
                        nomAtelierField.getText(),
                        longXSpinner.getValue(),
                        longYSpinner.getValue());

                // Fermer d'abord la fenêtre de création
                creerAtelierStage.close();
                // Puis afficher la fenêtre principale
                principalVue.mainWindow();
                principalVue.dessinerAtelier();

            } catch (Exception e) {
                e.printStackTrace();
                afficherErreur("Erreur", "Erreur de création",
                        "Une erreur est survenue lors de la création de l'atelier : " + e.getMessage());
            }
        });

        // Action pour le bouton "Annuler"
        annulerButton.setOnAction(event -> {
            // Ferme la fenêtre de création d'atelier
            creerAtelierStage.close();
            principalVue.startWindow();
        });

        // Création de la disposition de la fenêtre
        VBox layout = new VBox(20);
        HBox layoutBoutons = new HBox(20);
        GridPane grid = new GridPane();

        // Configuration de la grille pour les champs de saisie
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(nomAtelierLabel, 0, 0);
        grid.add(nomAtelierField, 1, 0);
        grid.add(longXLabel, 0, 1);
        grid.add(longXSpinner, 1, 1);
        grid.add(longYLabel, 0, 2);
        grid.add(longYSpinner, 1, 2);

        // Organisation des boutons et des champs dans le layout
        layoutBoutons.getChildren().addAll(annulerButton, creerButton);
        layout.getChildren().addAll(grid, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création de la scène avec le layout
        Scene scene = new Scene(layout);

        creerAtelierStage.initModality(Modality.APPLICATION_MODAL);
        creerAtelierStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerAtelierStage.setScene(scene);
        creerAtelierStage.show();
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
