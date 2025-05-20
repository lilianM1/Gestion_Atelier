package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.modeles.Poste;
import fr.insa.eymin.gestion_atelier.vues.PosteVue;
import javafx.stage.Stage;

public class PosteControleur {
    private ArrayList<Poste> postes;
    private ArrayList<Machine> machines;

    public PosteControleur(ArrayList<Poste> postes, ArrayList<Machine> machines) {
        this.postes = postes;
        this.machines = machines;
    }

    public void creerPoste() {
        PosteVue vue = new PosteVue(this);
        vue.afficherFenetreCreation();
    }

    public void ajouterPoste(String reference, String designation, ArrayList<Machine> machinesSelectionnees) {
        Poste nouveauPoste = new Poste(reference, designation, machinesSelectionnees);
        postes.add(nouveauPoste);
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
