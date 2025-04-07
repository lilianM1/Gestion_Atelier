package fr.insa.eymin.gestion_atelier.classes;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Produit {
    // ========================== Attributs ================================

    private String codeProduit;
    private String dProduit; // Désignation du produit

    // ========================== Constructeurs ============================

    public Produit() {
        this.codeProduit = null;
        this.dProduit = null;
    }

    public Produit(String codeProduit, String dProduit) {
        this.codeProduit = codeProduit;
        this.dProduit = dProduit;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations du produit
    public void afficherProduit() {
        System.out.println("codeProduit = " + codeProduit + ", dProduit = " + dProduit + "\n");
    }

    // ---------------------------------------------------------------------
    // Supprime le produit
    public void supprimerProduit() {
        this.codeProduit = null;
        this.dProduit = null;
    }

    // ---------------------------------------------------------------------
    // Créer un produit
    public static ArrayList<Produit> creerProduit(ArrayList<Produit> produits) {
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
        Button validerButton = new Button("Valider");
        Button annulerButton = new Button("Annuler");

        // Action du bouton Valider
        validerButton.setOnAction(e -> {
            try {
                if (codeProduitField.getText().isEmpty() || dProduitField.getText().isEmpty()) {
                    throw new Exception("Veuillez remplir tous les champs");
                }
                String codeProduit = codeProduitField.getText();
                String dProduit = dProduitField.getText();
                Produit produit = new Produit(codeProduit, dProduit);
                produit.afficherProduit();
                creerProdStage.close();
            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Champs vides");
                alert.setContentText("Veuillez remplir tous les champs.");
                alert.showAndWait();
                return;
            }
        });

        // Action du bouton Annuler
        annulerButton.setOnAction(e -> creerProdStage.close());

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

        layoutBoutons.getChildren().addAll(annulerButton, validerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création de la scène
        Scene scene = new Scene(layout);

        // Affichage de la fenêtre
        creerProdStage.getIcons().add(new Image("file:src\\main\\ressources\\icon.png"));
        creerProdStage.setScene(scene);
        creerProdStage.show();
        return produits;
    }

    // ========================== Getters/Setters ==========================

    public String getCodeProduit() {
        return codeProduit;
    }

    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }

    public String getdProduit() {
        return dProduit;
    }

    public void setdProduit(String dProduit) {
        this.dProduit = dProduit;
    }

}
