package com.castorama.integration.order.exp;

import java.util.List;

public class OrderInfoToExport {

	private String email;
	private String name;
	private String surname;
	private List<String> prodReferences;
	// init default separator
	private String separator = "|";

	public OrderInfoToExport(String email, String name, String surname) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
	}

	public OrderInfoToExport(String email, String name, String surname,
			String separator) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
		this.separator = separator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public List<String> getProdReferences() {
		return prodReferences;
	}

	public void setProdReferences(List<String> prodReferences) {
		this.prodReferences = prodReferences;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(email).append(separator);
		sb.append(name).append(separator);
		sb.append(surname).append(separator);
		if (prodReferences != null || !prodReferences.isEmpty()) {
			for (String ref : prodReferences) {
				sb.append(ref).append(separator);
			}
			sb.append("\n");
		}

		return sb.toString();
	}
}
