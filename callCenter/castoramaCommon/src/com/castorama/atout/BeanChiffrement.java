package com.castorama.atout;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.sql.DataSource;

	 
/**.
* BeanChiffrement : Castorama 2001
* Ce composant est chifrer ou dï¿½chiffer un nu de carte atout par exemple
* @version 1.0  
* @author Damien DURIEZ - INTERNENCE (Aout 2001) - Revu est simplifie par Axel : HOUPS !!!
*/
public class BeanChiffrement
{
	
    private static String ms_strRequeteEncrypt  = "{call PROC_DES56ENCRYPT(?, ?)}" ;    
    private static String ms_strRequeteDecrypt  = "{call PROC_DES56DECRYPT(?, ?)}" ;
	
    private static final int DEUX = 2;
    
	protected DataSource m_DataSource = null;	// Source de donnï¿½e ORACLE
	
	/**.
	* Modification du DataSource
	* @param   a_DataSource        DataSource
	* @throws  none
	*/
	public void setDataSource( DataSource a_DataSource )
	{
	  	m_DataSource = a_DataSource;
	}
	
	
	/**.
	* Rï¿½cupï¿½ration du DataSource
	* @param none
	* @return DataSource
	* @throws none
	*/
	public DataSource getDataSource()
	{
	  	return m_DataSource;
	}
	
	/**.
     * 
     * Encode une carte l'Atout
     * @param   a_strData       Data à encoder
     * @return  Data encodée
	 */
	public String encode(String a_strData)
	{
		//trace.logOpen(this,".encode");
		String l_strEncodedData = null;
		Connection l_Conn = null;
		CallableStatement l_Cs = null ;
		try
		{
			l_Conn	= getDataSource().getConnection() ;
			l_Cs = l_Conn.prepareCall(ms_strRequeteEncrypt) ;
			l_Cs.setString(1,a_strData);
			l_Cs.registerOutParameter(DEUX,java.sql.Types.VARCHAR) ;
			l_Cs.executeUpdate() ;
			l_strEncodedData = l_Cs.getString(DEUX) ;
			
		}
		catch (Exception e)
		{
			//trace.logError(this,e,".encode Exception : "+e.toString());
		}
		finally
		{
			try
			{
				if(l_Cs!=null) 
                    {
                    l_Cs.close();
                    }
				if(l_Conn!=null)
                    {
                    l_Conn.close();
                    }
			}
			catch (Exception l_oException)
			{
				//trace.logError(this,e,".encode Exception : "+e.toString());
                
			}
			finally
			{
				//trace.logClose(this,".encode");
			}
		}
		return l_strEncodedData ;
	}
	
	/**.
     * 
     * Decode la data 
     * @param       a_strEncodedData        Data encodeée
     * @return      Data plus encodée
	 */
	public String decode(String a_strEncodedData)
	{	
		//trace.logOpen(this,".encode");
		String l_strDecodedData = null;
		Connection l_Conn = null;
		CallableStatement l_Cs = null ;
		try
		{
			l_Conn	= getDataSource().getConnection() ;
			l_Cs = l_Conn.prepareCall(ms_strRequeteDecrypt) ;
			l_Cs.setString(1,a_strEncodedData);
			l_Cs.registerOutParameter(DEUX,java.sql.Types.VARCHAR) ;
			l_Cs.executeUpdate() ;
			l_strDecodedData = l_Cs.getString(DEUX) ;
			
		}
		catch (Exception e)
		{
			//trace.logError(this,e,".decode Exception : "+e.toString());
		}
		finally
		{
			try
			{
				if(l_Cs!=null)
                    {
                    l_Cs.close();
                    }
				if(l_Conn!=null)
                    {
                    l_Conn.close();
                    }
			}
			catch (Exception e)
			{
				//trace.logError(this,e,".decode Exception : "+e.toString());
			}
			finally
			{
				//trace.logClose(this,".decode");
			}
		}	
		return l_strDecodedData ;
	}
}