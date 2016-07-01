package com.castorama.category;

import java.util.Collection;
import java.util.Iterator;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**.
 * 
 * CastoCategoryManager permet de g�rer les r�gles m�tier
 *
 */
/* Code Review */public class CastoCategoryManager extends ApplicationLoggingImpl/* Code Review */
{
    /**. Constante statique **/
    public static final String DESTOCKAGE = "destockage";

	/* 890 DEBUT */
    /**. Constante statique **/
    public static final String BONNE_AFFAIRE = "bonnesAffaires";
    
    /**. Constante statique **/
    public static final String TYPE = "type";
	/* 890 FIN */

    private Repository m_productRepository;

    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_skuId     Id du sku en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public boolean isInCategorieDestockage (String a_skuId)
    {
        boolean l_retour = true;

        if (a_skuId != null)
        {
            try
            {
                RepositoryItem l_sku = getProductRepository().getItem(
                        a_skuId, "casto_sku");

                l_retour = isInCategorieDestockage(l_sku);
            }
            catch (RepositoryException l_exception)
            {
                if (isLoggingError())
                {
                    logError(l_exception);
                }
            }
        }

        return l_retour;
    }

    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_sku       Sku en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public static boolean isInCategorieDestockage (RepositoryItem a_sku)
    {
        boolean l_retour = false;

        if (a_sku != null)
        {
            // On recupere dans un premier temps les produits du sku 
            Collection l_produits = (Collection) a_sku.getPropertyValue("parentProducts");

            Iterator l_iteratorProducts = l_produits.iterator();
            while (l_iteratorProducts.hasNext())
            {
                RepositoryItem l_product = (RepositoryItem) l_iteratorProducts.next();
                l_retour = isInCategorieDestockageProduit(l_product);
            }
        }

        return l_retour;
    }

    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_produit   Produit en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public static boolean isInCategorieDestockageProduit (RepositoryItem a_produit)
    {
        boolean l_retour = false;
        Integer l_Mille = new Integer("1000");

        // si jamais on a trouve destockage = true, pas besoin de faire la suite 
        if (!l_retour)
        {
            // On recupere les categories parentes
            Collection l_parentCategories = (Collection) a_produit.
            getPropertyValue("parentCategories");

            if (l_parentCategories != null)
            {
                Iterator l_iterator = l_parentCategories.iterator();
                while (l_iterator.hasNext() && !l_retour)
                {
                    RepositoryItem l_categorie = (RepositoryItem) l_iterator.next();

                    // Pour chaque categorie parente, on regarde sa 
                    // valeur du champ destockage
                    if (l_categorie != null)
                    {
                    	/* MODIF 890 */
                        if (l_categorie.getPropertyValue(TYPE) != null &&
                                l_Mille.compareTo((Integer)l_categorie.getPropertyValue(TYPE)) == 0 &&
                                l_categorie.getPropertyValue(DESTOCKAGE) != null)
                        {
                            Boolean l_destockage = (Boolean)
                            l_categorie.getPropertyValue(DESTOCKAGE);

                            l_retour = l_destockage.booleanValue();
                        }
                    }
                    
                    if (!l_retour)
                    {
                        l_retour = isInCategorieDestockageProduit(l_categorie);
                    }
                }
            }
        }

        return l_retour;
    }

	/* 890 DEBUT */
    /**.
     * 
     * Methode permettant de savoir si un produit est bonne affaire ou non...
     * @param       a_skuId     Id du sku en question
     * @return      boolean     Boolean permettant de savoir s'il est bonne affaire
     */
    public boolean isInBonneAffaire (String a_skuId)
    {
        boolean l_retour = true;

        if (a_skuId != null)
        {
            try
            {
                RepositoryItem l_sku = getProductRepository().getItem(
                        a_skuId, "casto_sku");

                l_retour = isInBonneAffaire(l_sku);
            }
            catch (RepositoryException l_exception)
            {
                if (isLoggingError())
                {
                    logError(l_exception);
                }
            }
        }

        return l_retour;
    }

    /**.
     * 
     * Methode permettant de savoir si un produit est bonne affaire ou non...
     * @param       a_sku       Sku en question
     * @return      boolean     Boolean permettant de savoir s'il est bonne affaire
     */
    public static boolean isInBonneAffaire (RepositoryItem a_sku)
    {
        boolean l_retour = false;

        if (a_sku != null)
        {
            // On recupere dans un premier temps les produits du sku 
            Collection l_produits = (Collection) a_sku.getPropertyValue("parentProducts");

            Iterator l_iteratorProducts = l_produits.iterator();
            while (l_iteratorProducts.hasNext())
            {
                RepositoryItem l_product = (RepositoryItem) l_iteratorProducts.next();
                l_retour = isInBonneAffaireProduit(l_product);
            }
        }

        return l_retour;
    }

    /**.
     * 
     * Methode permettant de savoir si un produit est destocke ou non...
     * @param       a_produit   Produit en question
     * @return      boolean     Boolean permettant de savoir s'il est destocke
     */
    public static boolean isInBonneAffaireProduit (RepositoryItem a_produit)
    {
        boolean l_retour = false;
        Integer l_Mille = new Integer("1000");

        // si jamais on a trouve destockage = true, pas besoin de faire la suite 
        if (!l_retour)
        {
            // On recupere les categories parentes
            Collection l_parentCategories = (Collection) a_produit.
            getPropertyValue("parentCategories");

            if (l_parentCategories != null)
            {
                Iterator l_iterator = l_parentCategories.iterator();
                while (l_iterator.hasNext() && !l_retour)
                {
                    RepositoryItem l_categorie = (RepositoryItem) l_iterator.next();

                    // Pour chaque categorie parente, on regarde sa 
                    // valeur du champ destockage
                    if (l_categorie != null)
                    {
                        if (l_categorie.getPropertyValue(TYPE) != null &&
                                l_Mille.compareTo((Integer)l_categorie.getPropertyValue(TYPE)) == 0 &&
                                l_categorie.getPropertyValue(BONNE_AFFAIRE) != null)
                        {
                            Boolean l_bonneAffaire = (Boolean)
                            l_categorie.getPropertyValue(BONNE_AFFAIRE);

                            l_retour = l_bonneAffaire.booleanValue();
                        }
                    }
                    
                    if (!l_retour)
                    {
                        l_retour = isInBonneAffaireProduit (l_categorie);
                    }
                }
            }
        }

        return l_retour;
    }
	/* 890 FIN */

    /**
     * @return the productRepository
     */
    public Repository getProductRepository()
    {
        return m_productRepository;
    }

    /**
     * @param a_productRepository the productRepository to set
     */
    public void setProductRepository(Repository a_productRepository)
    {
        m_productRepository = a_productRepository;
    }


}
