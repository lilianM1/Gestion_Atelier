package fr.insa.eymin.gestion_atelier.controleurs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import org.kordamp.ikonli.feather.Feather;

import fr.insa.eymin.gestion_atelier.modeles.Atelier;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Operation;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.modeles.Produit;
import fr.insa.eymin.gestion_atelier.vues.AtelierVue;
import fr.insa.eymin.gestion_atelier.vues.MachineVue;
import fr.insa.eymin.gestion_atelier.vues.PosteVue;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
import fr.insa.eymin.gestion_atelier.vues.ProduitVue;
import fr.insa.eymin.gestion_atelier.vues.OperationVue;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PrincipalControleur {
    private PrincipalVue principalVue;
    private ArrayList<Machine> machines;
    private ArrayList<Produit> produits = new ArrayList<>();
    private ArrayList<Poste> postes = new ArrayList<>();
    private ArrayList<Operation> operations = new ArrayList<>();

    // =============================================================================
    // Constructeur
    public PrincipalControleur(PrincipalVue principalVue, ArrayList<Machine> machines,
            ArrayList<Produit> produits, ArrayList<Poste> postes, ArrayList<Operation> operations) {
        this.principalVue = principalVue;
        this.machines = machines;
        this.produits = produits;
        this.postes = postes;
        this.operations = operations;
    }

    public static void selectChamp(TextField champ) {
        champ.selectAll();
    }

    // =================================================================================
    // Méthodes de création

    public void creerProduit() {
        ProduitControleur controleur = new ProduitControleur(produits, principalVue);
        ProduitVue vue = new ProduitVue(controleur);
        vue.afficherFenetreCreation();
    }

    public void creerMachine() {
        MachineControleur controleur = new MachineControleur(machines, principalVue);
        MachineVue vue = new MachineVue(controleur);
        vue.afficherFenetreCreation();
    }

    public void creerPoste() {
        PosteControleur controleur = new PosteControleur(postes, machines, principalVue);
        PosteVue vue = new PosteVue(controleur);
        vue.afficherFenetreCreation();
    }

    public void creerOperation() {
        // Récupérez les machines et postes depuis votre modèle principal
        ArrayList<Machine> machines = this.machines;
        ArrayList<Poste> postes = this.postes;

        // Créez le contrôleur avec toutes les données nécessaires
        OperationControleur controleur = new OperationControleur(operations);

        // Ajoutez une méthode pour définir les machines et postes si vous ne modifiez
        // pas le constructeur
        controleur.setMachines(machines);
        controleur.setPostes(postes);

        OperationVue vue = new OperationVue(controleur);
        vue.afficherFenetreCreation();
    }

    // =================================================================================
    // Méthodes d'affichage

    public void afficherMachines() {
        for (Machine m : machines) {
            m.afficherMachine();
        }
        System.out.println();
    }

    public void afficherProduits() {
        for (Produit p : produits) {
            p.afficherProduit();
        }
        System.out.println();
    }

    public void afficherPostes() {
        for (Poste p : postes) {
            p.afficherPoste();
        }
        System.out.println();
    }

    public void afficherOperations() {
        for (Operation o : operations) {
            o.afficherOperation();
        }
        System.out.println();
    }

    // =================================================================================
    // Méthodes de gestion de l'atelier

    public void calculerFiabilite() {
        Atelier modele = new Atelier();
        AtelierVue vue = new AtelierVue();
        AtelierControleur controleur = new AtelierControleur(modele, vue, principalVue);
        controleur.calculerEtAfficherFiabilite();
    }

    public void dessinerAtelier(float longX, float longY) {
        principalVue.afficherMachinesSurPlan(machines, longX, longY);
    }

    public void saveAtelier() {
        // Atelier atelier = new Atelier(machines, postes, produits, operations);
        // atelier.sauvegarderAtelier();
    }

    public void creerNouveauAtelier() {
        Atelier modele = new Atelier();
        AtelierVue vue = new AtelierVue();
        AtelierControleur controleur = new AtelierControleur(modele, vue, principalVue);
        controleur.creerNouveauAtelier();
    }

    // =================================================================================
    // Méthodes de gestion de l'interface

    public void basculerPleinEcran() {
        Stage primaryStage = principalVue.getPrimaryStage();
        if (primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
        } else {
            primaryStage.setFullScreen(true);
        }
    }

    // =================================================================================
    // Méthodes de gestion des machines

    public void modifierMachine(Button modifierButton, AtomicReference<String> tempRef) {
        TextField dMach = principalVue.getDMach();
        TextField refMach = principalVue.getRefMach();
        TextField coutHMach = principalVue.getCoutHMach();
        TextField dureeMach = principalVue.getDureeMach();
        TextField posXField = principalVue.getPosXField();
        TextField posYField = principalVue.getPosYField();

        if (dMach.getText().isEmpty()) {
            // Affiche une alerte si aucun équipement n'est sélectionné
            principalVue.afficherAlerte(Alert.AlertType.WARNING,
                    "Avertissement",
                    "Aucun équipement sélectionné",
                    "Veuillez sélectionner un équipement à modifier.");
        } else {
            if (modifierButton.getText().equals("Modifier")) {
                // Passe en mode édition: active les champs de saisie
                principalVue.setFieldsEditable(true);
                principalVue.setModifierButtonText(modifierButton, "Valider");
                tempRef.set(refMach.getText()); // Mémorise la référence originale
            } else if (modifierButton.getText().equals("Valider")) {
                // Vérification que la référence n'existe pas déjà
                if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMach.getText())
                        && !m.getRefEquipement().equals(tempRef.get()))) {
                    principalVue.afficherAlerte(Alert.AlertType.ERROR,
                            "Erreur",
                            "Référence déjà utilisée",
                            "La référence de la machine existe déjà. Veuillez en choisir une autre.");
                    return;
                }
                // Vérification que les champs ne sont pas vides
                if (refMach.getText().isEmpty() || dMach.getText().isEmpty() ||
                        coutHMach.getText().isEmpty() || dureeMach.getText().isEmpty() ||
                        posXField.getText().isEmpty() || posYField.getText().isEmpty()) {
                    principalVue.afficherAlerte(Alert.AlertType.ERROR,
                            "Erreur",
                            "Champs vides",
                            "Veuillez remplir tous les champs obligatoires.");
                    return;
                }
                // Vérification que la durée et le coût horaire et la position sont des nombres
                // valides
                try {
                    Float.parseFloat(coutHMach.getText());
                    Float.parseFloat(dureeMach.getText());
                    Float.parseFloat(posXField.getText());
                    Float.parseFloat(posYField.getText());
                } catch (NumberFormatException e) {
                    principalVue.afficherAlerte(Alert.AlertType.ERROR,
                            "Erreur",
                            "Format incorrect",
                            "Veuillez entrer des valeurs numériques valides pour le coût horaire, la durée et la position.");
                    return;
                }

                // Passe en mode lecture: désactive les champs de saisie et enregistre les
                // modifications
                principalVue.setFieldsEditable(false);
                principalVue.setModifierButtonText(modifierButton, "Modifier");

                // Récupération des valeurs des champs
                ComboBox<EtatMachine> etatMach = principalVue.getEtatMach();

                updateMachine(tempRef.get(), refMach.getText(), dMach.getText(),
                        coutHMach.getText(), dureeMach.getText(), etatMach.getValue(), posXField.getText(),
                        posYField.getText());
                principalVue.dessinerAtelier();
            }
        }
    }

    public void supprimerMachine() {
        TextField dMach = principalVue.getDMach();
        TextField refMach = principalVue.getRefMach();

        if (dMach.getText().isEmpty()) {
            // Affiche une alerte si aucun équipement n'est sélectionné
            principalVue.afficherAlerte(Alert.AlertType.WARNING,
                    "Avertissement",
                    "Aucun équipement sélectionné",
                    "Veuillez sélectionner un équipement à supprimer.");
        } else {
            // Demande de confirmation avant la suppression
            boolean confirme = principalVue.afficherAlerteConfirmation(
                    "Confirmation",
                    "Êtes-vous sûr de vouloir supprimer cet équipement ?",
                    "Cette action est irréversible.");

            // Si l'utilisateur confirme, on procède à la suppression
            if (confirme) {
                // Suppression de la machine
                deleteMachine(refMach.getText());
                principalVue.viderChamps();

                // Redessine l'atelier après la suppression
                principalVue.dessinerAtelier();

                // Affiche une notification de confirmation
                principalVue.afficherNotif("Machine supprimée avec succès", Feather.TRASH_2,
                        principalVue.getRootContainer(), "info");
            }
        }
    }

    public void updateMachine(String ancienneRef, String nouvelleRef, String designation,
            String coutHoraireStr, String dureeStr, EtatMachine etat, String posX, String posY) {
        // Met à jour les informations de la machine correspondante
        for (Machine m : machines) {
            if (m.getRefEquipement().equals(ancienneRef)) {
                m.setRefEquipement(nouvelleRef);
                m.setdEquipement(designation);
                m.setCoutHoraire(Float.parseFloat(coutHoraireStr));
                m.setDureeUtil(Float.parseFloat(dureeStr));
                m.setPosX(Float.parseFloat(posX));
                m.setPosY(Float.parseFloat(posY));
                m.setEtat(etat);
            }
        }
    }

    public void deleteMachine(String refMach) {
        machines.removeIf(m -> m.getRefEquipement().equals(refMach));
    }

    public void sauvegarderAtelier() {
        File fichier = new File(
                "C:\\Users\\lilou\\Documents\\INSA\\PIF\\Informatique\\S2\\PROJET\\Gestion_Atelier\\src\\main\\ressources\\data\\atelier_saves\\"
                        + principalVue.getAtelier().getNomAtelier() + ".txt");
        File sauvegarde = new File(
                "C:\\Users\\lilou\\Documents\\INSA\\PIF\\Informatique\\S2\\PROJET\\Gestion_Atelier\\src\\main\\ressources\\data\\atelier_saves\\"
                        + principalVue.getAtelier().getNomAtelier() + ".bak");
        try {
            // Créer une sauvegarde si le fichier existe
            if (fichier.exists()) {
                Files.copy(fichier.toPath(), sauvegarde.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            // Écrire le nouveau fichier
            try (FileWriter writer = new FileWriter(fichier)) {
                // Écrire l'en-tête
                writer.write("A;" + principalVue.getAtelier().getNomAtelier() + ";" +
                        principalVue.getAtelier().getLongX() + ";" +
                        principalVue.getAtelier().getLongY() + "\n");
                // Écrire les machines
                for (Machine machine : machines) {
                    writer.write("M;" + machine.getRefEquipement() + ";" +
                            machine.getdEquipement() + ";" + machine.getPosX() + ";" +
                            machine.getPosY() + ";" + machine.getCoutHoraire() + ";" +
                            machine.getDureeUtil() + ";" + machine.getEtat() + "\n");
                }
                // TODO : ecrire la suite

            }
            principalVue.afficherNotif("Atelier sauvegardé avec succès", Feather.CHECK_SQUARE,
                    principalVue.getRootContainer(), "info");

        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            principalVue.afficherNotif("Erreur lors de la sauvegarde de l'atelier",
                    Feather.ALERT_OCTAGON, principalVue.getRootContainer(), "error");
            // Restaurer la sauvegarde si possible
            if (sauvegarde.exists()) {
                try {
                    Files.copy(sauvegarde.toPath(), fichier.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Fichier restauré depuis la sauvegarde");
                } catch (IOException ex) {
                    System.err.println("Impossible de restaurer : " + ex.getMessage());
                }
            }
        }
    }
}
