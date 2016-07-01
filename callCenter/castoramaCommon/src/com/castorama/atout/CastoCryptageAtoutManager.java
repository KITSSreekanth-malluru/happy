package com.castorama.atout;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import atg.commerce.catalog.CatalogTools;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.commande.CastoOrderTools;
import com.castorama.order.CastoPaymentGroupImpl;

/**
 * Classe permettant d'avertir de la disponibilite d'un sku a un utilisateur
 * par mail.
 */
/*Code Review*/public class CastoCryptageAtoutManager extends ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Constante.
     */
    public static final String ID = "id";

    /**
     * Constante statique.
     */
    public static final int PLUS_VENDU = 1005;

    /**
     * Constante.
     */
    public static final String DISPO_SKU_TEXTE = "dispo.sku.texte";

    /**
     * Constante.
     */
    public static final String FICHIER_TEMPLATE = "com.castorama.ficheproduit.FicheProduitMailAmiResources";

    /**
     * Constante.
     */
    public static final String DISPO_SKU_TITRE = "dispo.sku.titre";

    /**.
     * Taille d'un num�ro de carte l'Atout non crypt�
     */
    public static final int TAILLE_NON_CRYPTE = 19;

    protected static final ComponentName BEANCHIFFREMENT_COMPONENTNAME = ComponentName
    .getComponentName("/castorama/atout/BeanChiffrement");

    protected Map m_disponibiliteSkus;

    private CatalogTools m_catalogTools;

    private Repository m_Repository;


    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**.
     * R�cup�ration du Repository
     * @param none
     * @return Repository Repository
     * @throws none
     */
    public Repository getRepository()
    {
        return m_Repository;
    }

    /**.
     * Modification du Repository
     * @param        a_Repository       CatalogueRepository
     * @throws       none
     */
    public void setRepository(Repository a_Repository)
    {
        m_Repository = a_Repository;
    }

    /**
     * Renvoie une r�f�rence vers l'utilitaire du catalogue.
     * 
     * @return CatalogTools une r�f�rence vers l'utilitaire du catalogue.
     */
    public CatalogTools getCatalogTools()
    {
        return m_catalogTools;
    }

    /**
     * Fixe la r�f�rence vers l'utilitaire du catalogue.
     * 
     * @param a_catalogTools
     *            La nouvelle r�f�rence vers l'utilitaire du catalogue.
     */
    public void setCatalogTools(CatalogTools a_catalogTools)
    {
        m_catalogTools = a_catalogTools;
    }

    /**.
     * 
     * Crypte l'ensemble des commandes
     */
    public void cryptageAtout ()
    {
        if (isLoggingDebug())
        {
            logDebug("D�but fonction cryptageAtout");
        }


        MutableRepository l_oMutRepos = (MutableRepository) getRepository();

        try
        {
            // On construit la requete qui vient rechercher les skus
            RqlStatement l_oRequeteRQL;
            RepositoryView l_oView = l_oMutRepos.getView("order");
            RepositoryItem [] l_aList;
            Object[] l_aRqlparams = null;

            // Cas r�el
            /*Code Review*/l_oRequeteRQL = RqlStatement.parseRqlStatement("NOT numCarteAtout_avtCodeReview IS NULL");/*Code Review*/

            //pour les tests
            //l_oRequeteRQL = RqlStatement.parseRqlStatement("id = ?0");
            //l_aRqlparams = new Object[1];
            //l_aRqlparams[0] = "C49500008";

            // On execute la requete
            l_aList = 
                l_oRequeteRQL.executeQueryUncached (l_oView, l_aRqlparams);

            // On parcourt la liste des r�sultats de la requete (parcourt de la liste des SKU_CASTO)
            if (l_aList != null && l_aList.length > 0)
            {
                for (int l_nI = 0 ; l_nI < l_aList.length ; l_nI++)
                {
                    cryptageCommande((MutableRepositoryItem) l_aList[l_nI]);
                }
            }

        }
        catch (Exception l_oException)
        {
            if (isLoggingError())
            {
                logError(l_oException);
            }
        }
        finally
        {
            if (isLoggingDebug())
            {
                logDebug("Fin fonction cryptageAtout");
            }
        }
    }

    /**.
     * 
     * Crypte la commande donn�e en param�tre
     * @param   a_oCommande     COmmande � traiter
     */
    private void cryptageCommande (MutableRepositoryItem a_oCommande)
    {
        if (isLoggingDebug())
        {
            logDebug("D�but fonction cryptageCommande " + a_oCommande.getRepositoryId());
        }

        BeanChiffrement l_BeanChiffrement = (BeanChiffrement) Nucleus.getGlobalNucleus().resolveName(
                BEANCHIFFREMENT_COMPONENTNAME);

        if (l_BeanChiffrement != null)
        {
            /*Code Review*/String l_sNumeroCarte = (String) a_oCommande.getPropertyValue("numCarteAtout_avtCodeReview");/*Code Review*/

            // Si la longueur = 19, alors num�ro non crypt�
            if (l_sNumeroCarte != null && !"".equals(l_sNumeroCarte) && l_sNumeroCarte.length() == TAILLE_NON_CRYPTE)
            {
                String l_sCryptage = l_BeanChiffrement.encode(l_sNumeroCarte);

                /*Code Review*/a_oCommande.setPropertyValue("numCarteAtout_avtCodeReview", l_sCryptage);/*Code Review*/

                // On regarde aussi le moyen de paiement
                Collection l_aPaymentGroups = (Collection) a_oCommande.getPropertyValue("paymentGroups");

                if (l_aPaymentGroups != null)
                {
                    Iterator l_iterator = l_aPaymentGroups.iterator();

                    while (l_iterator.hasNext())
                    {
                        /*Code Review*/CastoPaymentGroupImpl l_paymentGroup = (CastoPaymentGroupImpl) l_iterator.next();/*Code Review*/
                        String l_CreditCardType = (String) l_paymentGroup.getPropertyValue("creditCardType");
                        String l_CreditCardNumber = (String) l_paymentGroup.getPropertyValue("creditCardNumber");

                        if ("Atout".equals(l_CreditCardType) &&
                                l_CreditCardNumber != null && !"".equals(l_CreditCardNumber) && l_CreditCardNumber.length() == TAILLE_NON_CRYPTE)
                        {
                            /* Code Review */ l_paymentGroup.setCreditCardNumber(CastoOrderTools.NUMERO_DEFAUT);/* Code Review */ 
                        }
                    }
                }
            }
        }

        if (isLoggingDebug())
        {
            logDebug("Fin fonction cryptageCommande");
        }
    }

}