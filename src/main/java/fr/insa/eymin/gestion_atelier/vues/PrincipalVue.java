package fr.insa.eymin.gestion_atelier.vues;

import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;

import fr.insa.eymin.gestion_atelier.controleurs.PrincipalControleur;
import fr.insa.eymin.gestion_atelier.modeles.*;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import javafx.scene.input.KeyCombination;
import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import atlantafx.base.util.BBCodeParser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import javafx.util.Duration;

/**
 * Classe Gui qui gère l'interface graphique de l'application de gestion
 * d'atelier
 * Cette classe contient les méthodes pour l'affichage et l'interaction avec
 * l'interface utilisateur
 */
public class PrincipalVue extends StackPane {
    // ========================== Attributs ================================
    private Stage primaryStage; // Fenêtre principale de l'application
    private Pane planAtelier; // Plan pour dessiner l'atelier
    private TextField refMach, dMach, coutHMach, dureeMach; // Champs pour les info de la machine
    private ComboBox<EtatMachine> etatMach; // Combo box pour l'état de la machine

    private ArrayList<Machine> machines = new ArrayList<>();
    private ArrayList<Produit> produits = new ArrayList<>();
    private ArrayList<Poste> postes = new ArrayList<>();
    private ArrayList<Operation> operations = new ArrayList<>();

    private PrincipalControleur controleur;

    private StackPane rootContainer; // Conteneur principal

    // ========================== Constructeurs ============================
    public PrincipalVue() {
        this.planAtelier = new Pane();
        this.controleur = new PrincipalControleur(this, machines, produits, postes, operations);
        this.rootContainer = new StackPane();
    }

    // ========================== Méthodes =================================
    /**
     * Méthode qui affiche la fenêtre principale de l'application
     * Crée et configure l'interface utilisateur avec menus, boutons et zones
     * d'affichage
     */
    public void mainWindow() {
        // Création de la fenêtre principale
        primaryStage = new Stage();
        primaryStage.setTitle("Gestion d'atelier");

        // Création des champs de texte pour afficher les informations de la machine
        // sélectionnée
        // Ces champs sont initialement en lecture seule
        refMach = new TextField();
        refMach.setEditable(false);
        refMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de référence, on le sélectionne
            PrincipalControleur.selectChamp(refMach);
        });
        dMach = new TextField();
        dMach.setEditable(false);
        dMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de désignation, on le sélectionne
            PrincipalControleur.selectChamp(dMach);
        });
        coutHMach = new TextField();
        coutHMach.setEditable(false);
        coutHMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de coût horaire, on le sélectionne
            PrincipalControleur.selectChamp(coutHMach);
        });
        dureeMach = new TextField();
        dureeMach.setEditable(false);
        dureeMach.setOnMouseClicked(event -> {
            // Lorsqu'on clique sur le champ de durée d'utilisation, on le sélectionne
            PrincipalControleur.selectChamp(dureeMach);
        });

        // ComboBox pour l'état de la machine, initialement désactivée
        etatMach = new ComboBox<EtatMachine>();
        etatMach.getItems().addAll(EtatMachine.values());
        etatMach.setDisable(true);

        // Création de la barre de menu principale
        MenuBar menuBar = new MenuBar();

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Gestion" =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        Menu gestionMenu = new Menu("Gestion");
        Menu sousMenuNouveau = new Menu("Nouveau", new FontIcon(Feather.PLUS));
        Menu sousMenuAfficher = new Menu("Afficher");

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-= Menu "Fichier" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu fichierMenu = new Menu("Fichier");
        MenuItem nvAtelier = new MenuItem("Nouvel Atelier");
        nvAtelier.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        // TODO: ajouter l'action pour créer un nouvel atelier
        MenuItem ouvrirAtelier = new MenuItem("Ouvrir Atelier", new FontIcon(Feather.FOLDER));
        ouvrirAtelier.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        // TODO: ajouter l'action pour ouvrir un atelier
        MenuItem saveAtelier = new MenuItem("Sauvegarder Atelier", new FontIcon(Feather.SAVE));
        saveAtelier.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        // TODO: ajouter l'action pour sauvegarder l'atelier
        saveAtelier.setOnAction(e -> {
            // controleur.sauvegarderAtelier();
        });
        MenuItem saveAsAtelier = new MenuItem("Sauvegarder sous");
        saveAsAtelier.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN));
        // TODO: ajouter l'action pour sauvegarder sous
        MenuItem quitter = new MenuItem("Quitter");
        quitter.setOnAction(e -> {
            // Action pour quitter l'application
            System.exit(0);
        });

        fichierMenu.getItems().addAll(
                nvAtelier,
                new SeparatorMenuItem(),
                ouvrirAtelier,
                new SeparatorMenuItem(),
                saveAtelier,
                saveAsAtelier,
                new SeparatorMenuItem(),
                quitter);

        // ======================================================================

        // ------------------------- Sous-menu "Nouveau" -------------------------
        // Option pour créer un nouveau produit
        MenuItem nouveauProduit = new MenuItem("Produit");
        nouveauProduit.setOnAction(e -> {
            controleur.creerProduit();
        });

        // Option pour créer une nouvelle machine
        MenuItem nouveauMachine = new MenuItem("Machine");
        nouveauMachine.setOnAction(e -> {
            controleur.creerMachine();
        });

        // Option pour créer un nouveau poste de travail
        MenuItem nouveauPoste = new MenuItem("Poste de travail");
        nouveauPoste.setOnAction(e -> {
            controleur.creerPoste();
        });

        // Création du sous-menu "Equipement" regroupant machines et postes
        Menu sousMenuNvEq = new Menu("Equipement");
        sousMenuNvEq.getItems().addAll(
                nouveauMachine,
                nouveauPoste);

        // Option pour créer une nouvelle opération
        MenuItem nouvelleOperation = new MenuItem("Opération");
        nouvelleOperation.setOnAction(e -> {
            controleur.creerOperation();
        });

        // Ajout des items au menu "Nouveau"
        sousMenuNouveau.getItems().addAll(
                sousMenuNvEq,
                nouveauProduit,
                nouvelleOperation);

        // ------------------------- Sous-menu "Afficher" -------------------------
        // Option pour afficher la liste des machines
        MenuItem afficherMachines = new MenuItem("Machines");
        afficherMachines.setOnAction(e -> {
            controleur.afficherMachines();
        });

        // Option pour afficher la liste des produits
        MenuItem afficherProduits = new MenuItem("Produits");
        afficherProduits.setOnAction(e -> {
            controleur.afficherProduits();
        });

        // Option pour afficher la liste des postes
        MenuItem afficherPostes = new MenuItem("Postes");
        afficherPostes.setOnAction(e -> {
            controleur.afficherPostes();
        });

        // Option pour afficher la liste des opérations
        MenuItem afficherOperations = new MenuItem("Opérations");
        afficherOperations.setOnAction(e -> {
            controleur.afficherOperations();
        });

        // Ajout des items au menu "Afficher"
        sousMenuAfficher.getItems().addAll(
                afficherProduits,
                afficherMachines,
                afficherPostes,
                afficherOperations);

        // ------------------------- Menu "Dessiner" -----------------------------
        // Option pour dessiner l'atelier dans le panneau central
        // MenuItem dessinerAtelier = new MenuItem("Dessiner atelier");
        // dessinerAtelier.setOnAction(e -> {
        // controleur.dessinerAtelier();
        // });

        // -----------------------------------------------------------------------
        // Ajout des sous-menus au menu "Gestion"
        // gestionMenu.getItems().addAll(
        // sousMenuNouveau,
        // sousMenuAfficher,
        // dessinerAtelier);
        gestionMenu.getItems().addAll(
                sousMenuNouveau,
                sousMenuAfficher);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Optimisation" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu optimisationMenu = new Menu("Optimisation");

        // Option pour calculer la fiabilité des machines
        MenuItem optimisationItem = new MenuItem("Calcul fiabilité machines",
                new FontIcon(Material2AL.ANALYTICS));
        optimisationItem.setOnAction(e -> {
            controleur.calculerFiabilite();
        });
        optimisationMenu.getItems().add(optimisationItem);

        // =-=-=-=-=-=-=-=-=-=-=-=-= Menu "Paramètres" =-=-=-=-=-=-=-=-=-=-=-=-=
        Menu parametresMenu = new Menu("Paramètres");

        // Option pour basculer en mode plein écran
        MenuItem fullscreenItem = new MenuItem("Plein écran", new FontIcon(Feather.MAXIMIZE));
        fullscreenItem.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        primaryStage.setFullScreenExitHint(""); // Masque le message d'indication pour quitter le plein écran
        fullscreenItem.setOnAction(e -> {
            controleur.basculerPleinEcran();
        });

        parametresMenu.getItems().addAll(
                fullscreenItem);

        // ---------------------------------------------------------------------
        // Ajout des menus à la barre de menu
        menuBar.getMenus().addAll(
                fichierMenu,
                gestionMenu,
                optimisationMenu,
                parametresMenu);

        // Création de la scène principale avec un BorderPane comme layout
        BorderPane fenetre = new BorderPane();

        // Configuration de la partie droite (panneau d'informations et de contrôle)
        // Boutons pour modifier et supprimer des équipements
        Button modifierButton = new Button("Modifier");
        Button supprimerButton = new Button("Supprimer");

        // Menu déroulant pour créer de nouveaux équipements
        MenuButton creerEq = new MenuButton("Nouveau");
        MenuItem creerMachine = new MenuItem("Machine");
        creerMachine.setOnAction(e -> {
            controleur.creerMachine();
        });
        MenuItem creerPoste = new MenuItem("Poste de travail");
        creerPoste.setOnAction(e -> {
            controleur.creerPoste();
        });
        MenuItem creerProduit = new MenuItem("Produit");
        creerProduit.setOnAction(e -> {
            controleur.creerProduit();
        });
        MenuItem creerOperation = new MenuItem("Opération");
        creerOperation.setOnAction(e -> {
            controleur.creerOperation();
        });
        Menu sousMenu = new Menu("Equipement");
        sousMenu.getItems().addAll(creerMachine, creerPoste);

        creerEq.getItems().addAll(sousMenu, creerProduit, creerOperation);

        Button btn = new Button("SHOW");

        // Organisation horizontale des boutons
        HBox boutonsHb = new HBox(modifierButton, supprimerButton, creerEq, btn);
        boutonsHb.setSpacing(10);

        // Configuration du bas du panneau d'informations
        VBox basInfo = new VBox();
        // TODO: ajouter des informations supplémentaires sur les gammes

        basInfo.getChildren().addAll(
                new Label("Informations sur l'Atelier :"));

        // Panneau d'informations sur l'équipement sélectionné
        VBox infoDroite = new VBox();
        Separator separator = new Separator();

        // Ajout des éléments au panneau d'informations
        infoDroite.getChildren().addAll(
                new Label("Informations sur l'équipement :"),
                new HBox(new Label("Référence : "), refMach),
                new HBox(new Label("Désignation : "), dMach),
                new HBox(new Label("Coût horaire : "), coutHMach, new Label(" €")),
                new HBox(new Label("Durée d'utilisation : "), dureeMach, new Label(" h")),
                new HBox(new Label("Etat : "), etatMach),
                boutonsHb,
                separator,
                basInfo);

        // Variable temporaire pour stocker la référence initiale lors de la
        // modification
        AtomicReference<String> tempRef = new AtomicReference<>();

        // Gestion de l'événement du bouton "Modifier"/"Valider"
        modifierButton.setOnAction(e -> {
            controleur.modifierMachine(modifierButton, tempRef);
        });

        // Gestion de l'événement du bouton "Supprimer"
        supprimerButton.setOnAction(e -> {
            controleur.supprimerMachine();
        });

        // Stylisation du panneau d'informations
        // infoDroite.setStyle("-fx-background-color:#eef1f5;");
        infoDroite.setStyle("-fx-background-color: #e5e9f0;"); // Couleur de fond
        infoDroite.setPadding(new javafx.geometry.Insets(10));
        infoDroite.setSpacing(10);
        infoDroite.setBorder(new Border(new BorderStroke(
                javafx.scene.paint.Paint.valueOf("#c7ceda"), // couleur
                BorderStrokeStyle.SOLID, // style
                CornerRadii.EMPTY, // rayons des coins
                new BorderWidths(0, 0, 0, 1) // largeurs: haut, droite, bas, gauche
        )));
        VBox.setVgrow(infoDroite, Priority.SOMETIMES);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.8); // Position du séparateur
        splitPane.getItems().addAll(planAtelier, infoDroite); // Ajout du plan et du panneau d'infos

        // Assemblage final de l'interface
        // fenetre.setRight(infoDroite); // Panneau d'informations à droite
        fenetre.setTop(menuBar); // Barre de menu en haut
        // fenetre.setCenter(planAtelier); // Plan de l'atelier au centre
        fenetre.setCenter(splitPane); // Panneau d'informations et plan de l'atelier
        // Créons un StackPane qui contiendra toute l'interface et permettra de
        // superposer des notifications
        rootContainer.getChildren().add(fenetre); // Ajoutez votre BorderPane principal

        btn.setOnAction(e -> {
            afficherNotif("TEST", Material2AL.INFO, rootContainer);
        });

        // Puis utilisez rootContainer comme racine de votre scène
        Scene scene = new Scene(rootContainer, 1280, 720);

        // Configuration finale et affichage de la fenêtre principale
        Image icon = new Image("file:src\\main\\ressources\\images\\icon.png"); // Icône de l'application
        primaryStage.getIcons().add(icon);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true); // Démarre en plein écran
        primaryStage.show(); // Affiche la fenêtre

    }

    public void startWindow() {
        // Crée la fenêtre principale
        mainWindow();

        // Fenêtre bloquante de sélection : Nouvel atelier vierge / Ouvrir atelier
        Stage selectionStage = new Stage();
        selectionStage.setTitle("Sélection de l'atelier");
        selectionStage.getIcons().add(new Image("file:src\\main\\ressources\\images\\icon.png"));
        selectionStage.initModality(Modality.APPLICATION_MODAL); // Modale
        selectionStage.initOwner(primaryStage); // Propriétaire de la fenêtre
        selectionStage.setResizable(false); // Non redimensionnable
        selectionStage.setWidth(300); // Largeur de la fenêtre
        selectionStage.setHeight(200); // Hauteur de la fenêtre

        // Création des boutons
        Button nvAtelierButton = new Button("Nouvel Atelier");
        nvAtelierButton.setOnAction(e -> {
            selectionStage.close(); // Ferme la fenêtre de sélection
            controleur.creerNouveauAtelier();
        });
        Button ouvrirAtelierButton = new Button("Ouvrir Atelier");
        ouvrirAtelierButton.setOnAction(e -> {
            // controleur.ouvrirAtelier();
            selectionStage.close(); // Ferme la fenêtre de sélection
        });
        // Création de la disposition
        VBox layout = new VBox(20);
        layout.getChildren().addAll(nvAtelierButton, ouvrirAtelierButton);
        layout.setAlignment(Pos.CENTER); // Centre le contenu
        layout.setPadding(new javafx.geometry.Insets(10)); // Ajoute des marges
        // Création de la scène
        Scene scene = new Scene(layout);
        selectionStage.setScene(scene); // Définit la scène
        selectionStage.showAndWait(); // Affiche la fenêtre et attend sa fermeture
    }

    public void dessinerAtelier() {
        controleur.dessinerAtelier();
    }

    // Méthode appelée par le contrôleur pour dessiner les machines
    public void afficherMachinesSurPlan(ArrayList<Machine> machines) {
        // Effacement du contenu précédent pour éviter les superpositions
        planAtelier.getChildren().clear();
        // Parcours de toutes les machines à placer sur le plan
        for (Machine m : machines) {
            // Création d'un bouton représentant la machine, identifié par sa référence
            Button machineButton = new Button(m.getRefEquipement());
            machineButton.setMnemonicParsing(false); // Désactive l'interprétation des caractères spéciaux (ex: '_')

            // Configuration de l'action lors du clic: afficher les détails de la machine
            machineButton.setOnAction(e -> {
                afficherDetailsMachine(m);
            });

            // Positionnement du bouton sur le plan selon les coordonnées de la machine
            machineButton.setLayoutX(m.getPosX());
            machineButton.setLayoutY(m.getPosY());

            // Ajout du bouton au panneau d'affichage
            planAtelier.getChildren().add(machineButton);
        }
    }

    // Affiche les détails d'une machine dans les champs de l'interface
    public void afficherDetailsMachine(Machine m) {
        dMach.setText(m.getdEquipement()); // Désignation/nom
        coutHMach.setText(String.valueOf(m.getCoutHoraire())); // Coût horaire
        dureeMach.setText(String.valueOf(m.getDureeUtil())); // Durée d'utilisation
        etatMach.setValue(m.getEtat()); // État actuel (EN_MARCHE, EN_PANNE, etc.)
        refMach.setText(m.getRefEquipement()); // Référence unique
    }

    // Méthodes pour accéder aux composants d'interface
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Pane getPlanAtelier() {
        return planAtelier;
    }

    public TextField getRefMach() {
        return refMach;
    }

    public TextField getDMach() {
        return dMach;
    }

    public TextField getCoutHMach() {
        return coutHMach;
    }

    public TextField getDureeMach() {
        return dureeMach;
    }

    public ComboBox<EtatMachine> getEtatMach() {
        return etatMach;
    }

    public StackPane getRootContainer() {
        return rootContainer;
    }

    // Définir l'accessibilité des champs de texte et combobox
    public void setFieldsEditable(boolean editable) {
        dMach.setEditable(editable);
        coutHMach.setEditable(editable);
        dureeMach.setEditable(editable);
        etatMach.setDisable(!editable);
        refMach.setEditable(editable);
    }

    // Changer le texte du bouton modifier
    public void setModifierButtonText(Button button, String text) {
        button.setText(text);
    }

    // Méthode pour vider les champs d'information
    public void viderChamps() {
        dMach.clear();
        coutHMach.clear();
        dureeMach.clear();
        refMach.clear();
        etatMach.setValue(null);
    }

    // Affiche une alerte
    public void afficherAlerte(Alert.AlertType type, String titre, String entete, String contenu) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(entete);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    public void afficherNotif(String message, Ikon icon, StackPane rootContainer) {
        final Notification msg = new Notification(
                message,
                icon != null ? new FontIcon(icon) : null);
        msg.getStyleClass().addAll(Styles.ACCENT, Styles.ELEVATED_1);
        msg.setMaxWidth(300);

        msg.setPrefHeight(Region.USE_COMPUTED_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
        StackPane.setMargin(msg, new javafx.geometry.Insets(10, 10, 0, 0));
        msg.setOnClose(ev -> {
            Timeline out = Animations.slideOutUp(msg, Duration.millis(250));
            out.setOnFinished(f -> rootContainer.getChildren().remove(msg));
            out.playFromStart();
        });

        Timeline in = Animations.slideInDown(msg, Duration.millis(250));
        if (!rootContainer.getChildren().contains(msg)) {
            rootContainer.getChildren().add(msg);
        }
        in.playFromStart();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(2000));
        delay.setOnFinished(event -> {
            Timeline out = Animations.slideOutUp(msg, Duration.millis(250));
            out.setOnFinished(f -> rootContainer.getChildren().remove(msg));
            out.playFromStart();
        });
        delay.play();

    }
}