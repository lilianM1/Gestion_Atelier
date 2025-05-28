package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import org.kordamp.ikonli.feather.Feather;

import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.vues.PosteVue;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
import javafx.stage.Stage;

public class PosteControleur {
    private ArrayList<Poste> postes;
    private ArrayList<Machine> machines;
    private PrincipalVue principalVue;

    public PosteControleur(ArrayList<Poste> postes, ArrayList<Machine> machines, PrincipalVue principalVue) {
        this.postes = postes;
        this.machines = machines;
        this.principalVue = principalVue;
    }

    public void creerPoste() {
        PosteVue vue = new PosteVue(this);
        vue.afficherFenetreCreation();
    }

    public void ajouterPoste(String reference, String designation, ArrayList<Machine> machinesSelectionnees) {
        Poste nouveauPoste = new Poste(reference, designation, machinesSelectionnees);
        postes.add(nouveauPoste);
        principalVue.afficherNotif("Poste créé avec succès", Feather.CHECK_SQUARE, principalVue.getRootContainer(),
                "info");
    }

    public void modifierPoste(Poste poste, String reference, String designation,
            ArrayList<Machine> machinesSelectionnees) {
        poste.setRefEquipement(reference);
        poste.setdEquipement(designation);
        poste.setMachines(machinesSelectionnees);
        principalVue.ecrireTreeTableView();
        principalVue.afficherNotif("Poste modifié avec succès", Feather.CHECK_SQUARE, principalVue.getRootContainer(),
                "info");
    }

    public boolean posteExistant(String reference) {
        return postes.stream().anyMatch(p -> p.getRefEquipement().equals(reference));
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public static void fermerFenetre(Stage stage) {
        stage.close();
    }
}
