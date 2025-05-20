package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import fr.insa.eymin.gestion_atelier.modeles.Atelier;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.modeles.Produit;
import fr.insa.eymin.gestion_atelier.vues.AtelierVue;
import fr.insa.eymin.gestion_atelier.vues.MachineVue;
import fr.insa.eymin.gestion_atelier.vues.PosteVue;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
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

    // =============================================================================
    // Constructeur
    public PrincipalControleur(PrincipalVue principalVue, ArrayList<Machine> machines,
            ArrayList<Produit> produits, ArrayList<Poste> postes) {
        this.principalVue = principalVue;
        this.machines = machines;
        this.produits = produits;
        this.postes = postes;
    }

    public static void selectChamp(TextField champ) {
        champ.selectAll();
    }

    // =================================================================================
    // Méthodes de création

    public void creerProduit() {
        ProduitControleur controleur = new ProduitControleur(produits);
        controleur.afficherFenetreCreation();
    }

    public void creerMachine() {
        MachineControleur controleur = new MachineControleur(machines, principalVue);
        MachineVue vue = new MachineVue(controleur);
        vue.afficherFenetreCreation();
    }

    public void creerPoste() {
        PosteVue.fenetreCreationPoste(machines, postes);
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

    // =================================================================================
    // Méthodes de gestion de l'atelier

    public void calculerFiabilite() {
        Atelier modele = new Atelier();
        AtelierVue vue = new AtelierVue();
        AtelierControleur controleur = new AtelierControleur(modele, vue);
        controleur.calculerEtAfficherFiabilite();
    }

    public void dessinerAtelier() {
        principalVue.afficherMachinesSurPlan(machines);
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
                // Passe en mode lecture: désactive les champs de saisie et enregistre les
                // modifications
                principalVue.setFieldsEditable(false);
                principalVue.setModifierButtonText(modifierButton, "Modifier");

                // Récupération des valeurs des champs
                TextField coutHMach = principalVue.getCoutHMach();
                TextField dureeMach = principalVue.getDureeMach();
                ComboBox<EtatMachine> etatMach = principalVue.getEtatMach();

                updateMachine(tempRef.get(), refMach.getText(), dMach.getText(),
                        coutHMach.getText(), dureeMach.getText(), etatMach.getValue());
                dessinerAtelier();
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
            // Supprime l'équipement sélectionné

            // Demande de confirmation avant la suppression
            principalVue.afficherAlerte(Alert.AlertType.CONFIRMATION,
                    "Confirmation",
                    "Êtes-vous sûr de vouloir supprimer cet équipement ?",
                    "Cette action est irréversible.");

            deleteMachine(refMach.getText());
            principalVue.viderChamps();

            // Redessine l'atelier après la suppression
            dessinerAtelier();
            // Affiche une alerte de confirmation
            principalVue.afficherAlerte(Alert.AlertType.INFORMATION,
                    "Confirmation",
                    "Équipement supprimé",
                    "L'équipement a été supprimé avec succès.");
        }
    }

    public void updateMachine(String ancienneRef, String nouvelleRef, String designation,
            String coutHoraireStr, String dureeStr, EtatMachine etat) {
        // Met à jour les informations de la machine correspondante
        for (Machine m : machines) {
            if (m.getRefEquipement().equals(ancienneRef)) {
                m.setRefEquipement(nouvelleRef);
                m.setdEquipement(designation);
                m.setCoutHoraire(Float.parseFloat(coutHoraireStr));
                m.setDureeUtil(Float.parseFloat(dureeStr));
                m.setEtat(etat);
            }
        }
    }

    public void deleteMachine(String refMach) {
        machines.removeIf(m -> m.getRefEquipement().equals(refMach));
    }
}
