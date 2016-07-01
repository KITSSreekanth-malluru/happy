package com.castorama.i18n;

import atg.core.util.ResourceUtils;
import atg.droplet.DropletException;

/**
 * @author Florte Jérémy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe d'utilitaires pour l'internationalisation
 */
public class I18nTools
{
    /*
     * ------------------------------------------------------------------------ [
     * Constructeurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Empêche l'instanciation de cette classe.
     */
    private I18nTools()
    {
    }

    /*
     * ------------------------------------------------------------------------ [
     * Méthodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Retourne un message internationalisé encapsulé dans une DropletException.
     * 
     * @param a_message
     *            Message.
     * @param a_bundle
     *            Bundle contenant le message.
     * 
     * @return DropletException L'exception avec le bon message
     */
    public static DropletException getFormError(String a_message, String a_bundle)
    {
        return new DropletException(getMessage(a_message, a_bundle));
    }

    /**
     * . Retourne un message internationalisé.
     * 
     * @param a_message
     *            Message.
     * @param a_bundle
     *            Bundle contenant le message.
     * 
     * @return String Message internationalisé
     */
    public static String getMessage(String a_message, String a_bundle)
    {
        return ResourceUtils.getUserMsgResource(a_message, a_bundle, null);
    }

    /**
     * Retourne un message internationalisé encapsulé dans une DropletException.
     * 
     * @param a_message
     *            Message.
     * @param a_bundle
     *            Bundle contenant le message.
     * @param a_parameters
     *            Paramètres du message.
     * 
     * @return DropletException L'exception avec le bon message
     */
    public static DropletException getFormError(String a_message, String a_bundle, Object[] a_parameters)
    {
        return new DropletException(getMessage(a_message, a_bundle, a_parameters));
    }

    /**
     * Retourne un message internationalisé.
     * 
     * @param a_message
     *            Le message.
     * @param a_bundle
     *            Bundle contenant le message.
     * @param a_parameters
     *            Paramètres du message.
     * 
     * @return String Message internationalisé.
     */
    public static String getMessage(String a_message, String a_bundle, Object[] a_parameters)
    {
        return ResourceUtils.getUserMsgResource(a_message, a_bundle, null, a_parameters);
    }
}
