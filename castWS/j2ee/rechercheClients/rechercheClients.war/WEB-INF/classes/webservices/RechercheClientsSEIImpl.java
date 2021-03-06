// no copyright notice
package webservices;


public class RechercheClientsSEIImpl
  extends atg.webservice.ManagedComponentProperties
  implements javax.xml.rpc.server.ServiceLifecycle,
             RechercheClientsSEI
{
  public static final String sServiceName = "rechercheClients";
  
  public static final String sNucleusPath = "/com/castorama/webservices/CastoRechercheClientsManager";

  public static final String sFunctionalName = "null"; 
  
  public static boolean sRegistered = false;
  
  public static boolean sEnabled = true; 
  
  public java.lang.String rechercheClients(java.lang.String requestXML) 
    throws java.rmi.RemoteException, atg.security.SecurityException
  {
    if (! sRegistered) { 
      sEnabled = register(null);
      sRegistered = true;
    }
     
    if (! sEnabled) {
      if (isLoggingDebug())
        logDebug(sServiceName + " error: disabled");
      throw new java.rmi.RemoteException("disabled");
    }

    if ((!"null".equals(sFunctionalName)) &&
         !getNucleusSecurityManager().hasAccess(
                    sFunctionalName, 
                    sNucleusPath, 
                    "rechercheClients",
                    getParameterNameValueMap(new String[] {"requestXML"},
                                             new Object[] { requestXML }) )) 
    {
      if (isLoggingDebug())
        logDebug(sServiceName + " error: Access denied using " + sFunctionalName);
      throw new atg.security.SecurityException("Access denied.");
    }
     
    try {
      // look up the Nucleus service and try to call it
     javax.naming.InitialContext ic = new javax.naming.InitialContext(); 
     com.castorama.webservices.CastoRechercheClientsManager service = (com.castorama.webservices.CastoRechercheClientsManager) ic.lookup("dynamo:/com/castorama/webservices/CastoRechercheClientsManager"); 
     if (isLoggingDebug())
       logDebug(sServiceName + " debug: executing com.castorama.webservices.CastoRechercheClientsManager.rechercheClients");
     return service.rechercheClients(requestXML);
    }
    catch (javax.naming.NamingException ne) {
      if (isLoggingError())
        logError(sServiceName + " error: " + ne, ne);
      throw new java.rmi.RemoteException(ne.getMessage(), ne);
    }
    
  }
  
  public void destroy() {
  }
  
  public void init(Object pObject)
    throws javax.xml.rpc.ServiceException
  {
    if (! sRegistered) {
      sEnabled = register(pObject);
      sRegistered = true;
    }
  }

   public String getComponentName() {
    return "rechercheClients";
  }
}   
