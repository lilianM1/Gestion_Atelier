package fr.insa.eymin.gestion_atelier.vues;

import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.OperationControleur;
import fr.insa.eymin.gestion_atelier.modeles.Equipement;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Vue pour gérer l'affichage et les interactions liées aux opérations.
 * Permet de créer de nouvelles opérations et d'afficher des erreurs.
 */
public class OperationVue {

    private OperationControleur controleur;

    /**
     * Constructeur de la vue d'opérations
     * 
     * @param controleur le contrôleur associé
     */
    public OperationVue(OperationControleur controleur) {
        this.controleur = controleur;
    }

    /**
     * Affiche la fenêtre de création d'une nouvelle opération
     */
    public void afficherFenetreCreation() {
        // Création de la fenêtre
        Stage creerOpStage = new Stage();
        creerOpStage.setTitle("Nouvelle opération");

        // Création des champs de texte
        TextField refOperationField = new TextField();
        refOperationField.setPromptText("Référence de l'opération");
        TextField dOperationField = new TextField();
        dOperationField.setPromptText("Désignation de l'opération");

        // Création des labels
        Label refOperationLabel = new Label("Référence : ");
        Label dOperationLabel = new Label("Désignation : ");
        Label equipementLabel = new Label("Équipement : ");

        // Création d'une liste d'équipements pour le combobox
        ObservableList<Equipement> equipements = FXCollections.observableArrayList();
        for (Machine machine : controleur.getMachines()) {
            equipements.add(machine);
        }
        for (Poste poste : controleur.getPostes()) {
            equipements.add(poste);
        }

        // Création du combobox pour sélectionner un équipement
        ComboBox<Equipement> equipementComboBox = new ComboBox<>(equipements);
        equipementComboBox.setPromptText("Sélectionner un équipement");

        // Affichage personnalisé des équipements dans le combobox
        equipementComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Equipement>() {
            @Override
            protected void updateItem(Equipement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRefEquipement() + " - " + item.getdEquipement());
                }
            }
        });

        equipementComboBox.setButtonCell(new javafx.scene.control.ListCell<Equipement>() {
            @Override
            protected void updateItem(Equipement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getRefEquipement() + " - " + item.getdEquipement());
                }
            }
        });

        // Création des boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Terminer");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            String refOperation = refOperationField.getText();
            String dOperation = dOperationField.getText();
            Equipement equipement = equipementComboBox.getValue();

            // Appel du contrôleur pour gérer la création de l'opération
            boolean succes = controleur.creerOperation(refOperation, dOperation, equipement);

            if (succes) {
                // Efface les champs si la création a réussi
                refOperationField.clear();
                dOperationField.clear();
                equipementComboBox.getSelectionModel().clearSelection();
            }
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> creerOpStage.close());

        // Création de la disposition
        VBox layout = new VBox(20);
        HBox layoutBoutons = new HBox(20);
        GridPane layoutChamps = new GridPane();

        layoutChamps.setHgap(10);
        layoutChamps.setVgap(10);
        layoutChamps.add(refOperationLabel, 0, 0);
        layoutChamps.add(refOperationField, 1, 0);
        layoutChamps.add(dOperationLabel, 0, 1);
        layoutChamps.add(dOperationField, 1, 1);
        layoutChamps.add(equipementLabel, 0, 3);
        layoutChamps.add(equipementComboBox, 1, 3);

        layoutBoutons.getChildren().addAll(terminerButton, creerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création de la scène
        Scene scene = new Scene(layout);

        // Affichage de la fenêtre
        creerOpStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerOpStage.setScene(scene);
        creerOpStage.show();
    }

    /**
     * Affiche une alerte d'erreur
     * 
     * @param titre   le titre de l'alerte
     * @param entete  l'en-tête de l'alerte
     * @param contenu le contenu de l'alerte
     */
    public void afficherErreur(String titre, String entete, String contenu) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}