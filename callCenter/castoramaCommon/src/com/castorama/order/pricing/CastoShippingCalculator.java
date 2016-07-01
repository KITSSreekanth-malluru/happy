/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2000 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package com.castorama.order.pricing;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.repository.RepositoryItem;
import atg.commerce.pricing.*;
import atg.core.util.Address;
import java.util.*;

import atg.commerce.order.*;
import atg.beans.DynamicBeans;
import atg.repository.*;

// import castorama.Trace;
import com.castorama.config.Configuration;
import com.castorama.constantes.CastoConstantes;

/**
 * A shipping calculator that sets the shipping amount to a fixed price.
 * <P>
 * If the property <code>addAmount</code> is true then instead of setting the
 * price quote amount to the value of the <code>amount</code> property, the
 * calculator adds the amount to the current amount in the price quote. This can
 * be used to configure a "surcharge" calculator, which increases the shipping
 * price.
 * 
 * <P>
 * 
 * The <code>shippingMethod</code> property should be set to the name of a
 * particular delivery process. For example: UPS Ground, UPS 2-day or UPS Next
 * Day.
 * 
 * <P>
 * 
 * If the <code>ignoreShippingMethod</code> property is true, then this
 * calculator does not expose a shipping method name (through
 * getAvailableMethods). In addition this calculator will always attempt to
 * perform pricing. This option is available if the user is not given a choice
 * of different shipping methods.
 * 
 * @beaninfo description: A shipping calculator that sets the shipping amount to
 *           a fixed price. attribute: componentCategory Pricing Calculators
 * 
 * @author Bob Mason
 * @version $Id: CastoShippingCalculator.java,v 1.1 2006/06/30 17:31:45
 *          groupinfra\pereirag Exp $
 */

public class CastoShippingCalculator extends ShippingCalculatorImpl
{

    protected Repository m_Repository;

    /**
     * Constructs an instanceof CastoShippingCalculator
     */
    public CastoShippingCalculator()
    {
    }

    /**
     * R�cup�ration du Repository
     * 
     * @param none
     * @return Repository - Repository
     * @throws none
     */
    public Repository getRepository()
    {
        return m_Repository;
    }

    /**
     * Modification du Repository
     * 
     * @param Repository -
     *            Repository
     * @return none
     * @throws none
     */
    public void setRepository(Repository a_Repository)
    {
        m_Repository = a_Repository;
    }

    protected double getAmount(Order a_arg0, ShippingPriceInfo a_arg1, ShippingGroup a_arg2, RepositoryItem a_arg3,
            Locale a_arg4, RepositoryItem a_arg5, Map a_arg6) throws PricingException
    {
        return getAmount(a_arg1, a_arg2, a_arg3, a_arg4, a_arg5, a_arg6);
    }

    /**
     * Returns the amount which should be used as the price for this shipping
     * group
     * 
     * @param pPriceQuote
     *            the price of the input shipping group
     * @param pShippingGroup
     *            the shipping group for which an amount is needed
     * @param pPricingModel
     *            a discount which could affect the shipping group's price
     * @param pLocale
     *            the locale in which the price is calculated
     * @param pProfile
     *            the profile of the person for whom the amount in being
     *            generated.
     * @param pExtraParameters
     *            any extra parameters that might affect the amount calculation
     * @return the amount for pricing the input pShippingGroup
     * @exception PricingException
     *                if there is a problem getting the amount (price) for the
     *                input shipping group
     */
    protected double getAmount(ShippingPriceInfo a_PriceQuote, ShippingGroup a_ShippingGroup,
            RepositoryItem a_PricingModel, Locale a_Locale, RepositoryItem a_Profile, Map a_ExtraParameters)
            throws PricingException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.order.pricing.CastoShippingCalculator.getAmount()");
        }

        double l_dAmount = 0;
        double l_dVolumeTotalCommande = 0;
        double l_dTotalForf = 0;

        try
        {

            ShippingGroupImpl l_ShippingGroup = (ShippingGroupImpl) a_ShippingGroup;

            // l_Amount = getCalculDesFraisDePort(pShippingGroup);

            if (a_ShippingGroup != null)
            {

                Configuration.getConfiguration().getClientLockManager().acquireWriteLock(a_ShippingGroup.getId());

                List l_ListeDesArticles = l_ShippingGroup.getCommerceItemRelationships();
                double l_dPoidsTotal = 0;
                boolean l_bUnArticleHorsNormes = false;
                for (int j = 0; j < l_ListeDesArticles.size(); j++)
                {
                    CommerceItemRelationship l_CommerceItemRelationship = (CommerceItemRelationship) l_ListeDesArticles
                            .get(j);
                    CommerceItemImpl l_Article = (CommerceItemImpl) l_CommerceItemRelationship.getCommerceItem();

                    Configuration.getConfiguration().getClientLockManager().acquireWriteLock(l_Article.getId());

                    try
                    {
                        Object l_CatalogRef = l_Article.getAuxiliaryData().getCatalogRef();
                        Integer l_Poids = (Integer) DynamicBeans.getPropertyValue(l_CatalogRef, "PoidsUV");
                        Boolean l_HorsNormes = (Boolean) DynamicBeans.getPropertyValue(l_CatalogRef, "horsNormes");
                        Boolean l_ExonerationPFE = (Boolean) DynamicBeans.getPropertyValue(l_CatalogRef, "exonerationPFE");
                        Boolean l_ExonerationPFT = (Boolean) DynamicBeans.getPropertyValue(l_CatalogRef, "exonerationPFT");
                        
                        // Quick Win Lot 2 : Gestion de des frais forfaitaire
                        Double l_sommeForf = (Double) DynamicBeans.getPropertyValue(l_CatalogRef, "sommeForfaitaire"); 

                        int l_nQuantite = (int) l_Article.getQuantity();

                        Integer l_Hauteur = (Integer) DynamicBeans.getPropertyValue(l_CatalogRef, "hauteurUV");
                        Integer l_Largeur = (Integer) DynamicBeans.getPropertyValue(l_CatalogRef, "largeurUV");
                        Integer l_Profondeur = (Integer) DynamicBeans.getPropertyValue(l_CatalogRef, "profondeurUV");
                        double l_dHauteurEnMetre = 0.0;
                        double l_dLargeurEnMetre = 0.0;
                        double l_dProfondeurEnMetre = 0.0;
                        
                        
                        if (!l_ExonerationPFE.booleanValue())
                        {
                            if(l_sommeForf==null || l_sommeForf.equals(new Double("0")))
                            {
                                if (!l_ExonerationPFT.booleanValue())
                                {
                                    int l_nPoids = l_Poids.intValue();
                                    l_bUnArticleHorsNormes |= l_HorsNormes.booleanValue();

                                    l_dPoidsTotal += (double) l_nPoids / 1000 * l_nQuantite;

                                    if (l_Hauteur != null)
                                    {
                                        l_dHauteurEnMetre = l_Hauteur.doubleValue() / 1000;
                                    }
                                    if (l_Largeur != null)
                                    {
                                        l_dLargeurEnMetre = l_Largeur.doubleValue() / 1000;
                                    }
                                    if (l_Profondeur != null)
                                    {
                                        l_dProfondeurEnMetre = l_Profondeur.doubleValue() / 1000;
                                    }

                                    l_dVolumeTotalCommande += l_dHauteurEnMetre * l_dLargeurEnMetre * l_dProfondeurEnMetre
                                            * l_nQuantite;
                                }
                            }
                            else
                            {
                                l_dTotalForf +=  l_sommeForf.doubleValue() * l_nQuantite;
                            }
                        }
                    }
                    finally
                    {
                        try
                        {
                            Configuration.getConfiguration().getClientLockManager().releaseWriteLock(l_Article.getId());
                        }
                        catch (Exception e)
                        {
                            logError("getAmount() : " + e.toString());
                            /*
                             * Trace.logError(this, e, ".getAmount
                             * shippingGroupId=" + a_ShippingGroup.getId() + "
                             * commerceItemId=" + l_Article.getId() + " : " +
                             * e);
                             */
                        }
                    }
                }

                String l_strGrillePFEId = getGrillePfeId(a_ShippingGroup, l_dPoidsTotal, l_dVolumeTotalCommande);
                synchronized (l_ShippingGroup)
                {
                    l_ShippingGroup.setPropertyValue("poidsTotal", new Integer((int) (l_dPoidsTotal * 1000)));
                }

                l_dAmount = getPfeParPoids(l_dPoidsTotal, l_bUnArticleHorsNormes, l_strGrillePFEId);
                l_dAmount += l_dTotalForf;
                // Trace.logDebug(this, ".getAmount l_dPoidsTotal=" +
                // l_dPoidsTotal + " l_dAmount=" + l_dAmount);
            }

            // Trace.logDebug(this, ".getAmount amount=" + l_dAmount);

        }
        catch (Exception e)
        {
            logError("getAmount() : " + e.toString());
            // Trace.logError(this, e, ".getAmount shippingGroupId=" +
            // a_ShippingGroup.getId() + " : " + e.toString());
        }
        finally
        {
            try
            {
                Configuration.getConfiguration().getClientLockManager().releaseWriteLock(a_ShippingGroup.getId());
            }
            catch (Exception e)
            {
                logError("getAmount() : " + e.toString());
                // Trace.logError(this, e, ".getAmount shippingGroupId=" +
                // a_ShippingGroup.getId() + " : " + e);
            }
            // Trace.logClose(this, ".getAmount");
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.order.pricing.CastoShippingCalculator.getAmount()");
        }
        return l_dAmount;
    }

    /**
     * R�cup�ration de la pfe pour un poids donn�
     * 
     * @param double
     *            poids
     * @param boolean
     *            UnArticleHorsNormes
     * @return double
     * @throws Exception
     */
    public double getPfeParPoids(double a_dPoids, boolean a_bUnArticleHorsNormes, String a_strGrillePFEId)
            throws Exception
    {

        // Trace.logOpen(this, ".getPfeParPoids() a_strGrillePFEId=" +
        // a_strGrillePFEId);

        double l_dAmount = 0;

        try
        {
            RepositoryItemDescriptor l_Item = m_Repository.getItemDescriptor("poids");
            RepositoryView l_ItemView = l_Item.getRepositoryView();
            QueryBuilder l_Builder = l_ItemView.getQueryBuilder();
            Vector l_CriteresVector = new Vector(); // les criteres de
            // recherches

            QueryExpression l_QueryPropertie;
            QueryExpression l_QueryValue;
            Query l_Query;
            Double l_Poids = new Double(a_dPoids);

            // ****** Critere : poids inf
            l_QueryPropertie = l_Builder.createPropertyQueryExpression("poidsInf");
            l_QueryValue = l_Builder.createConstantQueryExpression(l_Poids);
            l_Query = l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue, QueryBuilder.LESS_THAN);
            l_CriteresVector.add(l_Query);

            // ****** Critere : poids sup
            l_QueryPropertie = l_Builder.createPropertyQueryExpression("poidsSup");
            l_QueryValue = l_Builder.createConstantQueryExpression(l_Poids);
            l_Query = l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue,
                    QueryBuilder.GREATER_THAN_OR_EQUALS);
            l_CriteresVector.add(l_Query);

            // ****** Critere : grille PFE Id
            l_QueryPropertie = l_Builder.createPropertyQueryExpression("grillePfeId");
            l_QueryValue = l_Builder.createConstantQueryExpression(a_strGrillePFEId);
            l_Query = l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue, QueryBuilder.EQUALS);
            l_CriteresVector.add(l_Query);

            Query[] l_CritereList = new Query[l_CriteresVector.size()];
            l_CriteresVector.copyInto(l_CritereList);
            Query l_AndQuery = l_Builder.createAndQuery(l_CritereList);

            RepositoryItem[] l_Result = l_ItemView.executeQuery(l_AndQuery);

            if (l_Result != null && l_Result.length > 0)
            {

                Double l_Amount = (Double) l_Result[0].getPropertyValue("prixAuKg");
                Double l_ForfaitTtc = (Double) l_Result[0].getPropertyValue("forfaitTTC");
                Double l_ForfaitHnTtc = (Double) l_Result[0].getPropertyValue("forfaitHnTTC");

                // Trace.logDebug(this, ".getPfeParPoids prixAuKg=" + l_Amount +
                // " forfaitTTC=" + l_ForfaitTtc
                // + " forfaitHnTTC=" + l_ForfaitHnTtc);

                l_dAmount = l_Amount.doubleValue() * a_dPoids + l_ForfaitTtc.doubleValue();
                if (a_bUnArticleHorsNormes)
                {
                    l_dAmount += l_ForfaitHnTtc.doubleValue();
                }
            }

        }
        catch (Exception e)
        {
            logError("getPfeParPoids() : " + e.toString());
            // Trace.logError(this, e, ".getPfeParPoids Exception : " +
            // e.toString());
        }
        finally
        {
            // Trace.logClose(this, ".getPfeParPoids()");
        }
        return l_dAmount;
    }

    protected String getGrillePfeId(ShippingGroup a_ShippingGroup, double a_dPoidTotal, double a_dVolumeTotal)
    {
        // Trace.logOpen(this, ".getGrillePfeId()");
        String l_strGrillePfeId = "1";
        try
        {
            Address l_ShippingAddress = ((HardgoodShippingGroup) a_ShippingGroup).getShippingAddress();
            if (l_ShippingAddress != null && l_ShippingAddress.getPostalCode() != null
                    && !l_ShippingAddress.getPostalCode().equals(""))
            {

                RepositoryItemDescriptor l_Item = m_Repository.getItemDescriptor("codespostaux_PFE");
                RepositoryView l_ItemView = l_Item.getRepositoryView();
                QueryBuilder l_Builder = l_ItemView.getQueryBuilder();

                QueryExpression l_QueryPropertie;
                QueryExpression l_QueryValue;
                Query l_Query;

                l_QueryPropertie = l_Builder.createPropertyQueryExpression("code_postal");
                l_QueryValue = l_Builder.createConstantQueryExpression(l_ShippingAddress.getPostalCode()
                        .substring(0, 2));
                l_Query = l_Builder.createComparisonQuery(l_QueryPropertie, l_QueryValue, QueryBuilder.EQUALS);

                RepositoryItem[] l_Result = l_ItemView.executeQuery(l_Query);
                if (l_Result != null && l_Result.length > 0)
                {
                    // Trace.logDebug("a_dPoidTotal=" + a_dPoidTotal);
                    // Trace.logDebug("((Double)(l_Result[0].getPropertyValue(poidMini))).doubleValue()="
                    // + ((Double)
                    // (l_Result[0].getPropertyValue("poidMini"))).doubleValue());
                    // Trace.logDebug("a_dVolumeTotal=" + a_dVolumeTotal);
                    // Trace.logDebug("((Double)(l_Result[0].getPropertyValue(volumeMini))).doubleValue()="
                    // + ((Double)
                    // (l_Result[0].getPropertyValue("volumeMini"))).doubleValue());
                    /*
                     * D�sactivation du test de volume minimum le 05/02/2004
                     */
                    if (a_dPoidTotal > ((Double) (l_Result[0].getPropertyValue("poidMini"))).doubleValue()
                    /*
                     * } || a_dVolumeTotal >
                     * ((Double)(l_Result[0].getPropertyValue("volumeMini"))).doubleValue()
                     */)
                    {
                        l_strGrillePfeId = (String) l_Result[0].getPropertyValue("grillePfeId");
                    }
                }
            }
        }
        catch (Exception e)
        {
            logError("getGrillePfeId() : " + e.toString());
            
            //Trace.logError(this, e, ".getGrillePfeId " + e.toString());
        }
        //Trace.logClose(this, ".getGrillePfeId()");
        return l_strGrillePfeId;
    }

    private double m_Niveau1;

    public void setNiveau1(double a_Niveau1)
    {
        m_Niveau1 = a_Niveau1;
    }

    public double getNiveau1()
    {
        return m_Niveau1;
    }

    private double m_Niveau2;

    public void setNiveau2(double a_Niveau2)
    {
        m_Niveau2 = a_Niveau2;
    }

    public double getNiveau2()
    {
        return m_Niveau2;
    }

    private double m_PrixFraisPortFixe;

    public void setPrixFraisPortFixe(double a_PrixFraisPortFixe)
    {
        m_PrixFraisPortFixe = a_PrixFraisPortFixe;
    }

    public double getPrixFraisPortFixe()
    {
        return m_PrixFraisPortFixe;
    }

    private double m_PrixFraisPortEnPlusPourNiveau1;

    public void setPrixFraisPortEnPlusPourNiveau1(double a_PrixFraisPortEnPlusPourNiveau1)
    {
        m_PrixFraisPortEnPlusPourNiveau1 = a_PrixFraisPortEnPlusPourNiveau1;
    }

    public double getPrixFraisPortEnPlusPourNiveau1()
    {
        return m_PrixFraisPortEnPlusPourNiveau1;
    }

    private double m_PrixFraisPortEnPlusPourNiveau2;

    public void setPrixFraisPortEnPlusPourNiveau2(double a_PrixFraisPortEnPlusPourNiveau2)
    {
        m_PrixFraisPortEnPlusPourNiveau2 = a_PrixFraisPortEnPlusPourNiveau2;
    }

    public double getPrixFraisPortEnPlusPourNiveau2()
    {
        return m_PrixFraisPortEnPlusPourNiveau2;
    }

    public double getPrixFraisPortTotalNiveau1()
    {
        return m_PrixFraisPortEnPlusPourNiveau1 + m_PrixFraisPortFixe;
    }

    public double getPrixFraisPortTotalNiveau2()
    {
        return m_PrixFraisPortEnPlusPourNiveau2 + m_PrixFraisPortFixe;
    }

} // end of class
