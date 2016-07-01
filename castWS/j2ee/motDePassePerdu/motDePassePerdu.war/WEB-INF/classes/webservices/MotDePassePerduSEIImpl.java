// no copyright notice
package webservices;


public class MotDePassePerduSEIImpl
  extends atg.webservice.ManagedComponentProperties
  implements javax.xml.rpc.server.ServiceLifecycle,
             MotDePassePerduSEI
{
  public static final String sServiceName = "motDePassePerdu";
  
  public static final String sNucleusPath = "/com/castorama/webservices/CastoMotDePassePerduManager";

  public static final String sFunctionalName = "null"; 
  
  public static boolean sRegistered = false;
  
  public static boolean sEnabled = true; 
  
  public java.lang.String motDePassePerdu(java.lang.String email) 
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
                    "motDePassePerdu",
                    getParameterNameValueMap(new String[] {"email"},
                                             new Object[] { email }) )) 
    {
      if (isLoggingDebug())
        logDebug(sServiceName + " error: Access denied using " + sFunctionalName);
      throw new atg.security.SecurityException("Access denied.");
    }
     
    try {
      // look up the Nucleus service and try to call it
     javax.naming.InitialContext ic = new javax.naming.InitialContext(); 
     com.castorama.webservices.CastoMotDePassePerduManager service = (com.castorama.webservices.CastoMotDePassePerduManager) ic.lookup("dynamo:/com/castorama/webservices/CastoMotDePassePerduManager"); 
     if (isLoggingDebug())
       logDebug(sServiceName + " debug: executing com.castorama.webservices.CastoMotDePassePerduManager.motDePassePerdu");
     return service.motDePassePerdu(email);
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
    return "motDePassePerdu";
  }
}   
