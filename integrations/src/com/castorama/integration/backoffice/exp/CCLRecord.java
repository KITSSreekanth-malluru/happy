/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import org.joda.time.format.FormatUtils;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 *
 */
class CCLRecord {
	
	private String orderId = "";
	
	private String codeArticle = "";
	
	private String libelleArticle = "";

	private String units = "";
	
	private int poids;

	private int LDF;

	private int HN;
	
	private int delaiLivraison;
	
	private int poidsPFT;

	private int poidsPFL;
	

	private long quantity;
	
	
	private double price;

	private int frais;

	private double someForf;
	
	
	
	

	/**
	 * 
	 */
	public CCLRecord() {
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("CCL|");
		
		UtilFormat.fillEnd(sb, orderId, 20, ' ').append('|');
		UtilFormat.fillEnd(sb, codeArticle, 10, ' ').append('|');
		if(libelleArticle.length()>74){
			libelleArticle=libelleArticle.substring(0, 74).concat("...");
		}
		UtilFormat.fillEnd(sb, libelleArticle, 78, ' ').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(0.0), 13, '0').append('|');
		
		
		UtilFormat.fillEnd(sb, units, 3, ' ').append('|');
		UtilFormat.fillStart(sb, "" + poids, 8, '0').append('|');
		UtilFormat.fillStart(sb, "" + quantity, 9, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(0.0), 13, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(0.0), 13, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(price), 13, '0').append('|');

		sb.append("" + LDF + "|");
		sb.append("" + HN + "|");

		UtilFormat.fillStart(sb, "" + delaiLivraison, 4, '0').append('|');
		UtilFormat.fillStart(sb, "" + frais, 13, '0').append('|');
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(0.0), 13, '0').append('|');
		
		UtilFormat.fillStart(sb, "" + poidsPFT, 12, '0').append('|');
		UtilFormat.fillStart(sb, "" + poidsPFL, 12, '0').append('|');

		
		
		UtilFormat.fillStart(sb, UtilFormat.toMoneyString(someForf), 13, '0').append('|').append('\n');
		
		return sb.toString();
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = UtilFormat.valueToString(orderId);
	}

	/**
	 * @return the codeArticle
	 */
	public String getCodeArticle() {
		return codeArticle;
	}

	/**
	 * @param codeArticle the codeArticle to set
	 */
	public void setCodeArticle(Integer codeArticle) {
		this.codeArticle = UtilFormat.valueToString(codeArticle);
	}

	/**
	 * @return the libelleArticle
	 */
	public String getLibelleArticle() {
		return libelleArticle;
	}

	/**
	 * @param libelleArticle the libelleArticle to set
	 */
	public void setLibelleArticle(String libelleArticle) {
		this.libelleArticle = UtilFormat.valueToString(libelleArticle);
	}

	/**
	 * @return the poids
	 */
	public int getPoids() {
		return poids;
	}

	/**
	 * @param poids the poids to set
	 */
	public void setPoids(Integer poids) {
		this.poids = (null == poids)?0:poids;
	}

	/**
	 * @return the quantity
	 */
	public long getQuantity() {
		return quantity;
	}

	/**
	 * @param value the quantity to set
	 */
	public void setQuantity(Long value) {
		this.quantity = (null == value)?0:value;
	}
		/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = UtilFormat.valueToDouble(price);
	}

	/**
	 * @return the lDF
	 */
	public int getLDF() {
		return LDF;
	}

	/**
	 * @param ldf the lDF to set
	 */
	public void setLDF(Integer ldf) {
		LDF = (null == ldf)?0:ldf;
	}

	/**
	 * @return the hN
	 */
	public int getHN() {
		return HN;
	}

	/**
	 * @param hn the hN to set
	 */
	public void setHN(Integer hn) {
		HN = (null==hn)?0:hn;
	}

	/**
	 * @return the delaiLivraison
	 */
	public int getDelaiLivraison() {
		return delaiLivraison;
	}

	/**
	 * @param delaiLivraison the delaiLivraison to set
	 */
	public void setDelaiLivraison(int delaiLivraison) {
		this.delaiLivraison = delaiLivraison;
	}

	/**
	 * @return the frais
	 */
	public double getFrais() {
		return frais;
	}

	/**
	 * @param value the frais to set
	 */
	public void setFrais(Float value) {
		this.frais = UtilFormat.valueToInt(value);
	}

	/**
	 * @return the poidsPFT
	 */
	public int getPoidsPFT() {
		return poidsPFT;
	}

	/**
	 * @param poidsPFT the poidsPFT to set
	 */
	public void setPoidsPFT(Integer poidsPFT) {
		this.poidsPFT = (null == poidsPFT)?0:poidsPFT;
	}

	/**
	 * @return the poidsPFL
	 */
	public int getPoidsPFL() {
		return poidsPFL;
	}

	/**
	 * @param poidsPFL the poidsPFL to set
	 */
	public void setPoidsPFL(Integer poidsPFL) {
		this.poidsPFL = (null ==poidsPFL)?0:poidsPFL;
	}

	/**
	 * @return the someForf
	 */
	public double getSomeForf() {
		return someForf;
	}

	/**
	 * @param someForf the someForf to set
	 */
	public void setSomeForf(Double someForf) {
		this.someForf = UtilFormat.valueToDouble(someForf);
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param value the units to set
	 */
	public void setUnits(String value) {
		this.units = UtilFormat.valueToString(value);
	}
}
