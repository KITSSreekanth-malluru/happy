package com.castorama.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CheckIfCastoramaStaff extends DynamoServlet {

	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	
	private ArrayList<String> ipList;
	private String ips;

	/** OUTPUT parameter name. */
	private static final ParameterName OUTPUT = ParameterName
			.getParameterName("output");


	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String currentIP = pRequest.getHeader(X_FORWARDED_FOR);
		if (currentIP != null && isIP(currentIP) && ipList.contains(currentIP)) {
				pRequest.setParameter(X_FORWARDED_FOR, "true");
				pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
			 }
		 else {
				pRequest.setParameter(X_FORWARDED_FOR, "false");
				pRequest.serviceParameter(OUTPUT, pRequest, pResponse);
			}
		}
	

	public boolean isIP(String ip) {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	public ArrayList<String> parseIp(String ipList) {
		String[] listIp = ipList.split(",");
		ArrayList<String> iP = new ArrayList<String>();
		for (String str : listIp) {
			  String ip=str.replace(" ","");
			  iP.add(ip);
		}
		return iP;

	}

	public String getIps() {
		return ips;
	}

	public ArrayList<String> getIpList() {
		
		return ipList;
	}
	
	public void  setIpList(ArrayList<String> ipList){
		this.ipList=ipList;
		
	}

	public void reinitialize(String ips) {
		setIpList(parseIp(ips));

	}

	public void setIps(String ips) {
		this.ips = ips;
		reinitialize(ips);
	}

}
