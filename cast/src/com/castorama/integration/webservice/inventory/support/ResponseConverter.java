package com.castorama.integration.webservice.inventory.support;

import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import com.castorama.integration.webservice.Converter;
import com.castorama.integration.webservice.inventory.exception.NotImplementedException;
import com.castorama.integration.webservice.inventory.message.InventoryResponseMessage;
import com.castorama.integration.webservice.inventory.message.InventoryUnit;
import com.castorama.integration.webservice.inventory.model.WebServiceConfiguration;
import com.castorama.integration.webservice.inventory.model.WebServiceConstants;
import com.castorama.integration.webservice.inventory.model.xml.Envelope;
import com.castorama.integration.webservice.inventory.model.xml.InventoryBalance;
import com.castorama.integration.webservice.inventory.model.xml.ShowInventoryBalance;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author EPAM team
 */
public class ResponseConverter implements Converter<InventoryResponseMessage, String> {

    //region Constants
    private static final String STRING_ENCODING = "UTF-8";
    //endregion

    //region Dependencies
    private WebServiceConfiguration configuration;
    //endregion

    @Override
    public InventoryResponseMessage convertToMessage(String xmlString) throws Exception {
        InputStream inputStream = null;
        Envelope customerInput = null;
        try {
            inputStream = new ByteArrayInputStream(xmlString.getBytes(STRING_ENCODING));
            customerInput = (Envelope) getUnmarshaller().unmarshal(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return convertEnvelopeToMessage(customerInput);
    }

    @Override
    public String convertFromMessage(InventoryResponseMessage inventoryResponseMessage) {
        throw new NotImplementedException();
    }

    /**
     * Return xml unmarshaller
     *
     * @return xml unmarshaller
     * @throws javax.xml.bind.JAXBException
     */
    private Unmarshaller getUnmarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        return jaxbContext.createUnmarshaller();
    }

    /**
     * Convert Envelope to inventory web service response message
     *
     * @param envelope web service response Envelope object
     * @return InventoryResponseMessage
     */
    private InventoryResponseMessage convertEnvelopeToMessage(Envelope envelope) throws ParseException {
        SimpleDateFormat webServiceSimpleDateFormat =
                webServiceSimpleDateFormat = new SimpleDateFormat(configuration.getResponseWebServiceDateFormat());

        ShowInventoryBalance balance = envelope.getBody().getShowInventoryBalance();

        String languageCode = balance.getLanguageCode();
        String dateString = balance.getApplicationArea().getCreationDateTime();
        Date creationDate = webServiceSimpleDateFormat.parse(dateString);
        int storeId = balance.getDataArea().getFulfilmentSiteID();
        String bodId = balance.getApplicationArea().getBodId();

        List<InventoryUnit> unitList = new LinkedList<InventoryUnit>();

        for (InventoryBalance inventoryBalance : balance.getDataArea().getInventoryBalances()) {
            Integer codeArticle = inventoryBalance.getProductItem().getId();
            Integer quantity = inventoryBalance.getAvailableQuantity();
            unitList.add(new InventoryUnit(codeArticle, quantity));
        }

        InventoryResponseMessage responseMessage = new InventoryResponseMessage(
                languageCode, creationDate, storeId, bodId, unitList
        );

        return responseMessage;
    }

    //region Getters/Setters
    public WebServiceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(WebServiceConfiguration configuration) {
        this.configuration = configuration;
    }
    //endregion
}
