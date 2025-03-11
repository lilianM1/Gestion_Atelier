package fr.insa.eymin.gestion_atelier;

import atlantafx.base.theme.*;
import fr.insa.eymin.gestion_atelier.classes.Atelier;
import fr.insa.eymin.gestion_atelier.classes.Produit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class Gui {
    // ========================== Attributs ================================

    // ========================== Constructeurs ============================

    // ========================== Méthodes =================================
    // ---------------------------------------------------------------------
    // Affiche le menu principal
    public static void mainMenu() {
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");

        // Création de la barre de menu
        MenuBar menuBar = new MenuBar();

        // ------------------------- Menu "Gestion" -------------------------
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau");

        MenuItem nouveauProduit = new MenuItem("Produit");
        nouveauProduit.setOnAction(e -> {
            Produit.creerProduit();
        });

        sousMenuNouveau.getItems().addAll(nouveauProduit);
        gestionMenu.getItems().addAll(
                sousMenuNouveau);

        // ------------------------- Menu "Optimisation" -------------------------
        Menu optimisationMenu = new Menu("Optimisation");
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines");
        optimisationItem.setOnAction(e -> {
            Atelier.calculFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // ------------------------- Menu "Paramètres" -------------------------
        Menu parametresMenu = new Menu("Paramètres");

        // Sous-menu "Apparence"
        Menu themesMenu = new Menu("Apparence");
        MenuItem themeCupertinoDark = new MenuItem("Cupertino Dark");
        themeCupertinoDark.setOnAction(e -> {
            Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
        });
        MenuItem themeCupertinoLight = new MenuItem("Cupertino Light");
        themeCupertinoLight.setOnAction(e -> {
            Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        });
        MenuItem themeDark = new MenuItem("Dark");
        themeDark.setOnAction(e -> {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        });
        MenuItem themeLight = new MenuItem("Light");
        themeLight.setOnAction(e -> {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        });
        themesMenu.getItems().addAll(themeCupertinoDark, themeCupertinoLight, themeDark, themeLight);
        MenuItem themeNordDark = new MenuItem("Nord Dark");
        themeNordDark.setOnAction(e -> {
            Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());
        });
        themesMenu.getItems().add(themeNordDark);
        MenuItem themeNordLight = new MenuItem("Nord Light");
        themeNordLight.setOnAction(e -> {
            Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        });
        themesMenu.getItems().add(themeNordLight);
        MenuItem themeDracula = new MenuItem("Dracula");
        themeDracula.setOnAction(e -> {
            Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        });
        themesMenu.getItems().add(themeDracula);

        MenuItem fullscreenItem = new MenuItem("Plein écran");
        primaryStage.setFullScreenExitHint("");
        fullscreenItem.setOnAction(e -> {
            if (primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            } else {
                primaryStage.setFullScreen(true);
            }
        });

        parametresMenu.getItems().addAll(
                themesMenu,
                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène
        Scene scene = new Scene(new VBox(), 1280, 720);
        ((VBox) scene.getRoot()).getChildren().add(menuBar);

        // Définition de la scène pour la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\icon.png"); // Assurez-vous que le chemin est correct
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

}
