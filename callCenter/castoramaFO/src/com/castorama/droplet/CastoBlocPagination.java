package com.castorama.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.castorama.constantes.CastoConstantes;

/**
 * @author Yusif Geoffrey
 */

public class CastoBlocPagination extends DynamoServlet
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */
    /**
     * Constante statique.
     * 
     * Valeur 5.
     */
    private static final int NB_LIENS_AUTOUR_PAGE_EN_COURS = 5;

    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    private String m_sTypeUrl;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Retourne le type d'URL � afficher.
     * 
     * @return m_sTypeUrl Le type d'URL � afficher.
     */

    public String getTypeUrl()
    {
        return m_sTypeUrl;
    }

    /**
     * Fixe le type d'URL � afficher.
     * 
     * @param a_sTypeUrl
     *            Le type d'URL � afficher.
     */

    public void setTypeUrl(String a_sTypeUrl)
    {
        m_sTypeUrl = a_sTypeUrl;
    }

    /*
     * ------------------------------------------------------------------------ [
     * M�thodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
     *      atg.servlet.DynamoHttpServletResponse).
     * 
     * @param a_request
     *            Requ�te HTTP.
     * @param a_response
     *            R�ponse HTTP.
     * 
     * @throws ServletException
     *             Si une erreur survient.
     * @throws IOException
     *             Si une erreur survient.
     */
    public void service(DynamoHttpServletRequest a_request, DynamoHttpServletResponse a_response)
            throws ServletException, IOException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.CastoBlocPagination.service()");
        }

        /*
         * Récupération des param�tres de la droplet.
         */

        int l_nNombreTotalDeProduits = 0;
        String paramTotalProduct = a_request.getParameter("nombreTotalProduit");
        if (paramTotalProduct != null && paramTotalProduct.length() > 0) {
        	l_nNombreTotalDeProduits = Integer.parseInt(paramTotalProduct);
        }
        // AP int l_nNombreTotalDeProduits = new Integer(a_request.getParameter("nombreTotalProduit")).intValue();
        String l_sPageEnCours = a_request.getParameter("pageEnCours");
        
        Object l_nbProdMax = a_request.getParameter("nbProdMax");
        int l_nNbProdMax = CastoConstantes.DOUZE;
        if(null!=l_nbProdMax)
        {
            l_nNbProdMax = new Integer(a_request.getParameter("nbProdMax")).intValue();
        }

        // REFERENCEMENT: la page en cours est traitée différement maintenant
        // (POST), on test le null sait-on jamais!
        int l_pageEnCours = 1;
        if (l_sPageEnCours != null)
        {
            l_pageEnCours = new Integer(l_sPageEnCours).intValue();
        }
        // !REFERENCEMENT: la page en cours est traité différement maintenant,
        // on test le null !

        /*
         * si plus de 12 produits (donc plus d'une page), on traite la
         * pagination sinon on n'affiche pas de pagination
         */
        if (l_nNombreTotalDeProduits > l_nNbProdMax)
        {
            /*
             * calcul de nombre de pages totales
             */
            int l_nombreDePages = calculNombreDePages(l_nNombreTotalDeProduits,l_nNbProdMax);

            /*
             * On determine si il faut griser ou pas les liens precedent et
             * suivant : Si la page en cours est 1, on grise 'pr�c�dent' Si la
             * page en cours est <derni�re page>, on grise 'suivant'
             */
            boolean l_lienPrecedentGrise = 1 == l_pageEnCours;
            boolean l_lienSuivantGrise = l_pageEnCours == l_nombreDePages;

            // Determination du premier et dernier lien � afficher
            int l_premierLienAAfficher = affichageExtremites("premier", l_pageEnCours, l_nombreDePages);
            int l_dernierLienAAfficher = affichageExtremites("dernier", l_pageEnCours, l_nombreDePages);

            /*
             * G�n�ration du flux HTML
             */
            String l_fluxHtml = genererPagination(l_lienPrecedentGrise, l_lienSuivantGrise, l_pageEnCours, l_premierLienAAfficher, l_dernierLienAAfficher);

            /*
             * Renvoi du flux HTML g�n�r� au naviguateur
             */
            a_request.setParameter("fluxPagination", l_fluxHtml);
            a_request.serviceParameter(CastoConstantes.OPEN_PARAMETER_OUTPUT, a_request, a_response);
        }
        else
        {
            a_request.serviceParameter(CastoConstantes.OPEN_PARAMETER_EMPTY, a_request, a_response);
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.CastoBlocPagination.service()");
        }

    }

    /**
     * Cette m�thode d�termine quel lien de page afficher aux extr�mit�s de la
     * LISTE DES PAGES. en fonction de la page en cours
     * 
     * @param a_sExtremite
     *            l'extr�mit� � traiter
     * @param a_nPageEnCours
     *            la page en cours
     * @param a_nombreDePages
     *            Le nombre total de pages
     * 
     * @return int le lien � afficher
     */
    private int affichageExtremites(String a_sExtremite, int a_nPageEnCours, int a_nombreDePages)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE
                    + "com.castorama.droplet.CastoBlocPagination.affichageExtremites().");
        }

        int l_ret = 0;

        /*
         * Si la page en cours est � moins de cinq pages de la page 1, le
         * premier lien � afficher est 1 sinon on calcul le lien � afficher
         */
        if (a_sExtremite != null && a_sExtremite.equals("premier"))
        {
            if (a_nPageEnCours - NB_LIENS_AUTOUR_PAGE_EN_COURS <= 0)
            {
                l_ret = 1;
            }
            else
            {
                l_ret = a_nPageEnCours - NB_LIENS_AUTOUR_PAGE_EN_COURS;
            }
        }
        else
        {
            if (a_nPageEnCours + NB_LIENS_AUTOUR_PAGE_EN_COURS >= a_nombreDePages)
            {
                l_ret = a_nombreDePages;
            }
            else
            {
                l_ret = a_nPageEnCours + NB_LIENS_AUTOUR_PAGE_EN_COURS;
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE
                    + "com.castorama.droplet.CastoBlocPagination.affichageExtremites().");
        }

        return l_ret;
    }

    /**
     * Méthode qui calcule le nombre de pages à du bloc pagination.
     * 
     * @param a_nNombreTotalDeProduits
     *            le nombre total de produits concernés par la page liste
     * @param a_nNombreTotalDeCdesParPage
     *            a_nNombreTotalDeCdesParPage
     * @return int le nombre de pages du bloc pagination.
     */
    private int calculNombreDePages(int a_nNombreTotalDeProduits, int a_nNbProdMax)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.CastoBlocPagination.calculNombreDePages()");
        }

        /*
         * Le nombre de page qui sera retourn� par la m�thode
         */
        int l_ret = 0;

        /*
         * -------------------------------------------------------------------------
         * Calcul du nombre de pages :
         * -------------------------------------------------------------------------
         * 
         * 1 - Calcul du nombre de pages remplies entièrement : 12 produits par
         * page <nb_page_remplies> = <nb_total_de_produits> /
         * <nb_max_de_produit_par_page>
         * 
         * 2 - V�rification si on doit ajouter une page en plus qui ne sera pas
         * remplie. Si <nb_total_de_produits> - (<nb_page_remplies> *
         * <nb_max_de_produit_par_page>) = 0 le nombre de de pages � renvoyer
         * est <nb_page_remplies> sinon le nombre de de pages � renvoyer est
         * <nb_page_remplies> + 1
         * -------------------------------------------------------------------------
         */

        /*
         * Etape 1
         */
        int l_nPagesRemplies = a_nNombreTotalDeProduits / a_nNbProdMax;

        /*
         * Etape 2
         */
        int l_nNbProduitsRestantsAAfficher = a_nNombreTotalDeProduits - (l_nPagesRemplies * a_nNbProdMax);

        if (l_nNbProduitsRestantsAAfficher == 0)
        {
            l_ret = l_nPagesRemplies;
        }
        else
        {
            l_ret = l_nPagesRemplies + 1;
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.CastoBlocPagination.calculNombreDePages()");
        }

        return l_ret;
    }

    /**
     * Cette m�thode g�n�re le bloc de code HTML qui sera interpr�t� par le
     * navigateur.
     * 
     * @param a_lienPrecedentGrise
     *            Indique si lien pr�c�dent est gris� ou non.
     * @param a_lienSuivantGrise
     *            Indique si le lien suivant est gris� ou non.
     * @param a_pageEnCours
     *            Le num�ro de la page en cours.
     * @param a_adressePage
     *            L'adesse courante de la page.
     * @param a_provenance
     *            La provenance.
     * @param a_parentCategoryId
     *            Identifiant de la cat�gorie parente.
     * @param a_categoryEnCours
     *            La cat�gorie en cours.
     * @param a_profile
     *            Le profil utilisateur.
     * @param a_tri
     *            Les crit�res de tri.
     * @param a_filtreTg
     *            Les crit�res de filtre.
     * @param a_premierLienAAfficher
     *            Premier num�ro de page � afficher.
     * @param a_dernierLienAAfficher
     *            Dernier num�ro de page � afficher.
     * 
     * @return String le codez HTML du bloc pagination.
     */
    private String genererPagination(boolean a_lienPrecedentGrise, boolean a_lienSuivantGrise, int a_pageEnCours,
            int a_premierLienAAfficher, int a_dernierLienAAfficher)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.CastoBlocPagination.genererPagination()");
        }

        // R��criture d'url : url de type dev ou prod r�cup�r� ici.
        boolean l_urlTypedev = CastoConstantes.REECRITURE_DEV.equals(getTypeUrl());

        // buffer pour le flux HTML g�n�r�
        StringBuffer l_fluxHTML = new StringBuffer();

        /*
         * D�but du flux HTML
         */
        l_fluxHTML.append("<ul class=\"block_float\">");

        if (a_lienPrecedentGrise)
        {
            l_fluxHTML.append("<li><span class=\"pager_precedent_passif\">page precedente</span></li>");
        }
        else
        {
            /*
             * D�finition du numero de page precedente : <page_en_cours> - 1
             * pour le lien 'page precedente' et g�n�ration du html du lien en
             * fonction du type d'URL dev ou prod
             */
            int l_numeroPagePrecedente = a_pageEnCours - 1;

            if (l_urlTypedev)
            {
                l_fluxHTML.append(getUrlDev(l_numeroPagePrecedente, "pager_precedent_actif", "page precedente"));
            }
            else
            {
                l_fluxHTML.append(getUrlProd(l_numeroPagePrecedente, "pager_precedent_actif", "page precedente"));
            }
        }

        /*
         * Simple ajout du caract�re de s�paration des liens "|"
         */
        l_fluxHTML.append("<li><span>|</span></li>");

        /*
         * listage des liens sur les pages
         */
        for (int l_i = a_premierLienAAfficher; l_i <= a_dernierLienAAfficher; l_i++)
        {
            /*
             * si le lien en cours de traitement est le lien de la page
             * courante, on affiche simplement le numéro de la page sans activer
             * de lien. sinon on affiche le lien proprement dit sur la page
             * correspondante
             */
            if (l_i == a_pageEnCours)
            {
                l_fluxHTML.append("<li class=\"pager_lien_page\"><span>").append(l_i).append("</span></li>");
            }
            else
            {
                // REFERENCEMENT: on passe la page en changeant la page dans le
                // formulaire de pagination à l'aide de JS

                l_fluxHTML.append("<li class=\"pager_lien_page\"><a href=\"javascript: navPaginationForm('" + l_i + "');\"  onclick=\"sc_classement('page " + l_i + "');\">" + l_i + "</a></li>");

                // !REFERENCEMENT: on passe la page en changeant la page dans le
                // formulaire de pagination à l'aide de JS
            }
        }

        /*
         * Simple ajout du caract�re de s�paration des liens "|"
         */
        l_fluxHTML.append("<li><span>|</span></li>");

        /*
         * Grisage ou pas du lien 'page suivante' Si il n'y a pas de page
         * suivante ( une seule page à afficher dans la page liste ou page en
         * cours = dernière page grisage sinon on génère le html du lien sur la
         * page suivante : <page_en_cours>+1
         */
        if (a_lienSuivantGrise)
        {
            l_fluxHTML.append("<li><span class=\"pager_suivant_passif\">page suivante</span></li>");
        }
        else
        {
            /*
             * definition du numero de page suivante
             */
            int l_numeroPageSuivante = a_pageEnCours + 1;

            if (l_urlTypedev)
            {
                l_fluxHTML.append(getUrlDev(l_numeroPageSuivante, "pager_suivant_actif", "page suivante"));
            }
            else
            {
                l_fluxHTML.append(getUrlProd(l_numeroPageSuivante, "pager_suivant_actif", "page suivante"));
            }
        }

        /*
         * fin du flux HTML
         */
        l_fluxHTML.append("</ul>");

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.CastoBlocPagination.genererPagination()");
        }

        return l_fluxHTML.toString();
    }

    /**
     * M�thode qui renvoie l'action utilisateur sans le '.htm'.
     * 
     * @param a_profile
     *            Profile utilisateur.
     * 
     * @return String L'action utilisateur sans le '.htm'.
     */
    /*
     * private String getActionUtilisateur(Profile a_profile) { if
     * (isLoggingDebug()) { logDebug(CastoConstantes.METHODE_ENTREE +
     * "com.castorama.droplet.CastoBlocPagination.getActionUtilisateur()"); }
     * 
     * String l_actionUtilisateur = (String)
     * a_profile.getPropertyValue("actionUtilisateur");
     * 
     * int l_indexS = l_actionUtilisateur.lastIndexOf(TIRET_S); int l_lastSlash =
     * l_actionUtilisateur.lastIndexOf("/");
     * 
     * if (CODE_ERREUR != l_indexS) { if (l_indexS < l_lastSlash) {
     * l_actionUtilisateur = l_actionUtilisateur.substring(0,
     * l_actionUtilisateur.indexOf(POINT_HTM)); } else { l_actionUtilisateur =
     * l_actionUtilisateur.substring(0, l_indexS); } } else { if
     * (l_actionUtilisateur.indexOf(POINT_HTM) != -1) { l_actionUtilisateur =
     * l_actionUtilisateur.substring(0, l_actionUtilisateur.indexOf(POINT_HTM)); } }
     * 
     * if (isLoggingDebug()) { logDebug(CastoConstantes.METHODE_SORTIE +
     * "com.castorama.droplet.CastoBlocPagination.getActionUtilisateur()"); }
     * 
     * return l_actionUtilisateur; }
     */

    /**
     * Méthode qui permet de récupérer une url de pagination en prod.
     * 
     * @param a_numeroPage
     *            Numéro de page.
     * @param a_class
     *            Classe css.
     * @param a_texte
     *            Texte à inscrire.
     * 
     * @return String Une url de pagination en prod.
     */
    private String getUrlProd(int a_numeroPage, String a_class, String a_texte)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.CastoBlocPagination.getUrlProd()");
        }

        // REFERENCEMENT: on passe la page en changeant la page dans le
        // formulaire de pagination à l'aide de JS
        String l_fluxHTML = "<li class=\"pager_lien_page\"><a class=\"pager_precedent_suivant " + a_class + "\" href=\"javascript: navPaginationForm('" + a_numeroPage + "');\"  onclick=\"sc_classement('" + a_texte + "');\">" + a_texte + "</a></li>";

        // !REFERENCEMENT: on passe la page en changeant la page dans le
        // formulaire de pagination à l'aide de JS

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.CastoBlocPagination.getUrlProd()");
        }

        return l_fluxHTML;
    }

    /**
     * Méthode qui permet de récupérer une url de pagination en prod.
     * 
     * @param a_actionUtilisateur
     *            Action utilisateur.
     * @param a_numeroPage
     *            Numéro de page.
     * @param a_class
     *            Classe css.
     * @param a_texte
     *            Texte à inscrire.
     * 
     * @return String Une url de pagination en prod.
     */
    private String getUrlDev(int a_numeroPage, String a_class, String a_texte)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.droplet.CastoBlocPagination.getUrlDev()");
        }

        // REFERENCEMENT: on passe la page en changeant la page dans le
        // formulaire de pagination à l'aide de JS

        String l_fluxHTML = "<li class=\"pager_lien_page\"><a class=\"pager_precedent_suivant " + a_class + "\" href=\"javascript: navPaginationForm('" + a_numeroPage + "');\"  onclick=\"sc_classement('" + a_texte + "');\">" + a_texte + "</a></li>";

        // !REFERENCEMENT: on passe la page en changeant la page dans le
        // formulaire de pagination à l'aide de JS

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.droplet.CastoBlocPagination.getUrlDev()");
        }

        return l_fluxHTML;
    }
}