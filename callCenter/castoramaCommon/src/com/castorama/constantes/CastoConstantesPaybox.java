package com.castorama.constantes;

/**
 * Classe de constantes pour les paiements Paybox.
 *
 * @author Logica.
 * @version 0.1.
 */
public class CastoConstantesPaybox
{
    /* 
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */
    
    /**
     * Paramètre n° d'autorisation du paiement.
     */
    public static final String PARAMETRE_NUMERO_AUTORISATION = "auto";
    
    /**
     * Paramètre référence de la commande (identifiant ATG de la commande).
     */
    public static final String PARAMETRE_REFERENCE_COMMANDE = "ref";
    
    /**
     * Identifiant de transaction (n° d'appel séquentiel).
     */
    public static final String PARAMETRE_IDENTIFIANT_TRANSACTION = "trans";
    
    /**
     * Numéro de transaction.
     */
    public static final String PARAMETRE_NUMERO_TRANSACTION = "numt";
    
    /* 
     * ------------------------------------------------------------------------
     * Constructeurs
     * ------------------------------------------------------------------------
     */
    
    /**
     * Empêcher l'instanciation.
     */
    private CastoConstantesPaybox()
    {}
}