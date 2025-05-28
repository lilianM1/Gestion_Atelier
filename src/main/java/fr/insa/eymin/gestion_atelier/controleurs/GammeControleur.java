package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import org.kordamp.ikonli.feather.Feather;

import fr.insa.eymin.gestion_atelier.modeles.Gamme;
import fr.insa.eymin.gestion_atelier.modeles.Operation;
import fr.insa.eymin.gestion_atelier.modeles.Produit;
import fr.insa.eymin.gestion_atelier.vues.GammeVue;
import fr.insa.eymin.gestion_atelier.vues.PrincipalVue;

public class GammeControleur {
    private ArrayList<Gamme> gammes;
    private ArrayList<Operation> operations;
    private PrincipalVue principalVue;

    private GammeVue vue;

    private ArrayList<Produit> produits;

    public GammeControleur(ArrayList<Gamme> gammes, PrincipalVue principalVue, ArrayList<Operation> operations,
            ArrayList<Produit> produits) {
        this.gammes = gammes;
        this.vue = new GammeVue(this);
        this.principalVue = principalVue;
        this.operations = operations;
        this.produits = produits;
    }

    public ArrayList<Produit> getProduits() {
        return produits;
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public boolean creerGamme(String refGamme, String dGamme, Produit produit, ArrayList<Operation> operations) {
        try {
            // Vérification des champs vides
            if (refGamme.isEmpty() || dGamme.isEmpty() || produit == null || operations.isEmpty()) {
                System.out.println("Champs vides");
                vue.afficherErreur("Erreur", "Champs vides",
                        "Veuillez remplir tous les champs obligatoires.");
                return false;
            }

            // Vérification si la gamme existe déjà
            if (gammes.stream().anyMatch(g -> g.getRefGamme().equals(refGamme))) {
                vue.afficherErreur("Erreur", "Gamme déjà existante",
                        "La gamme avec la référence " + refGamme + " existe déjà.");
                return false;
            }

            // Création et ajout de la gamme
            Gamme gamme = new Gamme(refGamme, dGamme, operations, produit);
            gammes.add(gamme);
            principalVue.afficherNotif("Gamme créée avec succès", Feather.CHECK_SQUARE,
                    principalVue.getRootContainer(), "info");
            principalVue.ecrireTreeTableView();
            return true;
        } catch (Exception e) {
            vue.afficherErreur("Erreur", "Erreur lors de la création de la gamme",
                    "Une erreur est survenue : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierGamme(Gamme gamme, String refGamme, String dGamme, Produit produit,
            ArrayList<Operation> operations) {
        try {
            // Vérification des champs vides
            if (refGamme.isEmpty() || dGamme.isEmpty() || produit == null || operations.isEmpty()) {
                vue.afficherErreur("Erreur", "Champs vides",
                        "Veuillez remplir tous les champs obligatoires.");
                return false;
            }

            // Vérification si la gamme existe déjà
            if (gammes.stream().anyMatch(g -> g.getRefGamme().equals(refGamme) && !g.equals(gamme))) {
                vue.afficherErreur("Erreur", "Gamme déjà existante",
                        "La gamme avec la référence " + refGamme + " existe déjà.");
                return false;
            }

            // Modification de la gamme
            gamme.setRefGamme(refGamme);
            gamme.setdGamme(dGamme);
            gamme.setProduit(produit);
            gamme.setOperations(operations);
            principalVue.ecrireTreeTableView();
            principalVue.afficherNotif("Gamme modifiée avec succès", Feather.CHECK_SQUARE,
                    principalVue.getRootContainer(), "info");
            return true;
        } catch (Exception e) {
            vue.afficherErreur("Erreur", "Erreur lors de la modification de la gamme",
                    "Une erreur est survenue : " + e.getMessage());
            return false;
        }
    }

    // ===========================================================================
    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public void setProduits(ArrayList<Produit> produits) {
        this.produits = produits;
    }

    // ===========================================================================

}
