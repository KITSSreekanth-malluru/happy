package com.castorama.order;

import java.sql.Timestamp;

import com.castorama.constantes.CastoConstantesCommande;

import atg.commerce.order.CreditCard;

/**
 * .
 * CastoPaymentGroupImpl
 */
public class CastoPaymentGroupImpl extends CreditCard
{
    /**
     * .
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * .
     * @param a_libelleBanque a_libelleBanque
     */
    public void setLibelleBanque(String a_libelleBanque) 
    {
        setPropertyValue("libelleBanque", a_libelleBanque);
    }
    
    /**
     * .
     * @return String
     */
    public String getLibelleBanque() 
    {
        return (String) getPropertyValue("libelleBanque");
    }
    
    /**
     * .
     * @param a_numCheque a_numCheque
     */
    public void setNumCheque(String a_numCheque) 
    {
        setPropertyValue("numcheque", a_numCheque);
    }
    
    /**
     * .
     * @return String
     */
    public String getNumCheque()
    {
        return (String) getPropertyValue("numcheque");
    }
    
    /**
     * .
     * @param a_montantChequeFrancs a_montantChequeFrancs
     */
    public void setMontantChequeFrancs(String a_montantChequeFrancs) 
    {
        setPropertyValue("montantChequeFrancs", a_montantChequeFrancs);
    }
    
    /**
     * .
     * @return String
     */
    public String getMontantChequeFrancs() 
    {
        return (String) getPropertyValue("montantChequeFrancs");
    }
    
    /**
     * .
     * @param a_montantChequeEuros a_montantChequeEuros
     */
    public void setMontantChequeEuros(String a_montantChequeEuros) 
    {
        setPropertyValue("montantChequeEuros", a_montantChequeEuros);
    }
    
    /**
     * .
     * @return String
     */
    public String getMontantChequeEuros() 
    {
        return (String) getPropertyValue("montantChequeEuros");
    }
        
    /**
     * .
     * @param a_dateValidAtout a_dateValidAtout
     */
    public void setDateValidAtout(Timestamp a_dateValidAtout) 
    {
        setPropertyValue("dateValidAtout", a_dateValidAtout);
    }
    
    /**
     * .
     * @return Timestamp
     */
    public Timestamp getDateValidAtout()
    {
        return (Timestamp) getPropertyValue("dateValidAtout");
    }
    
    /**
     * .
     * @param a_paymentMethod a_paymentMethod
     */
    public void setPaymentMethod(String a_paymentMethod) 
    {
        setPropertyValue("paymentMethod", a_paymentMethod);
    }
    
    /**
     * .
     * @return String
     */
    public String getPaymentMethod()
    {
        return (String) getPropertyValue("paymentMethod");
    }
    
    /**
     * .
     * @param a_NumAuto a_NumAuto
     */
    public void setNumeroAutorisationPaybox(String a_NumAuto) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX, a_NumAuto);
    }
    
    /**
     * .
     * @return String
     */
    public String getNumeroAutorisationPaybox()
    {
        return (String) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_AUTORISATION_PAYBOX);
    }
    
    /**
     * .
     * @param a_IdTransactionPaybox a_IdTransactionPaybox
     */
    public void setIdTransactionPaybox(Integer a_IdTransactionPaybox) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX, a_IdTransactionPaybox);
    }
    
    /**
     * .
     * @return String
     */
    public Integer getIdTransactionPaybox()
    {
        return (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_IDENTIFIANT_TRANSACTION_PAYBOX);
    }
    
    /**
     * .
     * @param a_NumTransaction a_NumTransaction
     */
    public void setNumTransactionPaybox(Integer a_NumTransaction) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX, a_NumTransaction);
    }
    
    /**
     * .
     * @return String
     */
    public Integer getNumTransactionPaybox()
    {
        return (Integer) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_NUMERO_TRANSACTION_PAYBOX);
    }
    
    /**
     * .
     * @param a_DateTransaction a_DateTransaction
     */
    public void setDateTransactionPaybox(Timestamp a_DateTransaction) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX, a_DateTransaction);
    }
    
    /**
     * .
     * @return Timestamp
     */
    public Timestamp getDateTransactionPaybox()
    {
        return (Timestamp) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_TRANSACTION_PAYBOX);
    }
    
    /**
     * .
     * @param a_DatePaiement a_DatePaiement
     */
    public void setDatePaiementPaybox(Timestamp a_DatePaiement) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX, a_DatePaiement);
    }
    
    /**
     * .
     * @return Timestamp
     */
    public Timestamp getDatePaiementPaybox()
    {
        return (Timestamp) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_PAIEMENT_PAYBOX);
    }
    
    /**
     * .
     * @param a_DateExpiration a_DateExpiration
     */
    public void setDateExpirationPaybox(Timestamp a_DateExpiration) 
    {
        setPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT, a_DateExpiration);
    }
    
    /**
     * .
     * @return Timestamp
     */
    public Timestamp getDateExpirationPaybox()
    {
        return (Timestamp) getPropertyValue(CastoConstantesCommande.ORDER_PROPERTY_DATE_EXPIRATION_ATOUT);
    }
    
    /**
     * .
     * @param a_Amount a_Amount
     */
    public void setAmount(Double a_Amount) 
    {
        setPropertyValue("amount", a_Amount);
    }
    
    /**
     * .
     * @param a_Amount a_Amount
     */
    public void setAmountAuthorized(Double a_Amount) 
    {
        setPropertyValue("amountAuthorized", a_Amount);
    }
    
}
