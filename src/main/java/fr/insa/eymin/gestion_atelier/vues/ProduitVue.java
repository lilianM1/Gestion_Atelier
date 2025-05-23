package fr.insa.eymin.gestion_atelier.vues;

import atlantafx.base.theme.Styles;
import fr.insa.eymin.gestion_atelier.controleurs.ProduitControleur;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProduitVue {

    private ProduitControleur controleur;

    // Constructeur
    public ProduitVue(ProduitControleur controleur) {
        this.controleur = controleur;
    }

    /**
     * Affiche la fenêtre de création d'un nouveau produit
     */
    public void afficherFenetreCreation() {
        // Création de la fenêtre
        Stage creerProdStage = new Stage();
        creerProdStage.setTitle("Nouveau produit");

        // Création des champs de texte
        TextField codeProduitField = new TextField();
        codeProduitField.setPromptText("codeProduit");
        TextField dProduitField = new TextField();
        dProduitField.setPromptText("dProduit");

        // Création des labels
        Label codeProduitLabel = new Label("Code du produit : ");
        Label dProduitLabel = new Label("Désignation du produit : ");

        // Création des boutons
        Button creerButton = new Button("Créer");
        creerButton.getStyleClass().add(Styles.ACCENT);
        Button terminerButton = new Button("Terminer");

        // Action du bouton Créer
        creerButton.setOnAction(e -> {
            String codeProduit = codeProduitField.getText();
            String dProduit = dProduitField.getText();

            // Appel du contrôleur pour gérer la création du produit
            boolean succes = controleur.creerProduit(codeProduit, dProduit);

            if (succes) {
                // Efface les champs si la création a réussi
                codeProduitField.clear();
                dProduitField.clear();
            }
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> creerProdStage.close());

        // Création de la disposition
        VBox layout = new VBox(20);
        HBox layoutBoutons = new HBox(20);
        GridPane layoutChamps = new GridPane();

        layoutChamps.setHgap(10);
        layoutChamps.setVgap(10);
        layoutChamps.add(codeProduitLabel, 0, 0);
        layoutChamps.add(codeProduitField, 1, 0);
        layoutChamps.add(dProduitLabel, 0, 1);
        layoutChamps.add(dProduitField, 1, 1);

        layoutBoutons.getChildren().addAll(terminerButton, creerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création de la scène
        Scene scene = new Scene(layout);

        // Affichage de la fenêtre
        creerProdStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        creerProdStage.setScene(scene);
        creerProdStage.show();
    }

    /**
     * Affiche une alerte d'erreur
     */
    public void afficherErreur(String titre, String entete, String contenu) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}
