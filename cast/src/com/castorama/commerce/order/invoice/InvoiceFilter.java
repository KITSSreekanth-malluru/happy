package com.castorama.commerce.order.invoice;

import static com.castorama.constantes.CastoConstantesOrders.*;

import java.io.IOException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import atg.adapter.gsa.ChangeAwareSet;
import atg.repository.RepositoryItem;

import com.castorama.utils.CastPlaceList;
import com.castorama.utils.SaxXmlBuilder;
import com.castorama.utils.CastPlaceList.CastPlace;

/**
 * <code>InvoiceFilter</code> is subclass of <code>SaxXmlBuilder</code>. It is
 * used for generation xml for generation invoice shipment pdf.
 *
 * @version 1.0
 */
public final class InvoiceFilter extends SaxXmlBuilder {

    /** LVILLE_FACTURE constant. */
    private static final String LVILLE_FACTURE = "LVILLE_FACTURE";

    /** CODE_POSTAL_FACTURE constant. */
    private static final String CODE_POSTAL_FACTURE = "CODE_POSTAL_FACTURE";

    /** LADRESSE_3_FACTURE constant. */
    private static final String LADRESSE_3_FACTURE = "LADRESSE_3_FACTURE";

    /** LADRESSE_2_FACTURE constant. */
    private static final String LADRESSE_2_FACTURE = "LADRESSE_2_FACTURE";

    /** LADRESSE_1_FACTURE constant. */
    private static final String LADRESSE_1_FACTURE = "LADRESSE_1_FACTURE";

    /** LNOM_FACTURE constant. */
    private static final String LNOM_FACTURE = "LNOM_FACTURE";

    /** FRANCE constant. */
    private static final String FRANCE = "FRANCE";

    /** LVILLE_LIVRAISON constant. */
    private static final String LVILLE_LIVRAISON = "LVILLE_LIVRAISON";

    /** CODE_POSTAL_LIVRAISON constant. */
    private static final String CODE_POSTAL_LIVRAISON = "CODE_POSTAL_LIVRAISON";

    /** LADRESSE_3_LIVRAISON constant. */
    private static final String LADRESSE_3_LIVRAISON = "LADRESSE_3_LIVRAISON";

    /** LADRESSE_2_LIVRAISON constant. */
    private static final String LADRESSE_2_LIVRAISON = "LADRESSE_2_LIVRAISON";

    /** LADRESSE_1_LIVRAISON constant. */
    private static final String LADRESSE_1_LIVRAISON = "LADRESSE_1_LIVRAISON";

    /** LNOM_LIVRAISON constant. */
    private static final String LNOM_LIVRAISON = "LNOM_LIVRAISON";

    /** BILLING_ADDRESS constant. */
    private static final String BILLING_ADDRESS = "billing_address";

    /** SHIPPING_ADDRESS constant. */
    private static final String SHIPPING_ADDRESS = "shipping_address";

    /** COUNTRY constant. */
    private static final String COUNTRY = "country";

    /** CITY constant. */
    private static final String CITY = "city";

    /** ZIP constant. */
    private static final String ZIP = "zip";

    /** STREET3 constant. */
    private static final String STREET3 = "street3";

    /** STREET2 constant. */
    private static final String STREET2 = "street2";

    /** STREET1 constant. */
    private static final String STREET1 = "street1";

    /** MTOTAL_TTC constant. */
    private static final String MTOTAL_TTC = "MTOTAL_TTC";

    /** MTOTAL_HT constant. */
    private static final String MTOTAL_HT = "MTOTAL_HT";

    /** MPV_TOTAL_TTC constant. */
    private static final String MPV_TOTAL_TTC = "MPV_TOTAL_TTC";

    /** MTOTAL_TVA constant. */
    private static final String MTOTAL_TVA = "MTOTAL_TVA";

    /** MTAUX_TVA constant. */
    private static final String MTAUX_TVA = "MTAUX_TVA";

    /** QUNITE_VENTE constant. */
    private static final String QUNITE_VENTE = "QUNITE_VENTE";

    /** MPV_HT constant. */
    private static final String MPV_HT = "MPV_HT";

    /** LARTICLE constant. */
    private static final String LARTICLE = "LARTICLE";

    /** CARTICLE constant. */
    private static final String CARTICLE = "CARTICLE";

    /** LIGNES constant. */
    private static final String LIGNES = "LIGNES";

    /** CENLEVEMENT constant. */
    private static final String CENLEVEMENT = "CENLEVEMENT";

    /** DFACTURE_AVOIR constant. */
    private static final String DFACTURE_AVOIR = "DFACTURE_AVOIR";

    /** CCLIENT constant. */
    private static final String CCLIENT = "CCLIENT";

    /** TOTAL_TVA constant. */
    private static final String TOTAL_TVA = "total_tva";

    /** TOTAL_HT constant. */
    private static final String TOTAL_HT = "total_ht";

    /** TOTAL constant. */
    private static final String TOTAL = "total";

    /** PERCENT constant. */
    private static final String PERCENT = "percent";

    /** ITEM constant. */
    private static final String ITEM = "item";

    /** TOTAL_TTC constant. */
    private static final String TOTAL_TTC = "total_ttc";

    /** TOTAL_TAUX_TVA constant. */
    private static final String TOTAL_TAUX_TVA = "total_taux_tva";

    /** TAUX_TVA constant. */
    private static final String TAUX_TVA = "taux_tva";

    /** COUNT constant. */
    private static final String COUNT = "count";

    /** PRICE constant. */
    private static final String PRICE = "price";

    /** NAME constant. */
    private static final String NAME = "name";

    /** CODE constant. */
    private static final String CODE = "code";

    /** ITEMS constant. */
    private static final String ITEMS = "items";

    /** INVOICE constant. */
    private static final String INVOICE = "invoice";

    /** FACTURE constant. */
    private static final String FACTURE = "facture";

    /** REFERENCE constant. */
    private static final String REFERENCE = "reference";

    /** DATE constant. */
    private static final String DATE = "date";

    /** FACTURE_ID constant. */
    private static final String FACTURE_ID = "facture_id";

    /** CLIENT_ID constant. */
    private static final String CLIENT_ID = "client_id";

    /** CDATA constant. */
    private static final String CDATA = "CDATA";

    /** Apache Commons Logging instance. */
    private static final Log LOGGER = LogFactory.getLog(InvoiceFilter.class);

    /** Holds the DATE_FORMAT. */
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    /** Holds the formatter for dates. */
    private static final SimpleDateFormat SDF = new SimpleDateFormat(InvoiceFilter.DATE_FORMAT);

    private static final NumberFormat numberFormatter = NumberFormat.getIntegerInstance();

    /** TAX D3E. */
    private static final String TAXD3E = "TAXD3E";

    /**LABEL TAX D3E. */
    private static final String LTAXD3E = "LTAXD3E";

    /** TAX REP. */
    private static final String TAXREP = "TAXREP";

    /**LABEL TAX REP. */
    private static final String LTAXREP = "LTAXREP";

    private static final String CURRENCY = "EUR";
    private static final String VATS = "vats";

    static {
    	numberFormatter.setMinimumFractionDigits(0);
    	numberFormatter.setMaximumFractionDigits(0);
    	numberFormatter.setGroupingUsed(false);
    }

    private static final NumberFormat numberFormatter2Digit = NumberFormat.getIntegerInstance();

    static {
    	numberFormatter2Digit.setMinimumFractionDigits(2);
    	numberFormatter2Digit.setMaximumFractionDigits(2);
    	numberFormatter2Digit.setGroupingUsed(false);
    }


    /** Holds the createdDate. */
    private Date mCreatedDate;

    /** facture property */
    private RepositoryItem mFacture;

    final CastPlaceList mCountries;

    /**
     * Creates a new FeeChangeApprovalFormFilter object.
     *
     * @param pItem content      form content.
     */

    public InvoiceFilter(final RepositoryItem pItem, final CastPlaceList pCountries) {
        this.mFacture = pItem;
        this.mCountries = pCountries;
    }

    /**
     * Parse a document.
     *
     * @param  pInputSource The input source for the document entity
     *
     * @throws SAXException Any SAX exception, possibly wrapping another
     *                      exception.
     * @throws IOException  An IO exception from the parser, possibly from a
     *                      byte stream or character stream supplied by the
     *                      application
     */
    public void parse(final InputSource pInputSource) throws SAXException, IOException {
        startDocument();
        final AttributesImpl attrs = new AttributesImpl();
        HashMap<String, Float> totalVATValues = new HashMap<String, Float>();
        Object value = mFacture.getPropertyValue(CCLIENT);
        attrs.addAttribute("", CLIENT_ID, CLIENT_ID, CDATA, value != null ? value.toString() : "");
        attrs.addAttribute("", FACTURE_ID, FACTURE_ID, CDATA, mFacture.getRepositoryId());
        value = mFacture.getPropertyValue(DFACTURE_AVOIR);
        attrs.addAttribute("", DATE, DATE, CDATA, value != null ? SDF.format(mFacture.getPropertyValue(DFACTURE_AVOIR)) : "");

        value = mFacture.getPropertyValue(NCOMMANDE_CLIENT);
        attrs.addAttribute("", REFERENCE, REFERENCE, CDATA, value != null ? value.toString() : "");
        value = mFacture.getPropertyValue(CENLEVEMENT);
        attrs.addAttribute("", FACTURE, FACTURE, CDATA, value != null ? value.toString() : "");
        startElement("", INVOICE, INVOICE, attrs);

        addDeliveryAddressInfo();
        addBillingAddressInfo();
        final AttributesImpl attrs1 = new AttributesImpl();
        startElement("", ITEMS, ITEMS, attrs1);

        ChangeAwareSet lines = (ChangeAwareSet) mFacture.getPropertyValue(LIGNES);
        Iterator iterator = lines.iterator();
        while (iterator.hasNext()) {
            RepositoryItem line = (RepositoryItem) iterator.next();

            AttributesImpl attributes = new AttributesImpl();

            value = line.getPropertyValue("TARTICLE");
            if (value != null && !"1".equals(value.toString())) {
                value = line.getPropertyValue(CARTICLE);
                attributes.addAttribute("", CODE, CODE, CDATA, value != null ? value.toString() : "");
            }
            value = line.getPropertyValue(LARTICLE);

            attributes.addAttribute("", NAME, NAME, CDATA, value != null ? value.toString() : "");
            Object taxLabel = line.getPropertyValue(LTAXD3E);
            Object taxValue = line.getPropertyValue(TAXD3E);
            if (taxLabel == null || taxValue == null) {
                attributes.addAttribute("", TAXD3E, TAXD3E, CDATA, "");
            } else {
                attributes.addAttribute("", TAXD3E, TAXD3E, CDATA, taxLabel.toString() + " " + taxValue.toString() + " " + CURRENCY);
            }

            taxLabel = line.getPropertyValue(LTAXREP);
            taxValue = line.getPropertyValue(TAXREP);
            if (taxLabel == null || taxValue == null) {
                attributes.addAttribute("", TAXREP, TAXREP, CDATA, "");
            } else {
                attributes.addAttribute("", TAXREP, TAXREP, CDATA, taxLabel.toString() + " " + taxValue.toString() + " " + CURRENCY);
            }

            value = line.getPropertyValue(MPV_HT);
            attributes.addAttribute("", PRICE, PRICE, CDATA, value != null ? numberFormatter2Digit.format(value) : "");
            value = line.getPropertyValue(QUNITE_VENTE);
            attributes.addAttribute("", COUNT, COUNT, CDATA, value != null ? (numberFormatter.format(value)) : "");
            value = line.getPropertyValue(MTAUX_TVA);
            String VATValue = numberFormatter2Digit.format(value);
            attributes.addAttribute("", TAUX_TVA, TAUX_TVA, CDATA, value != null ? VATValue : "");
            value = line.getPropertyValue(MTOTAL_TVA);
            Float VATTotalValue = (Float) value;
            attributes.addAttribute("", TOTAL_TAUX_TVA, TOTAL_TAUX_TVA, CDATA, value != null ? numberFormatter2Digit.format(value) : "");
            value = line.getPropertyValue(MPV_TOTAL_TTC);
            attributes.addAttribute("", TOTAL_TTC, TOTAL_TTC, CDATA, value != null ? numberFormatter2Digit.format(value) : "");
            startElement("", ITEM, ITEM, attributes);
            endElement("", ITEM, ITEM);
            if (totalVATValues.get(VATValue) == null) {
                totalVATValues.put(VATValue, VATTotalValue);
            } else {
                Float currentVATTotal = totalVATValues.get(VATValue);
                totalVATValues.put(VATValue, VATTotalValue + currentVATTotal);
            }
        }

        endElement("", ITEMS, ITEMS);

        AttributesImpl attrs2 = new AttributesImpl();

        Float totalHt = (Float)mFacture.getPropertyValue(MTOTAL_HT);
        attrs2.addAttribute("", TOTAL_HT, TOTAL_HT, CDATA, totalHt != null? numberFormatter2Digit.format(totalHt):"");
        value = mFacture.getPropertyValue(MTOTAL_TVA);
        attrs2.addAttribute("", TOTAL_TVA, TOTAL_TVA, CDATA, value != null? numberFormatter2Digit.format(value):"");
        Float totalTtc = (Float)mFacture.getPropertyValue(MTOTAL_TTC);
        attrs2.addAttribute("", TOTAL_TTC, TOTAL_TTC, CDATA, totalTtc != null? numberFormatter2Digit.format(totalTtc):"");
        startElement("", TOTAL, TOTAL, attrs2);
        endElement("", TOTAL, TOTAL);

        startElement("", VATS, VATS, new AttributesImpl());
        for (String key : totalVATValues.keySet()) {
            AttributesImpl vatAttribute = new AttributesImpl();
            vatAttribute.addAttribute("", PERCENT, PERCENT, CDATA, numberFormatter2Digit.format(Float.parseFloat(key)) + " %");
            value = totalVATValues.get(key);
            vatAttribute.addAttribute("", TOTAL, TOTAL, CDATA, value != null ? numberFormatter2Digit.format(value) : "");
            startElement("", TAUX_TVA, TAUX_TVA, vatAttribute);
            endElement("", TAUX_TVA, TAUX_TVA);
        }
        endElement("", VATS, VATS);

        endElement("", INVOICE, INVOICE);
        endDocument();
    }

    /**
     *
     * @throws SAXException
     */
    private void addDeliveryAddressInfo() throws SAXException {
        final AttributesImpl attrs = new AttributesImpl();

        Object value = mFacture.getPropertyValue(LNOM_LIVRAISON);
        attrs.addAttribute("", NAME, NAME, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_1_LIVRAISON);
        attrs.addAttribute("", STREET1, STREET1, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_2_LIVRAISON);
        attrs.addAttribute("", STREET2, STREET2, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_3_LIVRAISON);
        attrs.addAttribute("", STREET3, STREET3, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(CODE_POSTAL_LIVRAISON);
        attrs.addAttribute("", ZIP, ZIP, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LVILLE_LIVRAISON);
        attrs.addAttribute("", CITY, CITY, CDATA, " " + (value != null? value.toString():""));
        value = mFacture.getPropertyValue("CPAYS_LIVRAISON");
        if(value != null) {
        	CastPlace county = (CastPlace)mCountries.getPlaceForId(value.toString());
        	if(county != null) {
        		attrs.addAttribute("", COUNTRY, COUNTRY, CDATA, county.getDisplayName());
        	}
        }
        attrs.addAttribute("", COUNTRY, COUNTRY, CDATA, value != null? mCountries.getPlaceForId(value.toString()).getDisplayName():"");
        startElement("", SHIPPING_ADDRESS, SHIPPING_ADDRESS, attrs);
        endElement("", SHIPPING_ADDRESS, SHIPPING_ADDRESS);
    }

    /**
     *
     * @throws SAXException
     */
    private void addBillingAddressInfo() throws SAXException {
        final AttributesImpl attrs = new AttributesImpl();

        Object value = mFacture.getPropertyValue(LNOM_FACTURE);
        attrs.addAttribute("", NAME, NAME, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_1_FACTURE);
        attrs.addAttribute("", STREET1, STREET1, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_2_FACTURE);
        attrs.addAttribute("", STREET2, STREET2, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LADRESSE_3_FACTURE);
        attrs.addAttribute("", STREET3, STREET3, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(CODE_POSTAL_FACTURE);
        attrs.addAttribute("", ZIP, ZIP, CDATA, value != null? value.toString():"");
        value = mFacture.getPropertyValue(LVILLE_FACTURE);
        attrs.addAttribute("", CITY, CITY, CDATA, " " + (value != null? value.toString():""));
        value = mFacture.getPropertyValue("CPAYS_FACTURE");
        if(value != null) {
        	CastPlace county = (CastPlace)mCountries.getPlaceForId(value.toString());
        	if(county != null) {
        		attrs.addAttribute("", COUNTRY, COUNTRY, CDATA, county.getDisplayName());
        	}
        }
        attrs.addAttribute("", COUNTRY, COUNTRY, CDATA, FRANCE);
        startElement("", BILLING_ADDRESS, BILLING_ADDRESS, attrs);
        endElement("", BILLING_ADDRESS, BILLING_ADDRESS);
    }

}
