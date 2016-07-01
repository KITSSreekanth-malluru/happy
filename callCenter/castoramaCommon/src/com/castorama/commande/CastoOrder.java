package com.castorama.commande;

import java.sql.Timestamp;
import java.util.Date;

import atg.commerce.order.OrderImpl;
import atg.repository.RepositoryItem;

import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.order.pricing.PreparationPriceInfo;

/**
 * . CastoOrder
 */
/* Code Review */public class CastoOrder extends OrderImpl/* Code Review */
{
    /*
     * ------------------------------------------------------------------------
     * Constantes
     * ------------------------------------------------------------------------
     */

    /**
     * Valeur qui indique que le chois de l'étage de livraison n'a pas été
     * renseigné.
     */
    public static final int ETAGE_LIVRAISON_NON_RENSEIGNE = 0;

    /**
     * Valeur qui indique que le chois de l'étage de livraison est le RDC.
     */
    public static final int ETAGE_LIVRAISON_RDC = 1;

    /**
     * Valeur qui indique que le chois de l'étage de livraison est l'étage (1er,
     * 2ème ,etc.).
     */
    public static final int ETAGE_LIVRAISON_ETAGE = 2;

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3249923885129139697L;

    /***************************************************************************
     * Code Review /** Le PreparationPriceInfo.
     */
    private PreparationPriceInfo m_PreparationPriceInfo;

    /***************************************************************************
     * Code Review
     *  /*
     * ------------------------------------------------------------------------
     * Accesseurs
     * ------------------------------------------------------------------------
     */
    /* Code Review */
    /**
     * Sets property PreparationPriceInfo.
     * 
     * @param a_PreparationPriceInfo
     *            PreparationPriceInfo to set.
     */
    public void setPreparationPriceInfo(PreparationPriceInfo a_PreparationPriceInfo)
    {
        m_PreparationPriceInfo = a_PreparationPriceInfo;
    }

    /**
     * Returns property PreparationPriceInfo.
     * 
     * @return PreparationPriceInfo The property PreparationPriceInfo.
     */
    public PreparationPriceInfo getPreparationPriceInfo()
    {
        return m_PreparationPriceInfo;
    }

    /**
     * Renvoie le montant des chèques cadeaux.
     * 
     * @return double Le montant des chèques cadeaux.
     */
    public double getMontantChequeCadeau()
    {
        Object l_montant = getPropertyValue("montantChequeCadeau");

        return (null != l_montant) ? ((Double) l_montant).doubleValue() : 0;
    }

    /**
     * Fixe le montant des chèques cadeaux.
     * 
     * @param a_montantChequeCadeau
     *            Le nouveau montant des chèques cadeaux..
     */
    public void setMontantChequeCadeau(double a_montantChequeCadeau)
    {
        setPropertyValue("montantChequeCadeau", new Double(a_montantChequeCadeau));
    }

    /**
     * Indique si la commande a été réglée avec des chèques cadeaux ou non.
     * 
     * @return boolean true si la commande a été réglée avec des chèques
     *         cadeaux, false sinon.
     */
    public boolean isPaiementChequecadeau()
    {
        Object l_paiement = getPropertyValue("paiementChequeCadeau");

        return (null != l_paiement) ? Boolean.valueOf(l_paiement.toString()).booleanValue() : false;
    }

    /**
     * Fixe si la commande a été réglée avec des chèques cadeaux ou non.
     * 
     * @param a_paiementChequecadeau
     *            true si la commande a été réglée avec des chèques cadeaux,
     *            false sinon.
     */
    public void setPaiementChequecadeau(boolean a_paiementChequecadeau)
    {
        setPropertyValue("paiementChequeCadeau", Boolean.valueOf(a_paiementChequecadeau));
    }

    /* /*Code Review */
    /**
     * Accesseur en lecture pour la propriété "messageTransporteur".
     * 
     * @return String La valeur de la propriété "messageTransporteur".
     */
    public String getMessageTransporteur()
    {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_MESSAGE_TRANSPORTEUR);
    }

    /**
     * Accesseur en écriture pour la propriété "messageTransporteur".
     * 
     * @param a_messageTransporteur
     *            La nouvelle valeur de la propriété "messageTransporteur".
     */
    public void setMessageTransporteur(String a_messageTransporteur)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_MESSAGE_TRANSPORTEUR, a_messageTransporteur);
    }

    /**
     * Accesseur en lecture pour la propriété "optionPaiementAtout".
     * 
     * @return String La valeur de la propriété "optionPaiementAtout".
     */
    public String getOptionPaiementAtout()
    {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_OPTION_PAIEMENT_ATOUT);
    }

    /**
     * Accesseur en écriture pour la propriété "optionPaiementAtout".
     * 
     * @param a_optionPaiementAtout
     *            La nouvelle valeur de la propriété "optionPaiementAtout".
     */
    public void setOptionPaiementAtout(String a_optionPaiementAtout)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_OPTION_PAIEMENT_ATOUT, a_optionPaiementAtout);
    }

    /**
     * Accesseur en lecture pour la propriété "commandecadeau".
     * 
     * @return boolean La valeur de la propriété "commandecadeau".
     */
    public boolean isCommandeCadeau()
    {
        return ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_COMMANDE_CADEAU)).booleanValue();
    }

    /**
     * Accesseur en écriture pour la propriété "commandecadeau".
     * 
     * @param a_commandeCadeau
     *            La nouvelle valeur de la propriété "commandecadeau".
     */
    public void setCommandeCadeau(boolean a_commandeCadeau)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_COMMANDE_CADEAU, Boolean.valueOf(a_commandeCadeau));
    }

    /**
     * Accesseur en lecture pour la propriété "commandeCPOK".
     * 
     * @return boolean La valeur de la propriété "commandeCPOK".
     */
    public boolean isCommandeCPOK()
    {
        boolean l_valeur = false;
        if (null!=getPropertyValue("commandeCPOK"))
        {
            if ("true".equals(getPropertyValue("commandeCPOK").toString()))
                l_valeur = true;
        }
        return l_valeur;
    }

    /**
     * Accesseur en écriture pour la propriété "commandeCPOK".
     * 
     * @param a_commandeCPOK
     *            La nouvelle valeur de la propriété "commandeCPOK".
     */
    public void setCommandeCPOK(boolean a_commandeCPOK)
    {
        setPropertyValue("commandeCPOK", Boolean.valueOf(a_commandeCPOK));
    }

    /**
     * Accesseur en lecture pour la propriété "Commentaire".
     * 
     * @return String La valeur de la propriété "Commentaire".
     */
    public String getCommentaire()
    {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_COMMENTAIRE);
    }

    /**
     * Accesseur en écriture pour la propriété "Commentaire".
     * 
     * @param a_commentaire
     *            La nouvelle valeur de la propriété "Commentaire".
     */
    public void setCommentaire(String a_commentaire)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_COMMENTAIRE, a_commentaire);
    }

    /**
     * Accesseur en lecture pour la propriété "numCarte".
     * 
     * @return String La valeur de la propriété "numCarteAtout".
     */
    /*
     * Code Review public String getNumCarteAtout() { return (String)
     * getPropertyValue(CastoConstantesCommande.NUMERO_CARTE_ATOUT); }
     */

    /**
     * Accesseur en lecture pour la propriété "creationDate".
     * 
     * @return Date La valeur de la propriété "creationDate".
     */
    public Date getSubmittedDate()
    {
        return (Date) getPropertyValue(CastoConstantesCommande.SUBMITTED_DATE);
    }

    /**
     * Accesseur en lecture pour la propriété "PayeCarteAtout".
     * 
     * @return boolean La valeur de la propriété "PayeCarteAtout".
     */
    public boolean isPayeCarteAtout()
    {
        if ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT) == null)
            return false;
        else
            return ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT)).booleanValue();
    }

    /**
     * Accesseur en écriture pour la propriété "PayeCarteAtout".
     * 
     * @param a_PayeCarteAtout
     *            La nouvelle valeur de la propriété "PayeCarteAtout".
     */
    public void setPayeCarteAtout(boolean a_PayeCarteAtout)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT, Boolean.valueOf(a_PayeCarteAtout));
    }

    /* Projet Castorama - La Défense *** logica */
    /**
     * Accesseur en lecture pour la propriété "CarteAtoutIndetermine".
     * 
     * @return boolean La valeur de la propriété "CarteAtoutINdetermine".
     */
    public boolean isCarteAtoutIndetermine()
    {
        if ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE) == null)
            return false;
        else
            return ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE))
                    .booleanValue();
    }

    /**
     * Accesseur en écriture pour la propriété "CarteAtoutIndetermine".
     * 
     * @param a_CarteAtoutIndetermine
     *            La nouvelle valeur de la propriété "CarteAtoutIndetermine".
     */
    public void setCarteAtoutIndetermine(boolean a_CarteAtoutIndetermine)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE, Boolean
                .valueOf(a_CarteAtoutIndetermine));
    }

    /* Fin Projet Castorama - LA Défense */

    /**
     * Accesseur en lecture pour la propriété "origineMagasin".
     * 
     * @return Un RepositoryItem correspondant au magasin ou null en cas
     *         d'échec.
     */
    public RepositoryItem getOrigineMagasin()
    {
        RepositoryItem l_magasin;
        Object l_obj = getPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ORIGINE_MAGASIN);

        if (null != l_obj)
        {
            l_magasin = (RepositoryItem) l_obj;
        }
        else
        {
            l_magasin = null;
        }

        return l_magasin;
    }

    /**
     * Accesseur en écriture pour la propriété "origineMagasin".
     * 
     * @param a_magasin
     *            Le magasin.
     */
    public void setOrigineMagasin(RepositoryItem a_magasin)
    {
        setPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ORIGINE_MAGASIN, a_magasin);
    }

    /**
     * Méthode qui renvoie les indications sur l'étage de livraison sélectionné
     * par l'internaute.
     * 
     * @return Un entier correspondant aux indications sur l'étage de livraison.
     */
    public int getEtageLivraison()
    {
        Object l_prop = getPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ETAGE_LIVRAISON);

        if (null != l_prop)
        {
            return ((Integer) l_prop).intValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe les indications sur l'étage de livraison sélectionné par
     * l'internaute.
     * 
     * @param a_etageLivraison
     *            La nouvelle indication sur l'étage de livraison sélectionné
     *            par l'internaute.
     */
    public void setEtageLivraison(int a_etageLivraison)
    {
        setPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ETAGE_LIVRAISON, new Integer(a_etageLivraison));
    }

    /**
     * Méthode qui renvoie la valeur du champ JSessionID de la commande.
     * 
     * @return La valeur du champ JSessionID de la commande.
     */
    public String getJSessionID()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID);

        if (null != l_prop)
        {
            return l_prop.toString();
        }
        return null;
    }

    /**
     * Méthode qui fixe la valeur du champ JSessionID de la commande.
     * 
     * @param a_jSessionID
     *            La nouvelle valeur du champ JSessionID de la commande.
     */
    public void setJSessionID(String a_jSessionID)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID, a_jSessionID);
    }

    /**
     * Méthode qui renvoie l'identifiant de transaction paybox de la commande.
     * 
     * @return L'identifiant de transaction paybox de la commande.
     */
    public int getIdTransaction()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX);

        if (null != l_prop)
        {
            return ((Integer) l_prop).intValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe l'identifiant de transaction paybox de la commande.
     * 
     * @param a_idTransaction
     *            Le nouvel identifiant de transaction paybox de la commande.
     */
    public void setIdTransaction(int a_idTransaction)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX, new Integer(
                a_idTransaction));
    }

    /**
     * Méthode qui renvoie le numéro d'autorisation du paiement paybox.
     * 
     * @return Le numéro d'autorisation du paiement paybox.
     */
    public String getNumAutorisation()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX);

        if (null != l_prop)
        {
            return l_prop.toString();
        }
        return null;
    }

    /**
     * Méthode qui fixe le numéro d'autorisation du paiement paybox.
     * 
     * @param a_numAutorisation
     *            Le nouveau numéro d'autorisation du paiement paybox.
     */
    public void setNumAutorisation(String a_numAutorisation)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX, a_numAutorisation);
    }

    /**
     * Méthode qui renvoie le numéro de transaction paybox de la commande.
     * 
     * @return Le numéro de transaction paybox de la commande.
     */
    public int getNumTransaction()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX);

        if (null != l_prop)
        {
            return ((Integer) l_prop).intValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le numéro de transaction paybox de la commande.
     * 
     * @param a_numTransaction
     *            Le nouveau numéro de transaction paybox de la commande.
     */
    public void setNumTransaction(int a_numTransaction)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX,
                new Integer(a_numTransaction));
    }

    /**
     * Méthode qui renvoie la date de transaction de paiement paybox.
     * 
     * @return La date de transaction de paiement paybox.
     */
    public Timestamp getDateTransaction()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX);

        if (null != l_prop)
        {
            return (Timestamp) l_prop;
        }
        return null;
    }

    /**
     * Méthode qui fixe la date de transaction de paiement paybox.
     * 
     * @param a_dateTransaction
     *            La nouvelle date de transaction de paiement paybox.
     */
    public void setDateTransaction(Timestamp a_dateTransaction)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX, a_dateTransaction);
    }

    /**
     * Méthode qui renvoie la date de paiement paybox.
     * 
     * @return La date de paiement paybox.
     */
    public Timestamp getDatePaiement()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX);

        if (null != l_prop)
        {
            return (Timestamp) l_prop;
        }
        return null;
    }

    /**
     * Méthode qui fixe la date de paiement paybox.
     * 
     * @param a_datePaiement
     *            La nouvelle date de paiement paybox.
     */
    public void setDatePaiement(Timestamp a_datePaiement)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX, a_datePaiement);
    }

    /**
     * Méthode qui renvoie la date d'expiration de la carte l'atout.
     * 
     * @return La date d'expiration de la carte l'atout.
     */
    public Timestamp getDateExpirationAtout()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT);

        if (null != l_prop)
        {
            return (Timestamp) l_prop;
        }
        return null;
    }

    /**
     * Méthode qui fixe la date d'expiration de la carte l'atout.
     * 
     * @param a_dateExpirationAtout
     *            La nouvelle date d'expiration de la carte l'atout.
     */
    public void setDateExpirationAtout(Timestamp a_dateExpirationAtout)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT, a_dateExpirationAtout);
    }

    /**
     * Méthode qui renvoie le montant total TTC de la commande.
     * 
     * @return Le montant total TTC de la commande.
     */
    public double getMontantTotalCommandeTTC()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_TOTAL_COMMANDE_TTC);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant total TTC de la commande.
     * 
     * @param a_montantTotalCommandeTTC
     *            Le nouveau montant total TTC de la commande.
     */
    public void setMontantTotalCommandeTTC(double a_montantTotalCommandeTTC)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_TOTAL_COMMANDE_TTC, new Double(
                a_montantTotalCommandeTTC));
    }

    /**
     * Méthode qui renvoie le montant des frais de livraison non remisé.
     * 
     * @return Le montant des frais de livraison non remisé.
     */
    public double getMontantFraisLivraisonNonRemise()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_LIVRAISON_NON_REMISE);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant des frais de livraison non remisé.
     * 
     * @param a_montantFraisLivraisonNonRemise
     *            Le nouveau montant des frais de livraison non remisé.
     */
    public void setMontantFraisLivraisonNonRemise(double a_montantFraisLivraisonNonRemise)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_LIVRAISON_NON_REMISE, new Double(
                a_montantFraisLivraisonNonRemise));
    }

    /**
     * Méthode qui renvoie le montant des frais de montée à l'étage non remisé.
     * 
     * @return Le montant des frais de montée à l'étage non remisé.
     */
    public double getMontantFraisMonteeEtageNonRemise()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_MONTEE_ETAGE_NON_REMISE);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant des frais de montée à l'étage non remisé.
     * 
     * @param a_montantFraisMonteeEtageNonRemise
     *            Le nouveau montant des frais de montée à l'étage non remisé.
     */
    public void setMontantFraisMonteeEtageNonRemise(double a_montantFraisMonteeEtageNonRemise)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_MONTEE_ETAGE_NON_REMISE, new Double(
                a_montantFraisMonteeEtageNonRemise));
    }

    /**
     * Méthode qui renvoie le montant de la remise sur les frais de livraison et
     * de montée à l'étage.
     * 
     * @return le montant de la remise sur les frais de livraison et de montée à
     *         l'étage.
     */
    public double getMontantRemiseLivraisonEtMonteeEtage()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_LIVRAISON_ET_MONTEE_ETAGE);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant de la remise sur les frais de livraison et de
     * montée à l'étage.
     * 
     * @param a_montantRemiseLivraisonEtMonteeEtage
     *            Le nouveau montant de la remise sur les frais de livraison et
     *            de montée à l'étage.
     */
    public void setMontantRemiseLivraisonEtMonteeEtage(double a_montantRemiseLivraisonEtMonteeEtage)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_LIVRAISON_ET_MONTEE_ETAGE, new Double(
                a_montantRemiseLivraisonEtMonteeEtage));
    }

    /**
     * Méthode qui renvoie le montant non remisé des frais de préparation (MAD).
     * 
     * @return Le montant non remisé des frais de préparation (MAD).
     */
    public double getMontantFraisDePreparationNonRemise()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_NON_REMISE);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant non remisé des frais de préparation (MAD).
     * 
     * @param a_montantFraisDePreparationNonRemise
     *            Le nouveau montant non remisé des frais de préparation (MAD).
     */
    public void setMontantFraisDePreparationNonRemise(double a_montantFraisDePreparationNonRemise)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_NON_REMISE, new Double(
                a_montantFraisDePreparationNonRemise));
    }

    /**
     * Méthode qui renvoie le montant remisé des frais de préparation (MAD).
     * 
     * @return Le montant remisé des frais de préparation (MAD).
     */
    public double getMontantFraisDePreparationRemise()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_REMISE);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant remisé des frais de préparation (MAD).
     * 
     * @param a_montantFraisDePreparationRemise
     *            Le nouveau montant remisé des frais de préparation (MAD).
     */
    public void setMontantFraisDePreparationRemise(double a_montantFraisDePreparationRemise)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_REMISE, new Double(
                a_montantFraisDePreparationRemise));
    }

    /**
     * Méthode qui renvoie le montant total des remises de la commande.
     * 
     * @return Le montant total des remises de la commande.
     */
    public double getMontantRemiseTotal()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_TOTAL);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant total des remises de la commande.
     * 
     * @param a_montantRemiseTotal
     *            Le montant total des remises de la commande.
     */
    public void setMontantRemiseTotal(double a_montantRemiseTotal)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_TOTAL, new Double(a_montantRemiseTotal));
    }

    /**
     * Méthode qui renvoie le montant total de la PFT de la commande.
     * 
     * @return le montant total de la PFT de la commande.
     */
    public double getMontantPFTTotal()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.MONTANT_PFT);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant total de la PFT de la commande.
     * 
     * @param a_montantPFTTotal
     *            Le montant total de la PFT de la commande.
     */
    public void setMontantPFTTotal(double a_montantPFTTotal)
    {
        setPropertyValue(CastoConstantesCommande.MONTANT_PFT, new Double(a_montantPFTTotal));
    }

    /**
     * Méthode qui renvoie le montant total de la PFT de la commande.
     * 
     * @return le montant total de la PFT de la commande.
     */
    public double getMontantPFLTotal()
    {
        Object l_prop = getPropertyValue(CastoConstantesCommande.MONTANT_PFL);

        if (null != l_prop)
        {
            return ((Double) l_prop).doubleValue();
        }
        return 0;
    }

    /**
     * Méthode qui fixe le montant total de la PFT de la commande.
     * 
     * @param a_montantPFLTotal
     *            Le montant total de la PFT de la commande.
     */
    public void setMontantPFLTotal(double a_montantPFLTotal)
    {
        setPropertyValue(CastoConstantesCommande.MONTANT_PFL, new Double(a_montantPFLTotal));
    }

    /**
     * . Renvoie le session Id
     * 
     * @return session_id
     */
    public String getSessionId()
    {
        String l_sessionId = (String) getPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID);
        return l_sessionId;
    }

    /**
     * . Fixe le session id
     * 
     * @param a_sessionId
     *            sessionId
     */
    public void setSessionId(String a_sessionId)
    {
        setPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID, a_sessionId);
    }

    /**
     * . Renvoie le PayeCarteAtout
     * 
     * @return PayeCarteAtout
     */
    public Boolean getPayeCarteAtout()
    {
        Boolean l_PayeCarteAtout = (Boolean) getPropertyValue("PayeCarteAtout");
        return l_PayeCarteAtout;
    }

    /**
     * . Fixe le PayeCarteAtout
     * 
     * @param a_PayeCarteAtout
     *            PayeCarteAtout
     */
    public void setPayeCarteAtout(Boolean a_PayeCarteAtout)
    {
        setPropertyValue("PayeCarteAtout", a_PayeCarteAtout);
    }

    /**
     * . Renvoie le idTransaction
     * 
     * @return idTransaction
     */
    public Integer getIdentifiantTransaction()
    {
        Integer l_idTransaction = (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX);
        return l_idTransaction;
    }

    /**
     * . Fixe le idTransaction
     * 
     * @param a_idTransaction
     *            idTransaction
     */
    public void setIdentifiantTransaction(Integer a_idTransaction)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX, a_idTransaction);
    }

    /**
     * . Renvoie le numTransaction
     * 
     * @return numTransaction
     */
    public Integer getNumeroTransaction()
    {
        Integer l_numAutorisation = (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX);
        return l_numAutorisation;
    }

    /**
     * . Fixe le numTransaction
     * 
     * @param a_numTransaction
     *            numTransaction
     */
    public void setNumeroTransaction(Integer a_numTransaction)
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX, a_numTransaction);
    }

    /**
     * . Renvoie le dateValidAtout
     * 
     * @return dateValidAtout
     */
    /*
     * Code Review public Timestamp getDateValidAtout() { Timestamp
     * l_dateValidAtout =
     * (Timestamp)getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT);
     * return l_dateValidAtout; }
     */

    /**
     * . Fixe le dateValidAtout
     * 
     * @param a_dateValidAtout
     *            dateValidAtout
     */
    /*
     * Code Review public void setDateValidAtout(Timestamp a_dateValidAtout) {
     * setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT,
     * a_dateValidAtout); }
     */

    /**
     * . Renvoie le BOState
     * 
     * @return BOState
     */
    public String getBOState()
    {
        String l_BOState = (String) getPropertyValue("BOState");
        return l_BOState;
    }

    /**
     * . Fixe le BOState
     * 
     * @param a_BOState
     *            BOState
     */
    public void setBOState(String a_BOState)
    {
        setPropertyValue("BOState", a_BOState);
    }

    /**
     * . Renvoie le BOStateDetail
     * 
     * @return BOStateDetail
     */
    public String getBOStateDetail()
    {
        String l_BOStateDetail = (String) getPropertyValue("BOStateDetail");
        return l_BOStateDetail;
    }

    /**
     * . Fixe le BOStateDetail
     * 
     * @param a_BOStateDetail
     *            BOStateDetail
     */
    public void setBOStateDetail(String a_BOStateDetail)
    {
        setPropertyValue("BOStateDetail", a_BOStateDetail);
    }

    /**
     * . Renvoie le profileId
     * 
     * @return profileId
     */
    public String getProfileId()
    {
        String l_profileId = (String) getPropertyValue("profileId");
        return l_profileId;
    }

    /**
     * . Fixe le profileId
     * 
     * @param a_profileId
     *            profileId
     */
    public void setProfileId(String a_profileId)
    {
        setPropertyValue("profileId", a_profileId);
    }

    /* Code Review */
    /**
     * . Fixe la date d'export
     * 
     * @param a_date
     *            a_date
     */
    public void setExportDate(Timestamp a_date)
    {
        setPropertyValue("exportdate", a_date);
    }

    /* Code Review */
    /**
     * . Numéro de version de la commande
     * 
     * @return version
     */
    public int getVersion()
    {
        Object l_profileId = (Object) getPropertyValue("version");
        if (null != l_profileId)
        {
            return ((Integer) l_profileId).intValue();
        }
        return 0;
    }

    /*
     * Logica, Fiche Mantis 1294, le 27/10/2008 : Identifier les commandes
     * faites par des admins
     */
    /**
     * . booléen pour flaguer les commandes faites par des admins
     * 
     * @param a_cdeAdmin
     *            a_cdeAdmin
     */
    public void setCdeAdmin(boolean a_cdeAdmin)
    {
        setPropertyValue("cdeAdmin", Boolean.valueOf(a_cdeAdmin));
    }

    /**
     * . booléen pour flaguer les commandes faites par des admins
     * 
     * @return version
     */
    public boolean getCdeAdmin()
    {
        Object l_cdeAdmin = (Object) getPropertyValue("cdeAdmin");
        if (null != l_cdeAdmin)
        {
            return ((Boolean) l_cdeAdmin).booleanValue();
        }
        return false;
    }

    /**
     * . Login de l'admin qui passe la commande de l'internaute
     * 
     * @param a_adminLogin
     *            a_adminLogin
     */
    public void setAdminLogin(String a_adminLogin)
    {
        setPropertyValue("adminLogin", a_adminLogin);
    }

    /**
     * . Login de l'admin qui passe la commande de l'internaute
     * 
     * @return adminLogin
     */
    public String getAdminLogin()
    {
        Object l_adminLogin = (Object) getPropertyValue("adminLogin");
        if (null != l_adminLogin)
        {
            return String.valueOf(l_adminLogin);
        }
        return null;
    }
    /*
     * Fin Logica, Fiche Mantis 1294, le 27/10/2008 : Identifier les commandes
     * faites par des admins
     */
}