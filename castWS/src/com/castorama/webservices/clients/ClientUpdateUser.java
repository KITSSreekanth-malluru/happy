package com.castorama.webservices.clients;

import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import com.atg.www.webservices.UpdateUserSEI;
import com.atg.www.webservices.UpdateUserSEIService;
import com.atg.www.webservices.UpdateUserSEIServiceLocator;

public class ClientUpdateUser {

	public static void main(String[] args) {
		System.out.println("Appel au webservice --> updateUser " + Arrays.asList(args));
		System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");

		try {
			String l_result = updateUser(args[0]);
			System.out.println("Retour du webservice");
			System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
			System.out.println(l_result);
		} catch (Exception e) {
			System.err.println("Exception levée : " + e.getMessage());
		}
	}

	public static String updateUser(String requestXML) throws ServiceException, ServletException, RemoteException {
		UpdateUserSEIService webService = new UpdateUserSEIServiceLocator();
		UpdateUserSEI stub = webService.getUpdateUserSEIPort();
		return stub.updateUser(requestXML);
	}

	public static String updateUser(String requestXML, HttpServletRequest request) throws ServiceException, ServletException, RemoteException {
		String mUrl = request.getRequestURL().toString();
		mUrl = mUrl.substring(0, mUrl.indexOf("test.jsp")) + "updateUser/updateUser";
		UpdateUserSEIService webService = new UpdateUserSEIServiceLocator();
		((UpdateUserSEIServiceLocator) webService).setUpdateUserSEIPortEndpointAddress(mUrl);
		UpdateUserSEI stub = webService.getUpdateUserSEIPort();
		return stub.updateUser(requestXML);
	}
}
