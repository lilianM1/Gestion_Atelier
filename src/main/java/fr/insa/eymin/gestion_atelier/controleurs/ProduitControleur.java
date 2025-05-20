package fr.insa.eymin.gestion_atelier.controleurs;

import java.util.ArrayList;

import fr.insa.eymin.gestion_atelier.modeles.Produit;
import fr.insa.eymin.gestion_atelier.vues.ProduitVue;

public class ProduitControleur {
    private ArrayList<Produit> produits;
    private ProduitVue vue;
    
    /**
     * Constructeur du contrôleur de produits
     * @param produits la liste des produits
     */
    public ProduitControleur(ArrayList<Produit> produits) {
        this.produits = produits;
        this.vue = new ProduitVue(this);
    }
    
    /**
     * Affiche la fenêtre de création d'un produit
     */
    public void afficherFenetreCreation() {
        vue.afficherFenetreCreation();
    }
    
    /**
     * Crée un nouveau produit avec les données saisies
     * Vérifie la validité des entrées et génère une erreur si nécessaire
     * @param codeProduit code du produit
     * @param dProduit désignation du produit
     * @return true si le produit a été créé avec succès, false sinon
     */
    public boolean creerProduit(String codeProduit, String dProduit) {
        try {
            // Vérification des champs vides
            if (codeProduit.isEmpty() || dProduit.isEmpty()) {
                vue.afficherErreur("Erreur", "Champs vides", "Veuillez remplir tous les champs.");
                return false;
            }
            
            // Vérification si le produit existe déjà
            if (produits.stream().anyMatch(p -> p.getCodeProduit().equals(codeProduit))) {
                vue.afficherErreur("Erreur", "Produit déjà existant", "Le produit existe déjà.");
                return false;
            }
            
            // Création et ajout du produit
            Produit produit = new Produit(codeProduit, dProduit);
            produits.add(produit);
            return true;
            
        } catch (Exception ex) {
            vue.afficherErreur("Erreur", "Erreur lors de la création", ex.getMessage());
            return false;
        }
    }
}
