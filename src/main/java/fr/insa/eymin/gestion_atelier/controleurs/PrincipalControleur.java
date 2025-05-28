package fr.insa.eymin.gestion_atelier.controleurs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.kordamp.ikonli.feather.Feather;

import fr.insa.eymin.gestion_atelier.modeles.Atelier;
import fr.insa.eymin.gestion_atelier.modeles.Equipement;
import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Gamme;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Operation;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.modeles.Produit;
import fr.insa.eymin.gestion_atelier.vues.AtelierVue;
import fr.insa.eymin.gestion_atelier.vues.GammeVue;
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
    private ArrayList<Gamme> gammes = new ArrayList<>();

    // =============================================================================
    // Constructeur
    public PrincipalControleur(PrincipalVue principalVue, ArrayList<Machine> machines,
            ArrayList<Produit> produits, ArrayList<Poste> postes, ArrayList<Operation> operations,
            ArrayList<Gamme> gammes) {
        this.gammes = gammes;
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
        OperationControleur controleur = new OperationControleur(operations, principalVue);

        // Ajoutez une méthode pour définir les machines et postes si vous ne modifiez
        // pas le constructeur
        controleur.setMachines(machines);
        controleur.setPostes(postes);

        OperationVue vue = new OperationVue(controleur);
        vue.afficherFenetreCreation();
    }

    public void creerGamme() {
        ArrayList<Operation> operations = this.operations;
        ArrayList<Produit> produits = this.produits;

        GammeControleur controleur = new GammeControleur(gammes, principalVue, operations, produits);

        controleur.setOperations(operations);
        controleur.setProduits(produits);

        GammeVue vue = new GammeVue(controleur);
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

    public void afficherGammes() {
        for (Gamme g : gammes) {
            g.afficherGamme();
        }
        System.out.println();
    }

    // =================================================================================
    // Méthodes de gestion de l'atelier

    public void calculerFiabilite() {
        Atelier modele = new Atelier(principalVue);
        AtelierVue vue = new AtelierVue();
        AtelierControleur controleur = new AtelierControleur(modele, vue, principalVue);
        controleur.calculerEtAfficherFiabilite();
    }

    public void dessinerAtelier(float longX, float longY) {
        principalVue.afficherMachinesSurPlan(machines, longX, longY);
    }

    public void creerNouveauAtelier() {
        // Réinitialiser les listes de machines, produits, postes et opérations
        machines.clear();
        produits.clear();
        postes.clear();
        operations.clear();
        gammes.clear();
        // Réinitialiser l'atelier dans la vue principale
        principalVue.setAtelier(new Atelier(principalVue));

        Atelier modele = new Atelier(principalVue);
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
                updateOp();
                principalVue.updateTableView();

            }
            principalVue.dessinerAtelier();

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

                // Met à jour la table
                principalVue.updateTableView();
                principalVue.getTreeTableView().refresh();
                principalVue.ecrireTreeTableView();

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

    public void updateOp() {
        // Met à jour les opérations associées à la machine
        for (Operation op : operations) {
            op.calculerDureeOperation();
        }
    }

    public void deleteMachine(String refMach) {
        machines.removeIf(m -> m.getRefEquipement().equals(refMach));
        // Supprimer la machine de tous les postes
        postes.forEach(poste -> poste.getMachines().removeIf(m -> m.getRefEquipement().equals(refMach)));
        // Supprimer la machine de toutes les opérations
        operations.forEach(op -> {
            if (op.getRefEquipement() != null && op.getRefEquipement().getRefEquipement().equals(refMach)) {
                op.setrefEquipement(null); // Ou gérer autrement si nécessaire
            }
        });
    }

    public void modifierPoste(Poste poste) {
        PosteControleur controleur = new PosteControleur(postes, machines, principalVue);
        PosteVue vue = new PosteVue(controleur);
        vue.afficherFenetreModification(poste);
    }

    public void modifierOperation(Operation operation) {
        // Passer toutes les listes nécessaires
        OperationControleur controleur = new OperationControleur(operations, machines, postes, principalVue);
        OperationVue vue = new OperationVue(controleur);
        vue.afficherFenetreModification(operation);
    }

    public void modifierGamme(Gamme gamme) {
        GammeControleur controleur = new GammeControleur(gammes, principalVue, operations, produits);
        GammeVue vue = new GammeVue(controleur);
        vue.afficherFenetreModification(gamme);
    }

    public void sauvegarderAtelier(File fichier) {
        File sauvegarde = new File(
                "C:\\Users\\lilou\\Documents\\INSA\\PIF\\Informatique\\S2\\PROJET\\Gestion_Atelier\\src\\main\\ressources\\data\\atelier_saves\\backups\\"
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
                    writer.write(machine.toStringForSave() + "\n");
                }
                for (Poste poste : postes) {
                    writer.write(poste.toStringForSave() + "\n");
                }
                for (Operation operation : operations) {
                    writer.write(operation.toStringForSave() + "\n");
                }
                for (Produit produit : produits) {
                    writer.write(produit.toStringForSave() + "\n");
                }
                for (Gamme gamme : gammes) {
                    writer.write(gamme.toStringForSave() + "\n");
                }

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

    public void chargerAtelier(File fichier) {
        try {
            // Vérifier si le fichier existe
            if (!fichier.exists()) {
                principalVue.afficherAlerte(Alert.AlertType.ERROR,
                        "Erreur",
                        "Fichier non trouvé",
                        "Le fichier spécifié n'existe pas.");
                return;
            }

            // Charger l'atelier depuis le fichier
            machines.clear();
            postes.clear();
            operations.clear();
            produits.clear();
            gammes.clear();
            if (principalVue.getTreeTableView() != null) {
                principalVue.updateTableView();
            }

            Map<String, List<String>> lignesTriees = Files.lines(fichier.toPath())
                    .filter(ligne -> !ligne.trim().isEmpty()) // Filtrer les lignes vides
                    .collect(Collectors.groupingBy(ligne -> {
                        // Trier par type de ligne (premier caractère avant le ;)
                        String[] parts = ligne.split(";");
                        return parts.length > 0 ? parts[0] : "UNKNOWN";
                    }));

            // Traiter dans l'ordre : Atelier → Machines → Postes → Produits → Opérations
            List<String> ordreTraitement = Arrays.asList("A", "M", "P", "OP", "PR", "G");

            ordreTraitement.forEach(type -> {
                List<String> lignesType = lignesTriees.get(type);
                switch (type) {
                    case "A" -> traiterLignesAtelier(lignesType);
                    case "M" -> traiterLignesMachines(lignesType);
                    case "P" -> traiterLignesPostes(lignesType);
                    case "PR" -> traiterLignesProduits(lignesType);
                    case "OP" -> traiterLignesOperations(lignesType);
                    case "G" -> traiterLignesGammes(lignesType);
                }
            });

            // Mettre à jour l'atelier
            principalVue.mainWindow();
            principalVue.dessinerAtelier();
            principalVue.afficherNotif("Atelier chargé avec succès", Feather.CHECK_SQUARE,
                    principalVue.getRootContainer(), "info");

        } catch (IOException e) {
            principalVue.afficherAlerte(Alert.AlertType.ERROR,
                    "Erreur",
                    "Erreur de lecture",
                    "Impossible de lire le fichier : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            principalVue.afficherAlerte(Alert.AlertType.ERROR,
                    "Erreur",
                    "Erreur de chargement",
                    "Une erreur est survenue lors du chargement de l'atelier : " + e.getMessage());
        }
    }

    private void traiterLignesAtelier(List<String> lignes) {
        if (lignes != null && !lignes.isEmpty()) {
            String ligne = lignes.get(0); // Une seule ligne d'atelier
            String[] parts = ligne.split(";");
            if (parts.length >= 4) {
                String nom = parts[1];
                float longX = Float.parseFloat(parts[2]);
                float longY = Float.parseFloat(parts[3]);

                // Mettre à jour l'atelier actuel
                principalVue.setAtelier(new Atelier(principalVue));
                principalVue.getAtelier().setNomAtelier(nom);
                principalVue.getAtelier().setLongX(longX);
                principalVue.getAtelier().setLongY(longY);
                principalVue.getAtelier().setEquipements(new ArrayList<>());
                System.out.println("Atelier chargé : " + nom + " (" + longX + "x" + longY + ")");
            }
        }
    }

    private void traiterLignesMachines(List<String> lignes) {
        if (lignes != null) {
            lignes.stream()
                    .map(ligne -> ligne.split(";"))
                    .filter(parts -> parts.length >= 8)
                    .forEach(parts -> {
                        try {
                            String ref = parts[1];
                            String description = parts[2];
                            float posX = Float.parseFloat(parts[3]);
                            float posY = Float.parseFloat(parts[4]);
                            float coutHoraire = Float.parseFloat(parts[5]);
                            float dureeUtil = Float.parseFloat(parts[6]);
                            EtatMachine etat = EtatMachine.valueOf(parts[7]);

                            Machine machine = new Machine(ref, description, posX,
                                    posY, coutHoraire, dureeUtil, etat);
                            machines.add(machine);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement d'une machine : " + e.getMessage());
                        }
                    });
        }
    }

    private void traiterLignesPostes(List<String> lignes) {
        if (lignes != null) {
            lignes.stream()
                    .map(ligne -> ligne.split(";"))
                    .filter(parts -> parts.length >= 4)
                    .forEach(parts -> {
                        try {
                            String refPoste = parts[1];
                            String dPoste = parts[2];
                            String machinesRefs = parts[3];

                            // Créer le poste d'abord
                            Poste poste = new Poste(refPoste, dPoste);

                            // Puis associer les machines
                            List<Machine> machinesAssociees = parseMachinesRefs(machinesRefs);
                            for (Machine machine : machinesAssociees) {
                                poste.ajouterMachine(machine); // Si cette méthode existe
                            }

                            postes.add(poste);

                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement d'un poste : " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        }
    }

    private void traiterLignesOperations(List<String> lignes) {
        if (lignes != null) {
            lignes.stream()
                    .map(ligne -> ligne.split(";"))
                    .filter(parts -> parts.length >= 4)
                    .forEach(parts -> {
                        try {
                            String refOp = parts[1];
                            String nomOp = parts[2];
                            float duree = Float.parseFloat(parts[3]);
                            String refEquipement = parts[4];

                            Operation operation = new Operation(refOp, nomOp, duree);

                            // Associer l'équipement si spécifié
                            if (!refEquipement.isEmpty()) {
                                Equipement equipement = machines.stream()
                                        .filter(m -> m.getRefEquipement().equals(refEquipement))
                                        .findFirst()
                                        .orElse(null);
                                if (equipement == null) {
                                    equipement = postes.stream()
                                            .filter(p -> p.getRefEquipement().equals(refEquipement))
                                            .findFirst()
                                            .orElse(null);
                                }
                                if (equipement != null) {
                                    operation.setrefEquipement(equipement);
                                }
                            }

                            operations.add(operation);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement d'une opération : " + e.getMessage());
                        }
                    });
        }
    }

    private void traiterLignesProduits(List<String> lignes) {
        if (lignes != null) {
            lignes.stream()
                    .map(ligne -> ligne.split(";"))
                    .filter(parts -> parts.length >= 3)
                    .forEach(parts -> {
                        try {
                            String ref = parts[1];
                            String nom = parts[2];

                            Produit produit = new Produit(ref, nom);
                            produits.add(produit);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement d'un produit : " + e.getMessage());
                        }
                    });
        }
    }

    private void traiterLignesGammes(List<String> lignes) {
        if (lignes != null) {
            lignes.stream()
                    .map(ligne -> ligne.split(";"))
                    .filter(parts -> parts.length >= 3)
                    .forEach(parts -> {
                        try {
                            String refGamme = parts[1];
                            String dGamme = parts[2];
                            String operationsRefs = parts[3];
                            String refProd = parts[4];

                            Gamme gamme = new Gamme(refGamme, dGamme);

                            // Associer le produit si spécifié
                            if (!refProd.isEmpty()) {
                                Produit produit = produits.stream()
                                        .filter(p -> p.getCodeProduit().equals(refProd))
                                        .findFirst()
                                        .orElse(null);
                                if (produit != null) {
                                    gamme.setProduit(produit);
                                }
                            }

                            // Puis associer les opérations
                            List<Operation> operationsAssociees = parseOperationsRefs(operationsRefs);
                            for (Operation op : operationsAssociees) {
                                gamme.ajouterOperation(op);
                            }

                            gammes.add(gamme);
                        } catch (Exception e) {
                            System.err.println("Erreur lors du chargement d'une gamme : " + e.getMessage());
                        }
                    });
        }
    }

    private List<Machine> parseMachinesRefs(String machinesRefsStr) {
        List<Machine> machinesAssociees = new ArrayList<>();

        // Enlever les crochets et espaces
        String cleaned = machinesRefsStr.replace("[", "").replace("]", "").trim();

        if (!cleaned.isEmpty()) {
            // Séparer par virgule
            String[] refs = cleaned.split(",");

            for (String ref : refs) {
                String refTrimmed = ref.trim();

                // Chercher la machine correspondante dans la liste des machines chargées
                Machine machine = machines.stream()
                        .filter(m -> m.getRefEquipement().equals(refTrimmed))
                        .findFirst()
                        .orElse(null);

                if (machine != null) {
                    machinesAssociees.add(machine);
                    System.out.println("Machine associée trouvée: " + refTrimmed);
                } else {
                    System.out.println("Machine non trouvée: " + refTrimmed);
                }
            }
        }

        return machinesAssociees;
    }

    private List<Operation> parseOperationsRefs(String operationsRefsStr) {
        List<Operation> operationsAssociees = new ArrayList<>();

        // Enlever les crochets et espaces
        String cleaned = operationsRefsStr.replace("[", "").replace("]", "").trim();

        if (!cleaned.isEmpty()) {
            // Séparer par virgule
            String[] refs = cleaned.split(",");

            for (String ref : refs) {
                String refTrimmed = ref.trim();

                // Chercher l'opération correspondante dans la liste des opérations chargées
                Operation operation = operations.stream()
                        .filter(o -> o.getRefOperation().equals(refTrimmed))
                        .findFirst()
                        .orElse(null);

                if (operation != null) {
                    operationsAssociees.add(operation);
                    System.out.println("Opération associée trouvée: " + refTrimmed);
                } else {
                    System.out.println("Opération non trouvée: " + refTrimmed);
                }
            }
        }

        return operationsAssociees;
    }

    public void supprimerPoste(Poste poste) {
        // Demande de confirmation avant la suppression
        boolean confirme = principalVue.afficherAlerteConfirmation(
                "Confirmation",
                "Êtes-vous sûr de vouloir supprimer cet équipement ?",
                "Cette action est irréversible.");

        // Si l'utilisateur confirme, on procède à la suppression
        if (confirme) {

            postes.remove(poste);
            // Supprimer le poste de toutes les opérations
            operations.forEach(op -> {
                if (op.getRefEquipement() != null
                        && op.getRefEquipement().getRefEquipement().equals(poste.getRefEquipement())) {
                    op.setrefEquipement(null);
                }
            });

            // Mettre à jour l'interface
            principalVue.ecrireTreeTableView();
            principalVue.updateTableView();
        }
    }

    public void supprimerOperation(Operation operation) {
        // Demande de confirmation avant la suppression
        boolean confirme = principalVue.afficherAlerteConfirmation(
                "Confirmation",
                "Êtes-vous sûr de vouloir supprimer cette opération ?",
                "Cette action est irréversible.");

        // Si l'utilisateur confirme, on procède à la suppression
        if (confirme) {
            operations.remove(operation);
            // Supprimer l'opération de toutes les gammes
            gammes.forEach(gamme -> gamme.getOperations().removeIf(op -> op.equals(operation)));

            // Mettre à jour l'interface
            principalVue.ecrireTreeTableView();
            principalVue.updateTableView();
        }
    }

    public void supprimerGamme(Gamme gamme) {
        // Demande de confirmation avant la suppression
        boolean confirme = principalVue.afficherAlerteConfirmation(
                "Confirmation",
                "Êtes-vous sûr de vouloir supprimer cette gamme ?",
                "Cette action est irréversible.");

        // Si l'utilisateur confirme, on procède à la suppression
        if (confirme) {
            gammes.remove(gamme);
            // Mettre à jour l'interface
            principalVue.ecrireTreeTableView();
            principalVue.updateTableView();
        }
    }

}
