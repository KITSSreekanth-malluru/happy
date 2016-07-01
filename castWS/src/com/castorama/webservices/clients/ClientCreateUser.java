package com.castorama.webservices.clients;

import java.rmi.RemoteException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.rpc.ServiceException;

import com.atg.www.webservices.CreateUserSEI;
import com.atg.www.webservices.CreateUserSEIService;
import com.atg.www.webservices.CreateUserSEIServiceLocator;

public class ClientCreateUser {

	public static void main(String[] args) {
		System.out.println("Appel au webservice --> createUser " + Arrays.asList(args));
		System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");

		try {
			String l_result = createUser(args[0]);
			System.out.println("Retour du webservice");
			System.out.println("¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯");
			System.out.println(l_result);
		} catch (Exception e) {
			System.err.println("Exception levée : " + e.getMessage());
		}
	}

	public static String createUser(String requestXML) throws ServiceException, ServletException, RemoteException {
		CreateUserSEIService webService = new CreateUserSEIServiceLocator();
		CreateUserSEI stub = webService.getCreateUserSEIPort();
		return stub.createUser(requestXML);
	}

	public static String createUser(String requestXML, HttpServletRequest request) throws ServiceException,
			ServletException, RemoteException {
		String mUrl = request.getRequestURL().toString();
		mUrl = mUrl.substring(0, mUrl.indexOf("test.jsp")) + "createUser/createUser";
		CreateUserSEIService webService = new CreateUserSEIServiceLocator();
		((CreateUserSEIServiceLocator) webService).setCreateUserSEIPortEndpointAddress(mUrl);
		CreateUserSEI stub = webService.getCreateUserSEIPort();
		return stub.createUser(requestXML);
	}
}
