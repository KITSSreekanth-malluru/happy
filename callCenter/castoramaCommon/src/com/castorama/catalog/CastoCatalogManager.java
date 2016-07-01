package com.castorama.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.transaction.TransactionManager;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.castorama.constantes.CastoConstantes;

/**
 * @author Florte Jérémy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe d'utilitaires pour la gestion des éléments du catalogue ATG.
 */
/* Code Review */public class CastoCatalogManager extends ApplicationLoggingImpl/* Code Review */
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Représente la propriété crossSelling d'un sku.
     */
    public static final String PROPERTY_CROSS_SELLING = "crossSelling";

    /**
     * Représente la propriété relatedSku d'un sku.
     */
    public static final String PROPERTY_RELATED_SKU = "relatedSku";

    /**
     * Représente la proprété relatingSku d'un sku.
     */
    public static final String PROPERTY_RELATING_SKU = "relatingSku";

    /**
     * Représente la propriété bundleLinks d'un sku.
     */
    public static final String PROPERTY_BUNDLE_LINKS = "bundleLinks";

    /**
     * Représente la propriété "childSkus" de l'item descripteur "product".
     */
    public static final String PROPERTY_CHILD_SKUS = "childSkus";

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Le CatalogRepository.
     */
    private Repository m_catalogRepository;
    
    /** SCENARII
     * Les cat�gories � traiter lors de l'initialisation de themeSku des skus.
     */
    private String[] m_categoriesATraiter;
    
    /** SCENARII
     * Les valeurs � setter dans themeSku.
     */
    private String[] m_valeursASetter;

    /**
     * Le TransactionManager � employer pour les acc�s au repository.
     */
    private TransactionManager m_transactionManager;

    /**
     * nb de produits traites lors du lancement du RMI marqueCommerciale.
     */
    private int m_nbPdtsMax;
    
    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    
    /**
     * renvoie la valeur du properties.
     * @return int le nomvbre de produits max
     */
    public int getNbPdtsMax()
    {
        return m_nbPdtsMax;
    }

    /**
     * 
     * TODO commentaire Javadoc.
     * @param a_nbPdtsMax int
     */
    public void setNbPdtsMax(int a_nbPdtsMax)
    {
        m_nbPdtsMax = a_nbPdtsMax;
    }

    /**
     * Renvoit une r�f�rence vers le CatalogRepository.
     * 
     * @return Repository Une r�f�rence vers le catalogRepository.
     */
    public Repository getCatalogRepository()
    {
        return m_catalogRepository;
    }

    /**
     * Fixe la r�f�rence vers le CatalogRepository.
     * 
     * @param a_catalogRepository
     *            Une nouvelle r�f�rence vers le catalogRepository.
     */
    public void setCatalogRepository(Repository a_catalogRepository)
    {
        m_catalogRepository = a_catalogRepository;
    }

    /**
     * Renvoit le {@link TransactionManager} � employer.
     * 
     * @return TransactionManager Le {@link TransactionManager} � employer.
     */
    public TransactionManager getTransactionManager()
    {
        return m_transactionManager;
    }

    /**
     * Fixe le {@link TransactionManager} � employer.
     * 
     * @param a_transactionManager
     *            Le {@link TransactionManager} � fixer.
     */
    public void setTransactionManager(TransactionManager a_transactionManager)
    {
        m_transactionManager = a_transactionManager;
    }

    /*
     * ------------------------------------------------------------------------ [
     * M�thodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * M�thode qui renvoit la liste des skus associ�s � un autre en se
     * basant sur la propri�t� demand�e.
     * 
     * @param a_item
     *            RepositoryItem du sku demand�.
     * @param a_property
     *            Propri�t� souhait�e ()
     * 
     * @return List Liste des identifiants des skus associ�s au sku a_item.
     */
    public List obtenirSkusAssocies(RepositoryItem a_item, String a_property)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirSkusAssocies().");
        }

        List l_ret = new ArrayList();
        Collection l_skusAssocies = null;

        try
        {
            l_skusAssocies = (List) a_item.getPropertyValue(a_property);
        }
        catch (ClassCastException l_classCastException)
        {
            try
            {
                l_skusAssocies = (Set) a_item.getPropertyValue(a_property);
            }
            catch (ClassCastException l_classCastExceptionBis)
            {
                logError(l_classCastExceptionBis.toString());
            }
        }

        if (null != l_skusAssocies)
        {
            l_ret.addAll(l_skusAssocies);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirSkusAssocies().");
        }

        return l_ret;
    }

    /**
     * M�thode pour obtenir la liste des skus pour lesquels la propri�t�
     * "bundleLinks" comprend l'item a_item.
     * 
     * @param a_item
     *            L'item.
     * 
     * @return List La liste des skus pour lesquels la propri�t�
     *         "bundleLinks" comprend l'item a_item.
     * 
     * @throws RepositoryException
     *             Si une exception survient lors de l'acc�s au repository.
     */
    public List obtenirBundleLinkedSkus(RepositoryItem a_item) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirBundleLinkedSkus().");
        }

        Repository l_repository = getCatalogRepository();
        List l_bundledLinkedSkus = new ArrayList();

        try
        {
            RqlStatement l_findSkus = RqlStatement.parseRqlStatement("BUNDLELINKS includes item (ITEM.ID = ?0)");
            RepositoryView l_view = l_repository.getView(CastoConstantes.DESCRIPTEUR_CASTO_SKU);
            RepositoryItem[] l_items = l_findSkus.executeQuery(l_view, new Object[]
            { a_item.getRepositoryId() });

            if (null != l_items)
            {
                int l_size = l_items.length;

                for (int l_i = 0; l_i < l_size; l_i++)
                {
                    l_bundledLinkedSkus.add(l_items[l_i]);
                }
            }
        }
        catch (RepositoryException l_exception)
        {
            logError(l_exception.toString());

            throw l_exception;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirBundleLinkedSkus().");
        }
        return l_bundledLinkedSkus;
    }

    /**
     * M�thode qui filtre une liste de skus en ne prenant en compte que les
     * skus li�s � un produit mono sku.
     * 
     * @param a_listeSkus
     *            La liste des skus � filtrer.
     * 
     * @return List La liste filtr�e des skus.
     * 
     * @throws RepositoryException
     *             Si une excpetion survient lors de l'acc�s au Repository.
     */
    public List obtenirProduitsMonoSku(List a_listeSkus) throws RepositoryException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirProduitsMonoSku().");
        }

        Repository l_repository = getCatalogRepository();
        List l_produitsMonoSkus = new ArrayList();
        StringBuffer l_ids = new StringBuffer();

        try
        {
            /*
             * Construction de la liste des identifiants des skus associ�s
             */
            int l_size = a_listeSkus.size();

            for (int l_i = 0; l_i < l_size; l_i++)
            {
                if (0 != l_i)
                {
                    l_ids.append(", ");
                }

                l_ids.append("\"").append(((RepositoryItem) a_listeSkus.get(l_i)).getRepositoryId()).append("\"");
            }

            /*
             * Recherche des produits monoskus (la propriété childskus ne doit
             * contenir qu'un seul item) dont la propriété CHILDSKUS comprend
             * un sku de la liste des des skus associés.
             */
            RqlStatement l_findProducts = RqlStatement.parseRqlStatement("CHILDSKUS includes item (ID in {"
                    + l_ids.toString() + "}) and count(CHILDSKUS) = 1");

            RepositoryView l_view = l_repository.getView(CastoConstantes.DESCRIPTEUR_CASTO_PRODUCT);
            RepositoryItem[] l_items = l_findProducts.executeQuery(l_view, null);

            if (null != l_items)
            {
                l_size = l_items.length;

                for (int l_i = 0; l_i < l_size; l_i++)
                {
                    List l_childSkus = (List) l_items[l_i].getPropertyValue("childskus");
                    RepositoryItem l_childSku = (RepositoryItem) l_childSkus.get(0);

                    if (!l_produitsMonoSkus.contains(l_childSku))
                    {
                        if (isLoggingInfo())
                        {
                            logInfo("|--> Article issu d'un produit mono sku : " + l_childSku.getRepositoryId()
                                    + " produit : " + l_items[l_i].getRepositoryId());
                        }

                        l_produitsMonoSkus.add(l_childSku);
                    }
                }
            }
        }
        catch (RepositoryException l_exception)
        {
            logError(l_exception.toString());

            throw l_exception;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.catalog.CastoCatalogManager.obtenirProduitsMonoSku().");
        }

        return l_produitsMonoSkus;
    }

    /**
     * M�thode qui renvoit l'identifiant du produit associ� au sku pass�
     * en param�tre.
     * 
     * @param a_skuId
     *            L'identifiant du sku.
     * 
     * @return String L'identifiant du produit associ� au sku pass� en
     *         paramètre ou null si aucun produit parent ou qu'une exception
     *         est survenue.
     */
    public String getIdProduit(String a_skuId)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.catalog.CastoCatalogManager.getIdProduit(String).");
        }

        String l_id;

        try
        {
            Repository l_catalogRepository = getCatalogRepository();

            RepositoryItem l_item = l_catalogRepository.getItem(a_skuId, CastoConstantes.DESCRIPTEUR_CASTO_SKU);

            if (null != l_item)
            {
                l_id = getIdProduit(l_item);
            }
            else
            {
                l_id = null; // Aucun produit parent pour l'article demandé.
            }
        }
        catch (RepositoryException l_repositoryException)
        {
            logError(l_repositoryException.toString());

            l_id = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.catalog.CastoCatalogManager.getIdProduit(String).");
        }

        return l_id;
    }

    /**
     * Méthode qui renvoit l'identifiant du produit associé au sku passé en
     * paramètre.
     * 
     * @param a_sku
     *            Le sku dont on souhaite connaître le produit père.
     * 
     * @return String L'identifiant du produit associé au sku passé en
     *         paramètre ou null si l'article n'a aucun produit parent.
     */
    public String getIdProduit(RepositoryItem a_sku)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.catalog.CastoCatalogManager.getIdProduit(RepositoryItem).");
        }

        String l_id;
        RepositoryItem l_product;

        if (null != a_sku)
        {
            /*
             * On récupère la propriété "parentProducts".
             */
            Set l_parents = (Set) a_sku.getPropertyValue("parentProducts");

            if (null != l_parents)
            {
                if (isLoggingInfo())
                {
                    logInfo("|--> parents : " + l_parents);
                }

                /*
                 * On sélectionne le premier parent.
                 */
                if (!l_parents.isEmpty())
                {
                    l_product = (RepositoryItem) l_parents.iterator().next();

                    l_id = l_product.getRepositoryId();
                }
                else
                {
                    l_id = null;
                }
            }
            else
            {
                l_id = null;
            }
        }
        else
        {
            l_id = null;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.catalog.CastoCatalogManager.getIdProduit(RepositoryItem).");
        }

        return l_id;
    }

    /**
     * M�thode qui v�rifie si un sku est disponible pour un magasin donn�,
     * dans la quantit� demand�e.
     * 
     * @param a_skuId
     *            Identifiant du sku.
     * @param a_magasinRef
     *            Identifiant du magasin.
     * @param a_seuil
     *            Seuil limite.
     * 
     * @return boolean True si l'article est disponible
     */
    public boolean verifierDispoStock(String a_skuId, String a_magasinRef, double a_seuil)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.catalog.CastoCatalogManager.verifierDispoStock().");
        }

        boolean l_ret;

        try
        {
            Repository l_catalogRepository = getCatalogRepository();

            RqlStatement l_findSku = RqlStatement.parseRqlStatement("sku.id = ?0 AND magasin.id = ?1");

            RepositoryView l_view = l_catalogRepository.getView("dispoMagasin");
            RepositoryItem[] l_items = l_findSku.executeQuery(l_view, new String[]
            { a_skuId, a_magasinRef, });

            if (null != l_items && 0 != l_items.length)
            {
                if (isLoggingInfo())
                {
                    logInfo("|--> verifierDispoStock : existe");
                }

                RepositoryItem l_dispo = l_items[0];

                double l_stock = ((Double) l_dispo.getPropertyValue("stock")).doubleValue();

                l_ret = l_stock > a_seuil;
            }
            else
            {
                if (isLoggingDebug())
                {
                    logInfo("|--> verifierDispoStock : existe pas");
                }

                l_ret = false; // Article introuvable pour le magasin
            }
        }
        catch (RepositoryException l_repositoryException)
        {
            logError(l_repositoryException.toString());

            l_ret = false; // Une erreur est survenue
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.catalog.CastoCatalogManager.verifierDispoStock().");
        }
        return l_ret;
    }
    
    /**
     * SCENARII : renseignement en masse de l'attribut "themeSku" des Skus d'une categorie root donn�e.
     * 
     */
    public void initializeThemeSku ()
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==      INITIALISATION DE LA VALEUR THEMESKU DES SKU        ==");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : d�but de la m�thode initializeThemeSku.");
        }
        
        try
        {
            // r�cup�ration des cat�gories � traiter et des valeurs � setter dans themeSku
            // et bouclage dessus
            String[] l_catATraiter = getCategoriesATraiter();
            String[] l_valeursASetter = getValeursASetter();
            for (int l_cat = 0 ; l_cat < l_catATraiter.length ; l_cat++)
            {
                String l_catATraiterEnCours =  l_catATraiter[l_cat];
                String l_valeursASetterEnCours =  l_valeursASetter[l_cat];
                
                // v�rification de l'existence de la categorie ROOT dont l'id est pass� en param�tre.
                // Si oui, on traite.
                RepositoryItem l_categorieAVerifier = getCatalogRepository().getItem(l_catATraiterEnCours,"casto_category");
                if (l_categorieAVerifier != null && l_categorieAVerifier.getPropertyValue("root") != null &&
                        "true".equals(l_categorieAVerifier.getPropertyValue("root").toString()))
                {
                    // R�cup�ration des categories feuilles contenant les skus � traiter 
                    RepositoryItemDescriptor l_itemDescriptor = getCatalogRepository().getItemDescriptor("category");
                    RepositoryView l_repositoryView = l_itemDescriptor.getRepositoryView();
                    RqlStatement l_statement = RqlStatement.parseRqlStatement("castoAncestorCategories INCLUDES ITEM ( id = \""+
                            l_catATraiterEnCours+"\") AND fixedChildCategories IS NULL");
                    Object[] l_categoriesFeuilles = l_statement.executeQuery(l_repositoryView, new Object[]{});
                    
                    // si on a des categories on traite
                    if (null != l_categoriesFeuilles && l_categoriesFeuilles.length != 0) 
                    {
                        // parcours des cat�gories
                        for (int l_i = 0 ; l_i < l_categoriesFeuilles.length ; l_i ++)
                        {
                            // r�cup�ration des produits de la cat�gorie
                            RepositoryItem l_categoryEnCours = (RepositoryItem)l_categoriesFeuilles[l_i];
                            List l_products = (List)l_categoryEnCours.getPropertyValue("fixedChildProducts");
                            
                            // si il y a des produits on traite
                            if (null != l_products && l_products.size() != 0)
                            {
                                // parcours des produits
                                for (int l_j = 0 ; l_j < l_products.size() ; l_j ++)
                                {
                                    // r�cup�ration des skus
                                    RepositoryItem l_produtctEnCours = (RepositoryItem)l_products.get(l_j);
                                    List l_skus = (List)l_produtctEnCours.getPropertyValue("childSKUs");
                                    
                                    // si il y a des skus : on traite
                                    if (null != l_skus && l_skus.size() != 0)
                                    {   
                                        for (int l_k = 0 ; l_k < l_skus.size() ; l_k++)
                                        {
                                            MutableRepository l_mutRepo = (MutableRepository)getCatalogRepository();
                                            MutableRepositoryItem l_skuEnCours = (MutableRepositoryItem)l_skus.get(l_k);
                                            if (isLoggingInfo())
                                            {
                                                logInfo(this.getClass()+" : mise a jour du sku "+
                                                        l_skuEnCours.getRepositoryId() + " avec la valeur " + l_valeursASetterEnCours);
                                            }
                                            l_skuEnCours.setPropertyValue("themeSku", l_valeursASetterEnCours);                                            
                                            l_mutRepo.updateItem(l_skuEnCours);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        logWarning(this.getClass()+" : -WARNING 2- Pas de categorie feuille contanant des skus. Aucune valeur � setter");
                    }
                      
                    
                }
                else
                {
                    String l_messageError = (l_categorieAVerifier == null) ? "La categorie dont l'id est "+
                            l_catATraiterEnCours+" n'existe pas" : "La categorie dont l'id est "+l_catATraiterEnCours+" n'eest pas une categorie root.";
                    logWarning(this.getClass()+" : -WARNING 1- "+l_messageError);
                }
            }

        }
        catch (Exception l_e)
        {
            logError(this.getClass()+" : -ERREUR 1- "+l_e);
        }
        
        
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==    FIN INITIALISATION DE LA VALEUR THEMESKU DES SKU      ==");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : fin de la m�thode initializeThemeSku.");
        }
    }

    
    
    /**
     * SCENARII : renseignement en masse de la marque commerciale des sku.
     * 
     */
    public void initializeMarqueCommerciale (String a_phase)
    {
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==  INITIALISATION DE LA VALEUR MARQUE COMMERCIALE DES SKU  ==");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : début de la méthode initializeMarqueCommerciale.");
        }
        
        int l_borneInf=0;
        Integer l_nbPdtsMax = new Integer(getNbPdtsMax()); 
        String  l_token;
        /*
         * On découpe en tokens les arguments de la ligne de commandes pour en extraire la borne d'extration 
         * inférieure et le nombre de skus à extraire à partir de cette borne
         */
        if(null != a_phase && !"".equals(a_phase))
        {
            StringTokenizer l_tokenizer = new StringTokenizer(a_phase, "!"); 
            if(null!=l_tokenizer && l_tokenizer.hasMoreTokens())
            {
                l_token = l_tokenizer.nextToken(); 
            }
            if(null!=l_tokenizer && l_tokenizer.hasMoreTokens())
            {
                l_token = l_tokenizer.nextToken(); 
                l_borneInf = Integer.parseInt(l_token);
            }
        }
        if (isLoggingInfo())
        {
            logInfo("borneInf= " + l_borneInf);
            logInfo("nbProduitsPris= " + l_nbPdtsMax);
        }
        
        try
        {
            MutableRepository l_mutRep = (MutableRepository)getCatalogRepository();
    
            // Récupération des Produits 
            RepositoryItemDescriptor l_itemDescriptor = getCatalogRepository().getItemDescriptor("casto_product");
            RepositoryView l_repositoryView = l_itemDescriptor.getRepositoryView();
            RqlStatement l_statement = RqlStatement.parseRqlStatement("ALL order by product_id RANGE ?0+?1 ");
            Object[] l_products = l_statement.executeQuery(l_repositoryView, new Object[]{new Integer(l_borneInf), l_nbPdtsMax});
            
            for (int l_i = 0 ; l_i < l_products.length ; l_i++)
            {
                RepositoryItem l_productEnCours = (RepositoryItem)l_products[l_i];
                String l_marqueCommerciale = (l_productEnCours.getPropertyValue("MarqueCommerciale") !=null) ?
                        l_productEnCours.getPropertyValue("MarqueCommerciale").toString() : null ;
                                        
                if (l_productEnCours.getPropertyValue("childSKUs") != null)
                {
                    List l_skus = (List)l_productEnCours.getPropertyValue("childSKUs");
                    if (l_skus != null && l_skus.size()!=0)
                    {
                        for (int l_j = 0 ; l_j < l_skus.size(); l_j++)
                        {
                            MutableRepositoryItem l_mutSku = (MutableRepositoryItem)l_skus.get(l_j);
                            l_mutSku.setPropertyValue("MarqueCommerciale", l_marqueCommerciale);
                            if (isLoggingInfo())
                            {
                                logInfo(this.getClass()+" : mise à jour du sku "+l_mutSku.getRepositoryId());
                            }
                            l_mutRep.updateItem(l_mutSku);
                        }
                    }
                }
            }
        }
        catch (Exception l_e)
        {
            logError(this.getClass()+" : -ERREUR 1- "+l_e);
        }
        
        
        if (isLoggingDebug())
        {
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==    FIN INITIALISATION DE MARQUE COMMERCIALE              ==");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : ==============================================================");
            logDebug(this.getClass()+" : fin de la méthode initializeMarqueCommerciale.");
        }
    }
    
    
    
    
    /**SCENARII.
     * @return the categoriesATraiter
     */
    public String[] getCategoriesATraiter()
    {
        return m_categoriesATraiter;
    }

    /**SCENARII.
     * @param a_categoriesATraiter the categoriesATraiter to set
     */
    public void setCategoriesATraiter(String[] a_categoriesATraiter)
    {
        m_categoriesATraiter = a_categoriesATraiter;
    }

    /**SCENARII.
     * @return the valeursASetter
     */
    public String[] getValeursASetter()
    {
        return m_valeursASetter;
    }

    /**SCENARII.
     * @param a_valeursASetter the valeursASetter to set
     */
    public void setValeursASetter(String[] a_valeursASetter)
    {
        m_valeursASetter = a_valeursASetter;
    }
    
    
}