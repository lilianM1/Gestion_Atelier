package fr.insa.eymin.gestion_atelier.classes;

import java.util.ArrayList;

import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;

public class Poste extends Equipement {
    // ========================== Attributs ================================

    private ArrayList<Machine> machines; // Liste des machines du poste

    // ========================== Constructeurs ============================

    public Poste() {
        super();
        this.machines = new ArrayList<Machine>();
    }

    public Poste(String refPoste, String dPoste, ArrayList<Machine> machines) {
        super(refPoste, dPoste);
        this.machines = machines;
    }

    // ========================== Méthodes =================================

    // ---------------------------------------------------------------------
    // Affiche les informations du poste
    public void afficherPoste() {
        ArrayList<String> listeM = new ArrayList<String>();
        for (Machine m : this.machines) {
            listeM.add(m.getRefEquipement());
        }
        System.out.println("refPoste = " + super.refEquipement + ", dPoste = " + super.dEquipement + ", machines = "
                + listeM + "\n");
    }

    // ---------------------------------------------------------------------
    // Crée un poste
    public static ArrayList<Poste> creerPoste(ArrayList<Poste> postes, ArrayList<Machine> machines) {
        Stage creerPosteStage = new Stage();
        creerPosteStage.setTitle("Nouveau Poste");

        // Création des champs de texte
        TextField refPosteField = new TextField();
        refPosteField.setPromptText("refPoste");
        TextField dPosteField = new TextField();
        dPosteField.setPromptText("dPoste");

        // Création des labels
        Label refPosteLabel = new Label("Référence du poste :");
        Label dPosteLabel = new Label("Désignation du poste :");
        Label listeMachines = new Label("Liste des machines :");

        // Création des boutons
        Button creerButton = new Button("Créer");
        Button terminerButton = new Button("Terminer");
        MenuButton machinesButton = new MenuButton("Machines");

        // Création de la liste des machines
        for (Machine m : machines) {
            CheckMenuItem machineItem = new CheckMenuItem(m.getRefEquipement());
            // Empêcher la fermeture du menu lors de la sélection d'un CheckMenuItem
            machineItem.setOnAction(e -> e.consume());
            machinesButton.getItems().add(machineItem);
        }

        // Action du bouton Valider
        creerButton.setOnAction(e -> {
            try {
                if (refPosteField.getText().isEmpty() || dPosteField.getText().isEmpty()
                        || machinesButton.getItems().stream().noneMatch(item -> ((CheckMenuItem) item).isSelected())) {
                    throw new Exception("Veuillez remplir tous les champs");
                }
                if (postes.stream().anyMatch(p -> p.getRefEquipement().equals(refPosteField.getText()))) {
                    throw new Exception("Le poste existe déjà");
                }
                String refPoste = refPosteField.getText();
                String dPoste = dPosteField.getText();
                ArrayList<Machine> machinesPoste = new ArrayList<Machine>();
                for (MenuItem item : machinesButton.getItems()) {
                    if (item instanceof CheckMenuItem && ((CheckMenuItem) item).isSelected()) {
                        String refMachine = item.getText();
                        Machine machine = machines.stream()
                                .filter(m -> m.getRefEquipement().equals(refMachine))
                                .findFirst()
                                .orElse(null);
                        if (machine != null) {
                            machinesPoste.add(machine);
                        }
                    }
                }
                Poste poste = new Poste(refPoste, dPoste, machinesPoste);
                postes.add(poste);
                refPosteField.clear();
                dPosteField.clear();
                for (MenuItem item : machinesButton.getItems()) {
                    if (item instanceof CheckMenuItem) {
                        ((CheckMenuItem) item).setSelected(false);
                    }
                }
            } catch (Exception ex) {
                if (ex.getMessage().equals("Veuillez remplir tous les champs")) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Champs vides");
                    alert.setContentText("Veuillez remplir tous les champs.");
                    alert.showAndWait();
                    return;
                } else if (ex.getMessage().equals("Le poste existe déjà")) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Poste déjà existant");
                    alert.setContentText("Le poste existe déjà.");
                    alert.showAndWait();
                    return;
                }
            }
        });

        // Action du bouton Terminer
        terminerButton.setOnAction(e -> {
            creerPosteStage.close();
        });

        // Création de la disposition
        VBox layout = new VBox(20);
        HBox layoutBoutons = new HBox(20);
        GridPane layoutChamps = new GridPane();

        layoutChamps.setHgap(10);
        layoutChamps.setVgap(10);
        layoutChamps.add(refPosteLabel, 0, 0);
        layoutChamps.add(refPosteField, 1, 0);
        layoutChamps.add(dPosteLabel, 0, 1);
        layoutChamps.add(dPosteField, 1, 1);
        layoutChamps.add(listeMachines, 0, 2);
        layoutChamps.add(machinesButton, 1, 2);

        layoutBoutons.getChildren().addAll(terminerButton, creerButton);
        layout.getChildren().addAll(layoutChamps, layoutBoutons);
        layout.setAlignment(Pos.CENTER);
        layoutBoutons.setAlignment(Pos.CENTER);
        layout.setPadding(new javafx.geometry.Insets(10));

        // Création de la scène
        Scene scene = new Scene(layout);

        // Affichage de la fenêtre
        creerPosteStage.getIcons().add(new Image("file:src\\main\\ressources\\icon.png"));
        creerPosteStage.setScene(scene);
        creerPosteStage.show();
        return postes;
    }

    // ---------------------------------------------------------------------
    // Supprime le poste
    public void supprimerPoste() {
        super.refEquipement = null;
        super.dEquipement = null;
        this.machines.clear();
    }

    // ========================== Getters/Setters ==========================

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<Machine> machines) {
        this.machines = machines;
    }

}
