package com.castorama.panier.infosArticles;
/**
 * Ce bean stocke les informations li�es � l'article et n�cessaires au traitements de tri et regroupement
 * par type et d�lai d'exp�dition ainsi que par code fournisseur.
 * @author sderoullers
 */
public class ArticleInfo
{
	private String designation;
	private int reference;
	private int delaiBrut;
	private int typeExpedition;
	private Integer codeFournisseur;
	
	public Integer getCodeFournisseur()					{	return codeFournisseur;	}
	public int getDelaiBrut()								{	return delaiBrut;	}
	public String getDesignation()						{	return designation;	}
	public int getReference()								{	return reference;	}
	public int getTypeExpedition()						{	return typeExpedition;	}
	public void setCodeFournisseur(Integer integer)	{	codeFournisseur = integer;	}
	public void setDelaiBrut(int i)						{	delaiBrut = i;	}
	public void setDesignation(String string)			{	designation = string;	}
	public void setReference(int i)						{	reference = i;	}
	public void setTypeExpedition(int i)				{	typeExpedition = i;	}
}