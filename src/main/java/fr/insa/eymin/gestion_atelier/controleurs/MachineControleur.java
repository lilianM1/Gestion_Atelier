package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import org.kordamp.ikonli.feather.Feather;

import fr.insa.eymin.gestion_atelier.modeles.EtatMachine;
import fr.insa.eymin.gestion_atelier.modeles.Machine;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;
import javafx.stage.Stage;

public class MachineControleur {
    private ArrayList<Machine> machines; // la liste des machines existantes, idéalement injectée via constructeur
    private PrincipalVue principalVue; // référence à la vue principale

    public MachineControleur(ArrayList<Machine> machines, PrincipalVue principalVue) {
        this.machines = machines;
        this.principalVue = principalVue;
    }

    public void creationMachine(String refMach, String dMach, float posX, float posY,
            float coutHoraire, float dureeUtil, EtatMachine etatMach) {
        // Vérification que la référence n'existe pas déjà
        if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMach))) {
            return;
        }

        // Création de la nouvelle machine
        Machine machine = new Machine(refMach, dMach, posX, posY, coutHoraire, dureeUtil, etatMach);
        machines.add(machine);

        // Mise à jour de l'affichage de l'atelier

        principalVue.dessinerAtelier();
        principalVue.afficherNotif("Machine créée avec succès", Feather.CHECK_SQUARE, principalVue.getRootContainer());

    }

    // ---------------------------------------------------------------------------

    public static void closeWindow(Stage creerMachineStage) {
        creerMachineStage.close();
    }

    // ---------------------------------------------------------------------------

    public boolean machineExistante(String refMach) {
        if (machines.stream().anyMatch(m -> m.getRefEquipement().equals(refMach))) {
            return true;
        } else {
            return false;
        }
    }

    // ===========================================================================

}
