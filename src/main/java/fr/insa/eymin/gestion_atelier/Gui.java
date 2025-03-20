package fr.insa.eymin.gestion_atelier;

import java.util.ArrayList;
import fr.insa.eymin.gestion_atelier.classes.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Gui {
    // ========================== Attributs ================================

    // ========================== Constructeurs ============================

    // ========================== Méthodes =================================
    // ---------------------------------------------------------------------
    // Affiche la fenête principale
    public static void mainWindow() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");
        Pane planAtelier = new Pane();

        Label dMach = new Label();
        Label coutHMach = new Label();
        Label dureeMach = new Label();
        Label etatMach = new Label();

        // Création de la barre de menu
        MenuBar menuBar = new MenuBar();

        // ------------------------- Menu "Gestion" -------------------------
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau");
        Menu sousMenuAfficher = new Menu("Afficher");

        MenuItem nouveauProduit = new MenuItem("Produit");
        ArrayList<Produit> produits = new ArrayList<Produit>();
        nouveauProduit.setOnAction(_ -> {
            Produit.creerProduit(produits);
        });

        MenuItem nouveauMachine = new MenuItem("Machine");
        ArrayList<Machine> machines = new ArrayList<Machine>();
        nouveauMachine.setOnAction(_ -> {
            Machine.creerMachine(machines, planAtelier, dMach, coutHMach, dureeMach, etatMach);
        });

        MenuItem afficherMachines = new MenuItem("Machines");
        afficherMachines.setOnAction(_ -> {
            for (Machine m : machines) {
                m.afficherMachine();
            }
            System.out.println();
        });

        MenuItem dessinerAtelier = new MenuItem("Dessiner atelier");
        dessinerAtelier.setOnAction(_ -> {
            Atelier.dessinerAtelier(planAtelier, machines, dMach, coutHMach, dureeMach, etatMach);
        });

        sousMenuNouveau.getItems().addAll(
                nouveauProduit,
                nouveauMachine);
        sousMenuAfficher.getItems().addAll(
                afficherMachines);
        gestionMenu.getItems().addAll(
                sousMenuNouveau,
                sousMenuAfficher,
                dessinerAtelier);

        // ------------------------- Menu "Optimisation" -------------------------
        Menu optimisationMenu = new Menu("Optimisation");
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines");
        optimisationItem.setOnAction(_ -> {
            Atelier.calculFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // ------------------------- Menu "Paramètres" -------------------------
        Menu parametresMenu = new Menu("Paramètres");

        MenuItem fullscreenItem = new MenuItem("Plein écran");
        primaryStage.setFullScreenExitHint("");
        fullscreenItem.setOnAction(_ -> {
            if (primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            } else {
                primaryStage.setFullScreen(true);
            }
        });

        parametresMenu.getItems().addAll(

                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(
                gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène
        BorderPane fenetre = new BorderPane();
        VBox infoDroite = new VBox();

        // Configuration de la partie droite
        infoDroite.getChildren().addAll(dMach, coutHMach, dureeMach, etatMach);
        infoDroite.setPrefWidth(250);
        infoDroite.setStyle("-fx-background-color: #161b22;");
        infoDroite.setPadding(new javafx.geometry.Insets(10));
        infoDroite.setSpacing(10);
        infoDroite.setBorder(new Border(new BorderStroke(javafx.scene.paint.Paint.valueOf("#21262d"),
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        VBox.setVgrow(infoDroite, Priority.SOMETIMES);

        // Création de la disposition
        fenetre.setRight(infoDroite);
        fenetre.setTop(menuBar);
        fenetre.setCenter(planAtelier);
        Scene scene = new Scene(fenetre, 1280, 720);

        // Définition de la scène pour la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\icon.png"); // Assurez-vous que le chemin est correct
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
