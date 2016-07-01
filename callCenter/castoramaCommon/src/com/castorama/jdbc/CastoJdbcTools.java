package com.castorama.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import atg.nucleus.logging.ApplicationLoggingImpl;

import com.castorama.constantes.CastoConstantes;

/**
 * @author Florte Jeremy (jeremy.florte@logicacmg.com)
 * 
 * @version 0.1
 * 
 * Description :
 * 
 * Classe d'utilitaires pour les connexions JDBC (rarement employees...).
 */
public class CastoJdbcTools extends /*Code Review*/ApplicationLoggingImpl/*Code Review*/
{
    /*
     * ------------------------------------------------------------------------ [
     * Attributs ]
     * ------------------------------------------------------------------------
     */

    /**
     * La DataSource pour les acc�s base.
     */
    private DataSource m_dataSource;

    /*
     * ------------------------------------------------------------------------ [
     * Accesseurs ]
     * ------------------------------------------------------------------------
     */

    /**
     * Renvoit la DataSource pour les acc�s base.
     * 
     * @return DataSource La DataSource pour les acc�s base.
     */
    public DataSource getDataSource()
    {
        return m_dataSource;
    }

    /**
     * Fixe la DataSource pour les acc�s base.
     * 
     * @param a_dataSource
     *            L anouvelle DataSource pour les acc�s base.
     */
    public void setDataSource(DataSource a_dataSource)
    {
        m_dataSource = a_dataSource;
    }

    /*
     * ------------------------------------------------------------------------ [
     * M�thodes ]
     * ------------------------------------------------------------------------
     */

    /**
     * M�thode qui renvoit une connexion vers la base de donn�es.
     * 
     * @return Connection Une connexion vers la base de donn�es.
     * 
     * @throws SQLException
     *             Si une exception survient lors de l'acc�s � la base.
     */
    public Connection getConnection() throws SQLException
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.jdbc.CastoJdbcTools.getConnection().");
        }

        Connection l_conn = getDataSource().getConnection();

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.jdbc.CastoJdbcTools.getConnection().");
        }
        return l_conn;
    }
    
    /**
     * M�thode qui ferme une connexion � la base de donn�es.
     * 
     * @param a_result
     *      Le ResultSet � fermer.
     * 
     * @param a_connection
     *            La connexion � fermer.
     * @param a_statement
     *            Le statement associ�.
     */
    public void closeConnection(ResultSet a_result, Connection a_connection, PreparedStatement a_statement)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.jdbc.CastoJdbcTools.CloseConnection(1).");
        }
        
        if (null != a_result)
        {
            try
            {
                a_result.close();
            }
            catch(SQLException l_exception)
            {
                logError(l_exception.toString());
            }
        }
        
        closeConnection(a_connection, a_statement);
        

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.jdbc.CastoJdbcTools.CloseConnection(1).");
        }
    }

    /**
     * M�thode qui ferme une connexion � la base de donn�es.
     * 
     * @param a_connection
     *            La connexion � fermer.
     * @param a_statement
     *            Le statement associ�.
     */
    public void closeConnection(Connection a_connection, PreparedStatement a_statement)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.jdbc.CastoJdbcTools.CloseConnection(2).");
        }

        if (null != a_statement)
        {
            try
            {
                a_statement.close();
            }
            catch (SQLException l_exception)
            {
                logError(l_exception.toString());
            }
        }
        if (null != a_connection)
        {
            try
            {
                a_connection.close();
            }
            catch (SQLException l_exception)
            {
                logError(l_exception.toString());
            }
        }

        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.jdbc.CastoJdbcTools.CloseConnection(2).");
        }
    }
    
    /**
     * M�thode qui ferme un Resultset mais sans fermer la connexion associ�e.
     * 
     * @param a_result
     *            Le Resultset � fermer.
     * @param a_statement
     *            Le statement associ�.
     */
    public void closeResultSet(ResultSet a_result, PreparedStatement a_statement)
    {
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_ENTREE + "com.castorama.jdbc.CastoJdbcTools.closeResultSet().");
        }
        
        if (null != a_result)
        {
            try
            {
                a_result.close();
            }
            catch(SQLException l_exception)
            {
                logError(l_exception.toString());
            }
        }
        
        if (null != a_statement)
        {
            try
            {
                a_statement.close();
            }
            catch (SQLException l_exception)
            {
                logError(l_exception.toString());
            }
        }
        
        if (isLoggingDebug())
        {
            logDebug(CastoConstantes.METHODE_SORTIE + "com.castorama.jdbc.CastoJdbcTools.closeResultSet().");
        }
    }
}