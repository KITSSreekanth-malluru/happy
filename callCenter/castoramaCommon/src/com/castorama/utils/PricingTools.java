package com.castorama.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * PricingTools : Castorama 2002<br>
 * Class comportant les méthodes utiles pour la manipulation de montants.
 * 
 * @version 1.0
 * @author Damien DURIEZ - INTERNENCE (Decembre 2001)
 */
public class PricingTools
{
    /*
     * ------------------------------------------------------------------------ [
     * Constantes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Taille des groupements pour l'affichage des nombres.
     */
    private static final int TAILLE_GROUPE_NOMBRE = 3;

    /**
     * Le double 100.
     */
    private static final double CENT = 100.0;

    /*
     * ------------------------------------------------------------------------ [
     * Constructeurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Empêche l'instanciation de la classe.
     */
    private PricingTools()
    {
    }

    /*
     * ------------------------------------------------------------------------ [
     * Méthodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * Arrondi d'un prix.
     * 
     * @param a_nombre
     *            Le nombre à arrondir.
     * 
     * @return Double Le nombre arrondi?
     */
    public static Double arrondi(Double a_nombre)
    {
        return new Double((new Long(Math.round(a_nombre.doubleValue() * CENT)).doubleValue()) / CENT);
    }
    
    
    /**
     * Méthhode qui formate un montant en fonction d'une locale.
     * 
     * @param a_montant Le montant à formater.
     * @param a_locale  la locale à employer.
     * 
     * @return  String Un montant formaté.
     */
    public static String formatPrix(String a_montant, Locale a_locale)
    {
        return formatPrix(new Double(a_montant), a_locale);
    }
    
    /**
     * Méthhode qui formate un montant en employant la locale FRANCE par défaut.
     * 
     * @param a_montant Le montant à formater.
     * 
     * @return  String Un montant formaté.  
     */
    public static String formatPrix(String a_montant)
    {
        return formatPrix(new Double(a_montant), Locale.FRANCE);
    }
    
    /**
     * Méthhode qui formate un montant en fonction d'une locale.
     * 
     * @param a_montant Le montant à formater.
     * @param a_locale  la locale à employer.
     * 
     * @return  String Un montant formaté.  
     */
    public static String formatPrix(double a_montant, Locale a_locale)
    {
        return formatPrix(new Double(a_montant), a_locale);
    }
    
    /**
     * Méthhode qui formate un montant.
     * 
     * @param a_montant Le montant à formater.
     * 
     * @return  String Un montant formaté.  
     */
    public static String formatPrix(double a_montant)
    {
        return formatPrix(new Double(a_montant), Locale.FRANCE);
    }
    
    /**
     * Méthhode qui formate un montant en fonction d'une locale.
     * 
     * @param a_montant Le montant à formater.
     * @param a_locale  la locale à employer.
     * 
     * @return  String Un montant formaté.  
     */
    public static String formatPrix(Double a_montant, Locale a_locale)
    {
        DecimalFormat l_formatter = new DecimalFormat("0.00", new DecimalFormatSymbols(a_locale));
        
        l_formatter.setGroupingSize(TAILLE_GROUPE_NOMBRE);
        l_formatter.setGroupingUsed(true);
        
        return l_formatter.format(arrondi(a_montant));
    }
    
    /**
     * Méthhode qui formate un montant.
     * 
     * @param a_montant Le montant à formater.
     * 
     * @return  String Un montant formaté.  
     */
    public static String formatPrix(Double a_montant)
    {
        return formatPrix(a_montant, Locale.FRANCE);
    }
}