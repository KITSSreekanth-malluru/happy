package com.castorama.commerce.order;

import java.sql.Timestamp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.commerce.order.CreditCard;
import atg.commerce.order.GiftCertificate;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentAddressContainer;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingAddressContainer;

import atg.core.util.Address;

import atg.repository.RemovedItemException;
import atg.repository.RepositoryItem;

import com.castorama.constantes.CastoConstantes;
import com.castorama.constantes.CastoConstantesCommande;
import com.castorama.constantes.CastoConstantesDefense;
import com.castorama.constantes.CastoConstantesOrders;

/**
 * Add Castorama custom fields.
 *
 * @author Epam Team
 */
public class CastOrderImpl extends OrderImpl {

    /** ETAGE_LIVRAISON_NON_RENSEIGNE constant. */
    public static final int ETAGE_LIVRAISON_NON_RENSEIGNE = 0;

    /** ETAGE_LIVRAISON_RDC constant. */
    public static final int ETAGE_LIVRAISON_RDC = 1;

    /** ETAGE_LIVRAISON_ETAGE constant. */
    public static final int ETAGE_LIVRAISON_ETAGE = 2;
    
    /** Ids of items removed after InventoryCheck execution */
    private Set<String> removedItemsIds;

	/**
     * Creates a new CastOrderImpl object.
     */
    public CastOrderImpl() {
    }

    /**
     * Returns billing property.
     *
     * @return billing property.
     */
    public PaymentGroup getBilling() {
        PaymentGroup result = null;
        List paymentGroups = getPaymentGroups();
        for (int cn = 0; cn < paymentGroups.size(); cn++) {
            Object item = paymentGroups.get(cn);
            if (item instanceof PaymentAddressContainer) {
                PaymentAddressContainer paymentGroup = (PaymentAddressContainer) item;
                Address address = paymentGroup.getBillingAddress();
                if (address != null) {
                    result = (PaymentGroup) item;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns shippingAddress property.
     *
     * @return shippingAddress property.
     */
    public Address getShippingAddress() {
        Address result = null;
        List shippingGroups = getShippingGroups();
        for (int cn = 0; cn < shippingGroups.size(); cn++) {
            Object item = shippingGroups.get(cn);
            if (item instanceof ShippingAddressContainer) {
                ShippingAddressContainer shippingGroup = (ShippingAddressContainer) item;
                Address address = shippingGroup.getShippingAddress();
                if (address != null) {
                    result = address;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns amountFromClaims property.
     *
     * @return amountFromClaims property.
     */
    public double getAmountFromClaims() {
        double result = 0;
        String cur = getPriceInfo().getCurrencyCode();
        if (cur != null) {
            List paymentGroups = getPaymentGroups();
            for (int cn = 0; cn < paymentGroups.size(); cn++) {
                Object item = paymentGroups.get(cn);
                if (item instanceof GiftCertificate) {
                    GiftCertificate certificate = (GiftCertificate) item;
                    if (cur.equals(certificate.getCurrencyCode())) {
                        result += certificate.getAmount();
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns amountFromCreditCards property.
     *
     * @return amountFromCreditCards property.
     */
    public double getAmountFromCreditCards() {
        double result = 0;
        String cur = getPriceInfo().getCurrencyCode();
        if (cur != null) {
            List paymentGroups = getPaymentGroups();
            for (int cn = 0; cn < paymentGroups.size(); cn++) {
                Object item = paymentGroups.get(cn);
                if (item instanceof CreditCard) {
                    CreditCard card = (CreditCard) item;
                    if (cur.equals(card.getCurrencyCode())) {
                        result += card.getAmount();
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns shippingLists property.
     *
     * @return shippingLists property.
     */
    public List getShippingLists() {
        return getShippingGroups();
    }

    /**
     * Returns commandeCadeau property.
     *
     * @return commandeCadeau property.
     */
    public boolean isCommandeCadeau() {
        return ((Boolean) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ORDER_IS_GIFT)).booleanValue();
    }

    /**
     * Sets the value of the commandeCadeau property.
     *
     * @param pcommandeCadeau parameter to set.
     */
    public void setCommandeCadeau(boolean pcommandeCadeau) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ORDER_IS_GIFT, Boolean.valueOf(pcommandeCadeau));
    }

    /**
     * Returns montantChequeCadeau property.
     *
     * @return montantChequeCadeau property.
     */
    public double getMontantChequeCadeau() {
        Object montant = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_MONTANT_CHEQUE_CADEAU);

        return (montant != null) ? ((Double) montant).doubleValue() : 0;
    }

    /**
     * Sets the value of the montantChequeCadeau property.
     *
     * @param pMontantChequeCadeau parameter to set.
     */
    public void setMontantChequeCadeau(double pMontantChequeCadeau) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_MONTANT_CHEQUE_CADEAU, new Double(pMontantChequeCadeau));
    }

    /**
     * Returns paiementChequecadeau property.
     *
     * @return paiementChequecadeau property.
     */
    public boolean isPaiementChequecadeau() {
        Object paiement = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAIEMENT_CHEQUE_CADEAU);

        return (paiement != null) ? Boolean.valueOf(paiement.toString()).booleanValue() : false;
    }

    /**
     * Sets the value of the paiementChequecadeau property.
     *
     * @param pPaiementChequecadeau parameter to set.
     */
    public void setPaiementChequecadeau(boolean pPaiementChequecadeau) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAIEMENT_CHEQUE_CADEAU,
                         Boolean.valueOf(pPaiementChequecadeau));
    }

    /**
     * Returns messageTransporteur property.
     *
     * @return messageTransporteur property.
     */
    public String getMessageTransporteur() {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_MESSAGE_TRANSPORTEUR);
    }

    /**
     * Sets the value of the messageTransporteur property.
     *
     * @param pMessageTransporteur parameter to set.
     */
    public void setMessageTransporteur(String pMessageTransporteur) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_MESSAGE_TRANSPORTEUR, pMessageTransporteur);
    }

    /**
     * Returns optionPaiementAtout property.
     *
     * @return optionPaiementAtout property.
     */
    public String getOptionPaiementAtout() {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_OPTION_PAIEMENT_ATOUT);
    }

    /**
     * Sets the value of the optionPaiementAtout property.
     *
     * @param pOptionPaiementAtout parameter to set.
     */
    public void setOptionPaiementAtout(String pOptionPaiementAtout) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_OPTION_PAIEMENT_ATOUT, pOptionPaiementAtout);
    }

    /**
     * Returns commandeCPOK property.
     *
     * @return commandeCPOK property.
     */
    public boolean isCommandeCPOK() {
        boolean valeur = false;
        Object o = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMANDE_CPOK);
        if (o != null) {
            if (CastoConstantes.TRUE.equals(o.toString())) {
                valeur = true;
            }
        }
        return valeur;
    }

    /**
     * Sets the value of the commandeCPOK property.
     *
     * @param pCommandeCPOK parameter to set.
     */
    public void setCommandeCPOK(boolean pCommandeCPOK) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_COMMANDE_CPOK, Boolean.valueOf(pCommandeCPOK));
    }

    /**
     * Returns commentaire property.
     *
     * @return commentaire property.
     */
    public String getCommentaire() {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_COMMENTAIRE);
    }

    /**
     * Sets the value of the commentaire property.
     *
     * @param pCommentaire parameter to set.
     */
    public void setCommentaire(String pCommentaire) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_COMMENTAIRE, pCommentaire);
    }
    
    /**
     * Returns Castorama card number property.
     *
     * @return castoramaCardNumber property.
     */
    public String getCastoramaCardNumber() {
        return (String) getPropertyValue(CastoConstantesCommande.PROPERTY_NUMERO_CARTE_ATOUT);
    }

    /**
     * Sets the value of the Castorama card number property.
     *
     * @param castoramaCardNumber parameter to set.
     */
    public void setCastoramaCardNumber(String castoramaCardNumber) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_NUMERO_CARTE_ATOUT, castoramaCardNumber);
    }

    /**
     * Returns payeCarteAtout property.
     *
     * @return payeCarteAtout property.
     */
    public boolean isPayeCarteAtout() {
        if ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT) == null) {
            return false;
        } else {
            return ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT)).booleanValue();
        }
    }

    /**
     * Sets the value of the payeCarteAtout property.
     *
     * @param pPayeCarteAtout parameter to set.
     */
    public void setPayeCarteAtout(boolean pPayeCarteAtout) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_PAYE_CARTE_ATOUT, Boolean.valueOf(pPayeCarteAtout));
    }

    /**
     * Returns carteAtoutIndetermine property.
     *
     * @return carteAtoutIndetermine property.
     */
    public boolean isCarteAtoutIndetermine() {
        if ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE) == null) {
            return false;
        } else {
            return ((Boolean) getPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE))
                   .booleanValue();
        }
    }

    /**
     * Sets the value of the carteAtoutIndetermine property.
     *
     * @param pCarteAtoutIndetermine parameter to set.
     */
    public void setCarteAtoutIndetermine(boolean pCarteAtoutIndetermine) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_CARTE_ATOUT_INDETERMINE,
                         Boolean.valueOf(pCarteAtoutIndetermine));
    }

    /**
     * Returns origineMagasin property.
     *
     * @return origineMagasin property.
     */
    public RepositoryItem getOrigineMagasin() {
        RepositoryItem magasin;
        Object obj = getPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ORIGINE_MAGASIN);

        if (obj != null) {
            magasin = (RepositoryItem) obj;
        } else {
            magasin = null;
        }

        return magasin;
    }

    /**
     * Sets the value of the origineMagasin property.
     *
     * @param pMagasin parameter to set.
     */
    public void setOrigineMagasin(RepositoryItem pMagasin) {
        setPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ORIGINE_MAGASIN, pMagasin);
    }

    /**
     * Returns etageLivraison property.
     *
     * @return etageLivraison property.
     */
    public int getEtageLivraison() {
        Object prop = getPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ETAGE_LIVRAISON);

        if (prop != null) {
            return ((Integer) prop).intValue();
        }
        return 0;
    }

    /**
     * Sets the value of the etageLivraison property.
     *
     * @param pEtageLivraison parameter to set.
     */
    public void setEtageLivraison(int pEtageLivraison) {
        setPropertyValue(CastoConstantesDefense.ORDER_PROPERTY_ETAGE_LIVRAISON, new Integer(pEtageLivraison));
    }

    /**
     * Returns JSessionID property.
     *
     * @return JSessionID property.
     */
    public String getJSessionID() {
        Object prop = getPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID);

        if (prop != null) {
            return prop.toString();
        }
        return null;
    }

    /**
     * Sets the value of the JSessionID property.
     *
     * @param pJSessionID parameter to set.
     */
    public void setJSessionID(String pJSessionID) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID, pJSessionID);
    }

    /**
     * Returns idTransaction property.
     *
     * @return idTransaction property.
     */
    public int getIdTransaction() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX);

        if (prop != null) {
            return ((Integer) prop).intValue();
        }
        return 0;
    }

    /**
     * Sets the value of the idTransaction property.
     *
     * @param pIdTransaction parameter to set.
     */
    public void setIdTransaction(int pIdTransaction) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX,
                         new Integer(pIdTransaction));
    }

    /**
     * Returns numAutorisation property.
     *
     * @return numAutorisation property.
     */
    public String getNumAutorisation() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX);

        if (prop != null) {
            return prop.toString();
        }
        return null;
    }

    /**
     * Sets the value of the numAutorisation property.
     *
     * @param pNumAutorisation parameter to set.
     */
    public void setNumAutorisation(String pNumAutorisation) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX, pNumAutorisation);
    }

    /**
     * Returns numTransaction property.
     *
     * @return numTransaction property.
     */
    public int getNumTransaction() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX);

        if (prop != null) {
            return ((Integer) prop).intValue();
        }
        return 0;
    }

    /**
     * Sets the value of the numTransaction property.
     *
     * @param pNumTransaction parameter to set.
     */
    public void setNumTransaction(int pNumTransaction) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX,
                         new Integer(pNumTransaction));
    }

    /**
     * Returns dateTransaction property.
     *
     * @return dateTransaction property.
     */
    public Timestamp getDateTransaction() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX);

        if (prop != null) {
            return (Timestamp) prop;
        }
        return null;
    }

    /**
     * Sets the value of the dateTransaction property.
     *
     * @param pDateTransaction parameter to set.
     */
    public void setDateTransaction(Timestamp pDateTransaction) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX, pDateTransaction);
    }

    /**
     * Returns datePaiement property.
     *
     * @return datePaiement property.
     */
    public Timestamp getDatePaiement() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX);

        if (prop != null) {
            return (Timestamp) prop;
        }
        return null;
    }

    /**
     * Sets the value of the datePaiement property.
     *
     * @param pDatePaiement parameter to set.
     */
    public void setDatePaiement(Timestamp pDatePaiement) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX, pDatePaiement);
    }

    /**
     * Returns dateExpirationAtout property.
     *
     * @return dateExpirationAtout property.
     */
    public Timestamp getDateExpirationAtout() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT);

        if (prop != null) {
            return (Timestamp) prop;
        }
        return null;
    }

    /**
     * Sets the value of the dateExpirationAtout property.
     *
     * @param pDateExpirationAtout parameter to set.
     */
    public void setDateExpirationAtout(Timestamp pDateExpirationAtout) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT, pDateExpirationAtout);
    }

    /**
     * Returns montantTotalCommandeTTC property.
     *
     * @return montantTotalCommandeTTC property.
     */
    public double getMontantTotalCommandeTTC() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_TOTAL_COMMANDE_TTC);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantTotalCommandeTTC property.
     *
     * @param pMontantTotalCommandeTTC parameter to set.
     */
    public void setMontantTotalCommandeTTC(double pMontantTotalCommandeTTC) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_TOTAL_COMMANDE_TTC,
                         new Double(pMontantTotalCommandeTTC));
    }

    /**
     * Returns montantFraisLivraisonNonRemise property.
     *
     * @return montantFraisLivraisonNonRemise property.
     */
    public double getMontantFraisLivraisonNonRemise() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_LIVRAISON_NON_REMISE);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantFraisLivraisonNonRemise property.
     *
     * @param pMontantFraisLivraisonNonRemise parameter to set.
     */
    public void setMontantFraisLivraisonNonRemise(double pMontantFraisLivraisonNonRemise) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_LIVRAISON_NON_REMISE,
                         new Double(pMontantFraisLivraisonNonRemise));
    }

    /**
     * Returns montantFraisMonteeEtageNonRemise property.
     *
     * @return montantFraisMonteeEtageNonRemise property.
     */
    public double getMontantFraisMonteeEtageNonRemise() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_MONTEE_ETAGE_NON_REMISE);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantFraisMonteeEtageNonRemise property.
     *
     * @param pMontantFraisMonteeEtageNonRemise parameter to set.
     */
    public void setMontantFraisMonteeEtageNonRemise(double pMontantFraisMonteeEtageNonRemise) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_MONTEE_ETAGE_NON_REMISE,
                         new Double(pMontantFraisMonteeEtageNonRemise));
    }

    /**
     * Returns montantRemiseLivraisonEtMonteeEtage property.
     *
     * @return montantRemiseLivraisonEtMonteeEtage property.
     */
    public double getMontantRemiseLivraisonEtMonteeEtage() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_LIVRAISON_ET_MONTEE_ETAGE);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantRemiseLivraisonEtMonteeEtage property.
     *
     * @param pMontantFraisDePreparationNonRemise parameter to set.
     */
    public void setMontantRemiseLivraisonEtMonteeEtage(double pMontantFraisDePreparationNonRemise) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_LIVRAISON_ET_MONTEE_ETAGE,
                         new Double(pMontantFraisDePreparationNonRemise));
    }

    /**
     * Returns montantFraisDePreparationNonRemise property.
     *
     * @return montantFraisDePreparationNonRemise property.
     */
    public double getMontantFraisDePreparationNonRemise() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_NON_REMISE);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantFraisDePreparationNonRemise property.
     *
     * @param pMontantFraisDePreparationNonRemise parameter to set.
     */
    public void setMontantFraisDePreparationNonRemise(double pMontantFraisDePreparationNonRemise) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_NON_REMISE,
                         new Double(pMontantFraisDePreparationNonRemise));
    }

    /**
     * Returns montantFraisDePreparationRemise property.
     *
     * @return montantFraisDePreparationRemise property.
     */
    public double getMontantFraisDePreparationRemise() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_REMISE);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantFraisDePreparationRemise property.
     *
     * @param pMontantFraisDePreparationRemise parameter to set.
     */
    public void setMontantFraisDePreparationRemise(double pMontantFraisDePreparationRemise) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_FRAIS_DE_PREPARATION_REMISE,
                         new Double(pMontantFraisDePreparationRemise));
    }

    /**
     * Returns montantRemiseTotal property.
     *
     * @return montantRemiseTotal property.
     */
    public double getMontantRemiseTotal() {
        Object prop = getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_TOTAL);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantRemiseTotal property.
     *
     * @param pMontantRemiseTotal parameter to set.
     */
    public void setMontantRemiseTotal(double pMontantRemiseTotal) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_MONTANT_REMISE_TOTAL, new Double(pMontantRemiseTotal));
    }

    /**
     * Returns montantPFTTotal property.
     *
     * @return montantPFTTotal property.
     */
    public double getMontantPFTTotal() {
        Object prop = getPropertyValue(CastoConstantesCommande.MONTANT_PFT);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantPFTTotal property.
     *
     * @param pMontantPFTTotal parameter to set.
     */
    public void setMontantPFTTotal(double pMontantPFTTotal) {
        setPropertyValue(CastoConstantesCommande.MONTANT_PFT, new Double(pMontantPFTTotal));
    }

    /**
     * Returns montantPFLTotal property.
     *
     * @return montantPFLTotal property.
     */
    public double getMontantPFLTotal() {
        Object prop = getPropertyValue(CastoConstantesCommande.MONTANT_PFL);

        if (prop != null) {
            return ((Double) prop).doubleValue();
        }
        return 0;
    }

    /**
     * Sets the value of the montantPFLTotal property.
     *
     * @param pMontantPFLTotal parameter to set.
     */
    public void setMontantPFLTotal(double pMontantPFLTotal) {
        setPropertyValue(CastoConstantesCommande.MONTANT_PFL, new Double(pMontantPFLTotal));
    }

    /**
     * Returns sessionId property.
     *
     * @return sessionId property.
     */
    public String getSessionId() {
        String sessionId = (String) getPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID);
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     *
     * @param pSessionId parameter to set.
     */
    public void setSessionId(String pSessionId) {
        setPropertyValue(CastoConstantesCommande.PROPERTY_JSESSION_ID, pSessionId);
    }

    /**
     * Returns payeCarteAtout property.
     *
     * @return payeCarteAtout property.
     */
    public Boolean getPayeCarteAtout() {
        Boolean PayeCarteAtout = (Boolean) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYE_CARTEATOUT);
        return PayeCarteAtout;
    }

    /**
     * Sets the value of the payeCarteAtout property.
     *
     * @param pPayeCarteAtout parameter to set.
     */
    public void setPayeCarteAtout(Boolean pPayeCarteAtout) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYE_CARTEATOUT, pPayeCarteAtout);
    }

    /**
     * Returns identifiantTransaction property.
     *
     * @return identifiantTransaction property.
     */
    public Integer getIdentifiantTransaction() {
        Integer idTransaction =
            (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX);
        return idTransaction;
    }

    /**
     * Sets the value of the identifiantTransaction property.
     *
     * @param pIdTransaction parameter to set.
     */
    public void setIdentifiantTransaction(Integer pIdTransaction) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX, pIdTransaction);
    }

    /**
     * Returns numeroTransaction property.
     *
     * @return numeroTransaction property.
     */
    public Integer getNumeroTransaction() {
        Integer numAutorisation =
            (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX);
        return numAutorisation;
    }

    /**
     * Sets the value of the numeroTransaction property.
     *
     * @param pNumTransaction parameter to set.
     */
    public void setNumeroTransaction(Integer pNumTransaction) {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX, pNumTransaction);
    }

    /**
     * Returns BOState property.
     *
     * @return BOState property.
     */
    public String getBOState() {
        String BOState = (String) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE);
        return BOState;
    }

    /**
     * Sets the value of the BOState property.
     *
     * @param pBOState parameter to set.
     */
    public void setBOState(String pBOState) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE, pBOState);
    }

    /**
     * Returns BOStateDetail property.
     *
     * @return BOStateDetail property.
     */
    public String getBOStateDetail() {
        String BOStateDetail = (String) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_DETAIL);
        return BOStateDetail;
    }

    /**
     * Sets the value of the BOStateDetail property.
     *
     * @param pBOStateDetail parameter to set.
     */
    public void setBOStateDetail(String pBOStateDetail) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_DETAIL, pBOStateDetail);
    }

    /**
     * Returns BOStateNum property.
     *
     * @return BOStateNum property.
     */
    public Integer getBOStateNum() {
        Integer mBOState = (Integer) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM);
        return mBOState;
    }

    /**
     * Sets the value of the BOStateNum property.
     *
     * @param pBOStateNum parameter to set.
     */
    public void setBOStateNum(Integer pBOStateNum) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_BO_STATE_NUM, pBOStateNum);
    }

    /**
     * Returns profileId property.
     *
     * @return profileId property.
     */
    public String getProfileId() {
        String profileId = (String) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID);
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     *
     * @param pProfileId parameter to set.
     */
    public void setProfileId(String pProfileId) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROFILEID, pProfileId);
    }

    /**
     * Sets the value of the exportDate property.
     *
     * @param pDate parameter to set.
     */
    public void setExportDate(Timestamp pDate) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_EXPORTDATE, pDate);
    }

    /**
     * Returns version property.
     *
     * @return version property.
     */
    public int getVersion() {
        Object version = (Object) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_VERSION);
        if (version != null) {
            return ((Integer) version).intValue();
        }
        return 0;
    }

    /**
     * Returns cdeAdmin property.
     *
     * @return cdeAdmin property.
     */
    public boolean getCdeAdmin() {
        Object cdeAdmin = (Object) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_CDE_ADMIN);
        if (cdeAdmin != null) {
            return ((Boolean) cdeAdmin).booleanValue();
        }
        return false;
    }

    /**
     * Sets the value of the cdeAdmin property.
     *
     * @param pCdeAdmin parameter to set.
     */
    public void setCdeAdmin(boolean pCdeAdmin) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_CDE_ADMIN, Boolean.valueOf(pCdeAdmin));
    }

    /**
     * Returns adminLogin property.
     *
     * @return adminLogin property.
     */
    public String getAdminLogin() {
        Object adminLogin = (Object) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ADMIN_LOGIN);
        if (adminLogin != null) {
            return String.valueOf(adminLogin);
        }
        return null;
    }

    /**
     * Sets the value of the adminLogin property.
     *
     * @param pAdminLogin parameter to set.
     */
    public void setAdminLogin(String pAdminLogin) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ADMIN_LOGIN, pAdminLogin);
    }
    
    /**
     * Returns totalDiscount property.
     *
     * @return totalDiscount property.
     */
    public Double getTotalDiscount() {
    	return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_TOTAL_DISCOUNT);
    }

    /**
     * Sets the value of the totalDiscount property.
     *
     * @param pTotalDiscount parameter to set.
     */
    public void setTotalDiscount(Double pTotalDiscount) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_TOTAL_DISCOUNT, pTotalDiscount);
    }
    /**
     * Returns processingFees property.
     *
     * @return processingFees property.
     */
    public Double getProcessingFees() {
        return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROCESSING_FEES);
    }

    /**
     * Sets the value of the processingFees property.
     *
     * @param pProcessingFees parameter to set.
     */
    public void setProcessingFees(Double pProcessingFees) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROCESSING_FEES, pProcessingFees);
    }
    /**
     * Returns shippingFees property.
     *
     * @return shippingFees property.
     */
    public Double getShippingFees() {
        return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_FEES);
    }

    /**
     * Sets the value of the shippingFees property.
     *
     * @param pShippingFees parameter to set.
     */
    public void setShippingFees(Double pShippingFees) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_FEES, pShippingFees);
    }
    /**
     * Returns shippingDiscount property.
     *
     * @return shippingDiscount property.
     */
    public Double getShippingDiscount() {
        return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_DISCOUNT);
    }

    /**
     * Sets the value of the shippingDiscount property.
     *
     * @param pShippingDiscount parameter to set.
     */
    public void setShippingDiscount(Double pShippingDiscount) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_SHIPPING_DISCOUNT, pShippingDiscount);
    }
    
    /**
     * Returns totalWeight property.
     *
     * @return totalWeight property.
     */
    public Double getTotalWeight() {
        return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_TOTAL_WEIGHT);
    }

    /**
     * Sets the value of the totalWeight property.
     *
     * @param pTotalWeight parameter to set.
     */
    public void setTotalWeight(Double pTotalWeight) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_TOTAL_WEIGHT, pTotalWeight);
    }
    
    /**
     * Returns itemsDiscount property.
     *
     * @return itemsDiscount property.
     */
    public Double getItemsDiscount() {
        return (Double) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ITEMS_DISCOUNT);
    }

    /**
     * Sets the value of the itemsDiscount property.
     *
     * @param pItemsDiscount parameter to set.
     */
    public void setItemsDiscount(Double pItemsDiscount) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_ITEMS_DISCOUNT, pItemsDiscount);
    }

    /**
     * Returns paymentSource property.
     *
     * @return paymentSource property.
     */
    public String getPaymentSource() {
    	Object paymentSource = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_SOURCE); 
    	return (null == paymentSource) ? null : paymentSource.toString();
    }

    /**
     * Sets the value of the paymentSource property.
     *
     * @param pPaymentSource parameter to set.
     */
    public void setPaymentSource(String pPaymentSource) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_SOURCE, pPaymentSource);
    }


    /**
     * Returns paymentUserId property.
     *
     * @return paymentUserId property.
     */
    public String getPaymentUserId() {
    	Object paymentUserId = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_USER_ID); 
    	return (null == paymentUserId) ? null : paymentUserId.toString();
    }

    /**
     * Sets the value of the paymentUserId property.
     *
     * @param pPaymentUserId parameter to set.
     */
    public void setPaymentUserId(String pPaymentUserId) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PAYMENT_USER_ID, pPaymentUserId);
    }

    /**
     * Returns deliveryType property.
     *
     * @return deliveryType property.
     */
    public String getDeliveryType() {
        Object deliveryType = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_DELIVERY_TYPE); 
        return (null == deliveryType) ? null : deliveryType.toString();
    }

    /**
     * Sets the value of the deliveryType property.
     *
     * @param pDeliveryType parameter to set.
     */
    public void setDeliveryType(String pDeliveryType) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_DELIVERY_TYPE, pDeliveryType);
    }
    
    /**
     * Returns magasinId property.
     *
     * @return magasinId property.
     */
    public String getMagasinId() {
        return (String) getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_MAGASIN_ID);
    }
    
    /**
     * Sets the value of the magasinId property.
     *
     * @param pMagasinId parameter to set.
     */
    public void setMagasinId(String pMagasinId) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_MAGASIN_ID, pMagasinId);
    }

    /**
     * Returns processingFeeNiceWord property.
     *
     * @return processingFeeNiceWord property.
     */
    public String getProcessingFeeNiceWord() {
        Object processingFeeNiceWord = getPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROCESSING_FEE_NICE_WORD); 
        return (null == processingFeeNiceWord) ? null : processingFeeNiceWord.toString();
    }

    /**
     * Sets the value of the processingFeeNiceWord property.
     *
     * @param pProcessingFeeNiceWord parameter to set.
     */
    public void setProcessingFeeNiceWord(String pProcessingFeeNiceWord) {
        setPropertyValue(CastoConstantesOrders.ORDER_PROPERTY_PROCESSING_FEE_NICE_WORD, pProcessingFeeNiceWord);
    }

    /**
     * Returns payboxHandled property.
     *
     * @return payboxHandled property.
     */
    public Boolean getPayboxHandled() {
        Object payboxHandled = getPropertyValue(CastoConstantesOrders.PAYBOX_HANDLED); 
        return (Boolean) ((null == payboxHandled) ? null : payboxHandled);
    }
    
    /**
     * Sets the value of the payboxHandled property.
     *
     * @param pPayboxHandled parameter to set.
     */
    public void setPayboxHandled(Boolean pPayboxHandled) {
        setPropertyValue(CastoConstantesOrders.PAYBOX_HANDLED, pPayboxHandled);
    }
    
    public String toString(){
        StringBuilder sb = new StringBuilder(super.toString());
        try {
          sb.replace(sb.length()-1, sb.length(), "; deliveryType:");
          sb.append(getDeliveryType());
          sb.append("; magasinId:");
          sb.append(getMagasinId());
          sb.append("; processingFeeNiceWord:");
          sb.append(getProcessingFeeNiceWord());
          sb.append("; payboxHandled:");
          sb.append(getPayboxHandled());
        }
        catch (RemovedItemException exc) {
          sb.append("removed");
        }
        sb.append("]");

        return sb.toString();
    }

    public Set<String> getRemovedItemsIds() {
    	if(removedItemsIds == null){
    		removedItemsIds = new HashSet<String>();
    	}
		return removedItemsIds;
	}
}
