package com.castorama.commande;

import com.castorama.constantes.CastoConstantesCommande;

import atg.commerce.order.RepositoryContactInfo;

/**
 * @author Florte J�r�my (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Extension de RepositoryContactInfo pour la gestion des propri�t�s
 * suppl�mentaires.
 */
public class CastoRepositoryContactInfo extends RepositoryContactInfo
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7334940951983706323L;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Renvoit la civilit� de l'adresse.
     * 
     * @return String La civilit� de l'adresse.
     */
    public String getCivilite()
    {
        return (String) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_CIVILITE);
    }

    /**
     * Fixe la civilit� de l'adresse.
     * 
     * @param a_civilite
     *            La nouvelle civilit� de l'adresse.
     */
    public void setCivilite(String a_civilite)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_CIVILITE, a_civilite);
    }

    /**
     * Renvoit le n� de t�l�phone professionnel de l'adresse.
     * 
     * @return String Le n� de t�l�phone professionnel de l'adresse.
     */
    public String getTelephonePro()
    {
        return (String) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_TEL_PRO);
    }

    /**
     * Fixe le n� de t�l�phone professionnel de l'adresse.
     * 
     * @param a_telephonePro
     *            Le nouveau n� de t�l�phone professionnel de l'adresse.
     */
    public void setTelephonePro(String a_telephonePro)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_TEL_PRO, a_telephonePro);
    }

    /**
     * Renvoit le n� de t�l�phone portable de l'adresse.
     * 
     * @return String Le n� de t�l�phone portable de l'adresse.
     */
    public String getTelephonePortable()
    {
        return (String) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_TEL_PORTABLE);
    }

    /**
     * Fixe le n� de t�l�phone portable de l'adresse.
     * 
     * @param a_telephonePortable
     *            Le nouveau n� de t�l�phone portable de l'adresse.
     */
    public void setTelephonePortable(String a_telephonePortable)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_TEL_PORTABLE, a_telephonePortable);
    }

    /**
     * Renvoit la TVA intracommunautaire de l'adresse.
     * 
     * @return String La TVA intracommunautaire de l'adresse.
     */
    public String getTVAIntracommunautaire()
    {
        return (String) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_TVA_INTRACOMMUNAUTAIRE);
    }

    /**
     * Fixe la TVA intracommunautaire de l'adresse.
     * 
     * @param a_tvaIntracommunautaire
     *            La nouvelle TVA intracommunautaire de l'adresse.
     */
    public void setTVAIntracommunautaire(String a_tvaIntracommunautaire)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_TVA_INTRACOMMUNAUTAIRE,
                a_tvaIntracommunautaire);
    }

    /**
     * Méthode qui renvoie la valeur de la propriété "alerteAutomatique".
     * 
     * @return Une valeur booléenne.
     */
    public boolean getAlerteAutomatique()
    {
        return ((Boolean) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_ALERTE_AUTOMATIQUE))
                .booleanValue();
    }

    /**
     * Méthode qui fixe la valeur de la propriété "alerteAutomatique".
     * 
     * @param a_alerteAutomatique
     *            Une valeur booléenne.
     */
    public void setAlerteAutomatique(boolean a_alerteAutomatique)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_ALERTE_AUTOMATIQUE, Boolean
                .valueOf(a_alerteAutomatique));
    }

    /**
     * Méthode qui renvoie la valeur de la propriété "moyenContact".
     * 
     * @return "0" | "1" | "2" | "3".
     */
    public String getMoyenContact()
    {
        return (String) mRepositoryItem.getPropertyValue(CastoConstantesCommande.PROPERTY_MOYEN_CONTACT);
    }

    /**
     * Méthode qui fixe la valeur de la propriété "moyenContact".
     * 
     * @param a_moyenContact
     *            "0" | "1" | "2" | "3".
     */
    public void setMoyenContact(String a_moyenContact)
    {
        mRepositoryItem.setPropertyValue(CastoConstantesCommande.PROPERTY_MOYEN_CONTACT, a_moyenContact);
    }
}
