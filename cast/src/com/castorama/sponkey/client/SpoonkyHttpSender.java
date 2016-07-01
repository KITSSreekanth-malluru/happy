package com.castorama.sponkey.client;

import java.net.*;
import java.net.Proxy.*;

import org.apache.axis.*;
import org.apache.axis.transport.http.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.*;

import atg.nucleus.Nucleus;

import com.castorama.sponkey.TelFormServiceClient;

/**
 * ToDo: DOCUMENT ME!
 * 
 * @author Andrei_Raichonak
 */
@SuppressWarnings("serial")
public class SpoonkyHttpSender extends CommonsHTTPSender {
	private static final int HTTP_PORT = 80;

	private static final int HTTPS_PORT = 443;

	/**
	 * ToDo: DOCUMENT ME!
	 * 
	 * @param messageContext
	 *            ToDo: DOCUMENT ME!
	 * 
	 * @throws AxisFault
	 *             ToDo: DOCUMENT ME!
	 */
	@Override
	public void invoke(MessageContext messageContext) throws AxisFault {
		//System.out.println("\n[START_SOAP]\n" + messageContext.getRequestMessage().getSOAPPartAsString()
		//		+ "\n[END_SOAP]");
		super.invoke(messageContext);

		//System.out.println("\n[START_SOAP]\n" + messageContext.getResponseMessage().getSOAPPartAsString()
		//		+ "\n[END_SOAP]");

	}

	@Override
	protected HostConfiguration getHostConfiguration(HttpClient client, MessageContext context, URL url) {
		Nucleus nuke = Nucleus.getGlobalNucleus();
		TelFormServiceClient telFormServiceClient = (TelFormServiceClient)nuke.resolveName("/com/castorama/sponkey/TelFormServiceClient");
		Proxy proxy = null;
		if(telFormServiceClient.getProxyEnabled()) {
			proxy = getProxy(telFormServiceClient.getProxyHost(), telFormServiceClient.getProxyPort(), 
				telFormServiceClient.getProxyPassword(), telFormServiceClient.getProxyUser());
		}
		setupHttpClient(client, proxy, url.toString());
		return client.getHostConfiguration();
	}

	private void setupHttpClient(HttpClient pClient, Proxy pProxy, String pURL) {
		if (pProxy != null && !Proxy.NO_PROXY.equals(pProxy) // && !isHttps(pURL)
				&& pProxy.address() instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress) pProxy.address();
			//System.out.println(getDomain(address.getHostName()));
			pClient.getHostConfiguration().setProxy(getDomain(address.getHostName()), address.getPort());
			//System.out.println(pClient.getHostConfiguration().getProxyHost());
			if (pProxy instanceof AuthenticatedProxy) {
				AuthenticatedProxy authProxy = (AuthenticatedProxy) pProxy;
				Credentials credentials = new UsernamePasswordCredentials(authProxy.getUserName(), authProxy
						.getPassword());
				AuthScope proxyAuthScope = new AuthScope(address.getHostName(), address.getPort(), AuthScope.ANY_REALM);
				pClient.getState().setProxyCredentials(proxyAuthScope, credentials);
			}
		}
	}

	

	public static boolean isHttps(String pURL) {
		return pURL.matches("https.*");
	}

	public static String getDomain(String repositoryUrl) {
		// looking for double slash in the input URL.
		int doubleSlash = repositoryUrl.indexOf("://");
		// cut double slash from URL if it exists.
		String result = doubleSlash < 0 ? repositoryUrl : repositoryUrl.substring(doubleSlash + 3); 

		//determine positions of port char and patch char.
		int pathPosition = result.indexOf('/');
		int portPosition = result.indexOf(':');

		int first = 0, last = 0;

		// minimum positive, or string length
		if (portPosition > 0 && pathPosition > 0)
			last = Math.min(portPosition, pathPosition);
		else if (portPosition > 0)
			last = portPosition;
		else if (pathPosition > 0)
			last = pathPosition;
		else
			last = result.length();

		return result.substring(first, last);
	}
	
	public static Proxy getProxy(String proxyHost, int proxyPort, String proxyUsername, String proxyPassword) {
		boolean authenticated = (proxyUsername != null && proxyPassword != null && proxyUsername.length() > 0 && proxyPassword
				.length() > 0);
		if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {			
			InetSocketAddress sockAddr = new InetSocketAddress(proxyHost, proxyPort);
			if (authenticated) {
				return new AuthenticatedProxy(Type.HTTP, sockAddr, proxyUsername, proxyPassword);
			} else {
				return new Proxy(Type.HTTP, sockAddr);
			}
		}
		return Proxy.NO_PROXY;
	}
	
	public static int getPort(String repositoryUrl) {
		int colonSlashSlash = repositoryUrl.indexOf("://");
		int colonPort = repositoryUrl.indexOf(':', colonSlashSlash + 1);
		if (colonPort < 0)
			return isHttps(repositoryUrl) ? HTTPS_PORT : HTTP_PORT;

		int requestPath = repositoryUrl.indexOf('/', colonPort + 1);

		int end;
		if (requestPath < 0)
			end = repositoryUrl.length();
		else
			end = requestPath;

		return Integer.parseInt(repositoryUrl.substring(colonPort + 1, end));
	}

}
