package com.castorama.order;

import java.util.Comparator;
import java.util.Date;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryItem;

import com.castorama.constantes.CastoConstantes;

/**
 * .
 * Logica
 * 18/09/2008
 * Fiche Mantis 1256 - Droplet qui liste les commandes d'un user par submitted date
 */
public class OrderComparator extends ApplicationLoggingImpl implements Comparator 
{
    /*
     * ------------------------------------------------------------------------
     * Contantes
     * ------------------------------------------------------------------------
     */
    private static final String SUBMITTED_DATE = "submittedDate";
    
    /*
     * ------------------------------------------------------------------------
     * Méthode
     * ------------------------------------------------------------------------
     */
    /*
     * .
     * Logica
     * 18/09/2008
     * Fiche Mantis 1256 - Droplet qui liste les commandes d'un user par submitted date
     */
    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_order1
     *            objet 1 à comparer.
     * @param a_order2
     *            objet 2 à comparer.
     * @return valeur
     */
    public int compare(Object a_order1, Object a_order2)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.order.OrderComparator.compare()");
        }
        
        RepositoryItem l_order1 = (RepositoryItem) a_order1;
        RepositoryItem l_order2 = (RepositoryItem) a_order2;
        int l_return=CastoConstantes.NEG;
        
        if(null!=a_order1)
        {
            if(null!=a_order2)
            {
                Date l_subDateOrder1 = (Date)l_order1.getPropertyValue(SUBMITTED_DATE);
                Date l_subDateOrder2 = (Date)l_order2.getPropertyValue(SUBMITTED_DATE);
                if(null!=l_subDateOrder1)
                {
                    if(null!=l_subDateOrder2)
                    {
                        l_return = l_subDateOrder2.compareTo(l_subDateOrder1);
                    }
                    else
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("l_subDateOrder2==null");
                        }
                        l_return = CastoConstantes.NEG;
                    }
                }
                else
                {
                    if(null!=l_subDateOrder2)
                    {
                        l_return = CastoConstantes.UN;
                    }
                    else
                    {
                        if (isLoggingDebug())
                        {
                            logDebug("l_subDateOrder1==null && l_subDateOrder2==null");
                        }
                        l_return = CastoConstantes.ZERO;
                    }
                }
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("a_order2==null");
                }
                l_return = CastoConstantes.NEG;
            }
        }
        else
        {
            if(null!=a_order2)
            {
                if (isLoggingDebug())
                {
                    logDebug("a_order1==null");
                }
                l_return = CastoConstantes.UN;
            }
            else
            {
                if (isLoggingDebug())
                {
                    logDebug("a_order1==null && a_order2==null");
                }
                l_return = CastoConstantes.ZERO;
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.order.OrderComparator.compare()");
        }
        
        return l_return;
    }

}
