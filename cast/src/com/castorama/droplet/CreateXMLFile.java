package com.castorama.droplet;

import java.io.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Create xml file to manage price of the SKU on the jsp.
 *
 * @author Alena_Karpenkava
 *
 */
public class CreateXMLFile extends DynamoServlet {


    /** Output parameters */
    static final ParameterName OUTPUT = ParameterName.getParameterName("output");
    static final String OUT = "out";

    /** Input parameters */

    static final String SKU_ID = "skuId";
    static final String SKU_NAME = "skuName";
    static final String TYPE_ARTICLE = "typeArticle";
    static final String DESCRIPTION = "libelleClientLong";
    static final String DESC_EN = "libelleEspaceNouveaute";
    static final String MENTIONS = "mentionsLegalesObligatoires";
    static final String GARANTIE = "garantie";
    static final String RESTRICTIONS = "restrictionsUsage";
    static final String CUV_SKU = "CUV";
    static final String CUP_SKU = "CUP";
    static final String PUPUV_SKU = "PUPUV";
    static final String STOCK_LEVEL = "stockLevel";
    static final String SALE_PRICE = "salePrice";
    static final String LIST_PRICE = "listPrice";
    static final String CARD_PRICE = "cardPrice";
    static final String SHOWM2FIRST = "showM2PriceFirst";
    static final String PRICE_PER_UNITE = "pricePerUnite";
    static final String PRODUCT_NAME = "productName";
    static final String PRODUCT_BENEFIT = "productBenefit";
    static final String PRODUCT_PROMO_DESCRIPTION = "productPromoDescription";
    static final String PROMO_EXPIRATION_DATE = "promoExpirationDate";
    static final String PARENT_PRODUCT_IDS = "parentProductIds";
    static final String DISPLAY_DISCOUNT = "displayDiscount";

    static final String CATS = "cats";
    static final String CAT_IDS = "defCatIds";
    static final String CAT_ID = "defCatId";
    static final String CAT_NAMES = "catNames";
    static final String CAT_NAME = "catName";

    static final String IMGS = "images";
    static final String IMG_AUX = "auxilaryMedia";
    static final String IMG_SKU_MIN = "skuMiniature";
    static final String IMG_SKU_THUMB = "skuThumbnail";
    static final String IMG_SKU_CAR = "skuCarousel";
    static final String IMG_SKU_S = "skuSmall";
    static final String IMG_SKU_L = "skuLarge";
    static final String IMG_SKU_SUP = "skuSupporting";
    static final String IMG_SKU_AUXS = "skuAuxilariesMedias";
    static final String IMG_SKU_COMP = "skuComparator";
    static final String IMG_PROD_THUMB = "productThumbnail";
    static final String IMG_PROD_S = "productSmall";
    static final String IMG_PROD_L = "productLarge";
    static final String IMG_PROD_AUXS = "productAuxilariesMedias";

    /** Constants */
    static final String ID = "id";
    static final String SKU = "sku";
    static final String URL = "url";
    static final String PRODUCT_ID = "productId";
    static final String DISPLAY_NAME = "displayName";

    static final String TRANSFORMER_EXCEPTION = "TransformerException";
    static final String PARSER_CONFIGURATION_EXCEPTION = "ParserConfigurationException";

    /**
     * Create xml file to manage attributes of the SKU on the jsp.
     *
     * @param pRequest -
     *            request
     * @param pResponse -
     *            response
     * @throws IOException
     *
     * @throws ServletException
     */
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        pResponse.setDateHeader("Expires", 0);
        pResponse.setDateHeader("Last-Modified", new Date().getTime());
        pResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        pResponse.setHeader("Pragma", "no-cache");
        pResponse.setContentType("text/xml");

        String skuId = (String) pRequest.getObjectParameter(SKU_ID);
        Long stockLevel = (Long) pRequest.getObjectParameter(STOCK_LEVEL);
        String productName = (String) pRequest.getObjectParameter(PRODUCT_NAME);
        String productBenefit = (String) pRequest.getObjectParameter(PRODUCT_BENEFIT);
        String productPromoDescription = (String) pRequest.getObjectParameter(PRODUCT_PROMO_DESCRIPTION);
        Date promoExpirationDate = (Date) pRequest.getObjectParameter(PROMO_EXPIRATION_DATE);
        String CUP = (String) pRequest.getObjectParameter(CUP_SKU);
        Float PUPUV = (Float) pRequest.getObjectParameter(PUPUV_SKU);
        String CUV = (String) pRequest.getObjectParameter(CUV_SKU);
        String skuName = (String) pRequest.getObjectParameter(SKU_NAME);
        Integer typeArticle = (Integer) pRequest.getObjectParameter(TYPE_ARTICLE);
        String description = (String) pRequest.getObjectParameter(DESCRIPTION);
        String libelleEspaceNouveaute = (String) pRequest.getObjectParameter(DESC_EN);
        String mentionsLegalesObligatoires = (String) pRequest.getObjectParameter(MENTIONS);
        String garantie = (String) pRequest.getObjectParameter(GARANTIE);
        String restrictionsUsage = (String) pRequest.getObjectParameter(RESTRICTIONS);
        String listPrice = (String) pRequest.getObjectParameter(LIST_PRICE);
        String salePrice = (String) pRequest.getObjectParameter(SALE_PRICE);
        Object reqPrice = pRequest.getObjectParameter(CARD_PRICE);
        String cardPrice = "";
        if (reqPrice != null) {
            cardPrice = reqPrice.toString();
        }
        Boolean showM2PriceFirst = (Boolean) pRequest.getObjectParameter(SHOWM2FIRST);
        String pricePerUnite = (String) pRequest.getObjectParameter(PRICE_PER_UNITE);
        List parentProductIds = (List) pRequest.getObjectParameter(PARENT_PRODUCT_IDS);
        Boolean displayDiscount = (Boolean) pRequest.getObjectParameter (DISPLAY_DISCOUNT);

        List cats = (List) pRequest.getObjectParameter(CATS);

        String skuMiniature = (String) pRequest.getObjectParameter(IMG_SKU_MIN);
        String skuThumbnail = (String) pRequest.getObjectParameter(IMG_SKU_THUMB);
        String skuCarousel = (String) pRequest.getObjectParameter(IMG_SKU_CAR);
        String skuSmall = (String) pRequest.getObjectParameter(IMG_SKU_S);
        String skuLarge = (String) pRequest.getObjectParameter(IMG_SKU_L);
        String skuSupporting = (String) pRequest.getObjectParameter(IMG_SKU_SUP);
        Map skuAuxilariesMedias = (Map) pRequest.getObjectParameter(IMG_SKU_AUXS);
        String skuComparator = (String) pRequest.getObjectParameter(IMG_SKU_COMP);
        String productThumbnail = (String) pRequest.getObjectParameter(IMG_PROD_THUMB);
        String productSmall = (String) pRequest.getObjectParameter(IMG_PROD_S);
        String productLarge = (String) pRequest.getObjectParameter(IMG_PROD_L);
        Map productAuxilariesMedias = (Map) pRequest.getObjectParameter(IMG_PROD_AUXS);

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // sku element
            Element skuElement = document.createElement(SKU);
            // creates an Attribute of sku element as type and sets its value to String
            skuElement.setAttribute(new String(ID), skuId);
            // stockLevel element
            Element stockLevelElement = document.createElement(STOCK_LEVEL);
            // productName element
            Element productNameElement = document.createElement(PRODUCT_NAME);
            // productBenefit element
            Element productBenefitElement = document.createElement(PRODUCT_BENEFIT);
            // productPromoDescription element
            Element productPromoDescriptionElement = document.createElement(PRODUCT_PROMO_DESCRIPTION);
            // promoExpirationDate element
            Element promoExpirationDateElement = document.createElement(PROMO_EXPIRATION_DATE);
            // CUP element
            Element CUPElement = document.createElement(CUP_SKU);
            // PUPUV element
            Element PUPUVElement = document.createElement(PUPUV_SKU);
            // CUV element
            Element CUVElement = document.createElement(CUV_SKU);
            // skuName element
            Element skuNameElement = document.createElement(SKU_NAME);
            // typeArticle element
            Element typeArticleElement = document.createElement(TYPE_ARTICLE);
            // description element
            Element descriptionElement = document.createElement(DESCRIPTION);
            // libelleEspaceNouveaute element
            Element libelleEspaceNouveauteElement = document.createElement(DESC_EN);
            // mentionsLegalesObligatoires element
            Element mentionsLegalesObligatoiresElement = document.createElement(MENTIONS);
            // garantie element
            Element garantieElement = document.createElement(GARANTIE);
            // restrictionsUsage element
            Element restrictionsUsageElement = document.createElement(RESTRICTIONS);
            // listPrice element
            Element listPriceElement = document.createElement(LIST_PRICE);
            // salePrice element
            Element salePriceElement = document.createElement(SALE_PRICE);
            // cardPrice element
            Element cardPriceElement = document.createElement(CARD_PRICE);
            // showM2PriceFirst element
            Element showM2PriceFirstElement = document.createElement(SHOWM2FIRST);
            // pricePerUnite element
            Element pricePerUniteElement = document.createElement(PRICE_PER_UNITE);
            // parentProductIds element
            Element parentProductIdsElement = document.createElement(PARENT_PRODUCT_IDS);
            // displayDiscount element
            Element displayDiscountElement = document.createElement(DISPLAY_DISCOUNT);

            // catIds element
            Element catIdsElement = document.createElement(CAT_IDS);
            // catNames element
            Element catNamesElement = document.createElement(CAT_NAMES);

            // imagesElement element
            Element imagesElement = document.createElement(IMGS);
            // skuMiniature element
            Element skuMiniatureElement = document.createElement(IMG_SKU_MIN);
            // skuThumbnail element
            Element skuThumbnailElement = document.createElement(IMG_SKU_THUMB);
            // skuCarousel element
            Element skuCarouselElement = document.createElement(IMG_SKU_CAR);
            // skuSmall element
            Element skuSmallElement = document.createElement(IMG_SKU_S);
            // skuLarge element
            Element skuLargeElement = document.createElement(IMG_SKU_L);
            // skuSupporting element
            Element skuSupportingElement = document.createElement(IMG_SKU_SUP);
            // productAuxilariesMedias element
            Element productAuxilariesMediasElement = document.createElement(IMG_PROD_AUXS);
            // skuComparator element
            Element skuComparatorElement = document.createElement(IMG_SKU_COMP);
            // productThumbnail element
            Element productThumbnailElement = document.createElement(IMG_PROD_THUMB);
            // productSmall element
            Element productSmallElement = document.createElement(IMG_PROD_S);
            // productLarge element
            Element productLargeElement = document.createElement(IMG_PROD_L);
            // skuAuxilariesMedias element
            Element skuAuxilariesMediasElement = document.createElement(IMG_SKU_AUXS);

            if (stockLevel != null) {
                // stockLevel element node value
                stockLevelElement.appendChild(document.createTextNode(stockLevel.toString()));
            }
            if (productName != null) {
                // productName element node value
                productNameElement.appendChild(document.createTextNode(productName));
            }
            if (productBenefit != null) {
                // productBenefit element node value
                productBenefitElement.appendChild(document.createTextNode(productBenefit));
            }
            if (productPromoDescription != null) {
                // productPromoDescription element node value
                productPromoDescriptionElement.appendChild(document.createTextNode(productPromoDescription));
            }
            if (promoExpirationDate != null) {
                // promoExpirationDate element node value
                promoExpirationDateElement.appendChild(document.createTextNode(promoExpirationDate.toString()));
            }
            if (CUP != null) {
                // CUP element node value
                CUPElement.appendChild(document.createTextNode(CUP));
            }
            if (PUPUV != null) {
                // PUPUV element node value
                PUPUVElement.appendChild(document.createTextNode(PUPUV.toString()));
            }
            if (CUV != null) {
                // CUV element node value
                CUVElement.appendChild(document.createTextNode(CUV));
            }
            if (skuName != null) {
                // skuName element node value
                skuNameElement.appendChild(document.createTextNode(skuName));
            }
            if (typeArticle != null) {
                // typeArticle element node value
                typeArticleElement.appendChild(document.createTextNode(typeArticle.toString()));
            }
            if (description != null) {
                // description element node value
                descriptionElement.appendChild(document.createTextNode(description));
            }
            if (libelleEspaceNouveaute != null) {
                // description element node value
                libelleEspaceNouveauteElement.appendChild(document.createTextNode(libelleEspaceNouveaute));
            }
            if (mentionsLegalesObligatoires != null) {
                // description element node value
                mentionsLegalesObligatoiresElement.appendChild(document.createTextNode(mentionsLegalesObligatoires));
            }
            if (garantie != null) {
                // description element node value
                garantieElement.appendChild(document.createTextNode(garantie));
            }
            if (restrictionsUsage != null) {
                // description element node value
                restrictionsUsageElement.appendChild(document.createTextNode(restrictionsUsage));
            }
            if (listPrice != null) {
                // listPrice element node value
                listPriceElement.appendChild(document.createTextNode(listPrice.toString()));
            }
            if (salePrice != null) {
                // salePrice element node value
                salePriceElement.appendChild(document.createTextNode(salePrice.toString()));
            }
            if (cardPrice != null) {
                // cardPrice element node value
                cardPriceElement.appendChild(document.createTextNode(cardPrice.toString()));
            }
            if (showM2PriceFirst != null) {
                // showM2PriceFirst element node value
                showM2PriceFirstElement.appendChild(document.createTextNode(showM2PriceFirst.toString()));
            }
            if (pricePerUnite != null) {
                // pricePerUnite element node value
                pricePerUniteElement.appendChild(document.createTextNode(pricePerUnite.toString()));
            }
            if (displayDiscount != null){
                displayDiscountElement.appendChild(document.createTextNode(displayDiscount.toString()));
            }
            if (parentProductIds != null) {
                // parentProductIds element node value
                Iterator iterator = parentProductIds.iterator();
                while(iterator.hasNext()){
                    RepositoryItem product = (RepositoryItem)iterator.next();
                    String productId = (String)product.getPropertyValue(ID);
                    // productId element
                    Element productIdElement = document.createElement(PRODUCT_ID);
                    productIdElement.appendChild(document.createTextNode(productId));
                    parentProductIdsElement.appendChild(document.appendChild(productIdElement));
                }
            }

            if (skuAuxilariesMedias != null) {
                // skuAuxilariesMedias element node value
                for (RepositoryItem media : (Collection<RepositoryItem>) skuAuxilariesMedias.values()) {
                    String url = (String) media.getPropertyValue(URL);
                    Element auxilaryMediaElement = document.createElement(IMG_AUX);
                    auxilaryMediaElement.appendChild(document.createTextNode(url));
                    skuAuxilariesMediasElement.appendChild(document.appendChild(auxilaryMediaElement));
                }
            }
            if (productAuxilariesMedias != null) {
                // productAuxilariesMedias element node value
                for (RepositoryItem media : (Collection<RepositoryItem>) productAuxilariesMedias.values()) {
                    String url = (String) media.getPropertyValue(URL);
                    Element auxilaryMediaElement = document.createElement(IMG_AUX);
                    auxilaryMediaElement.appendChild(document.createTextNode(url));
                    productAuxilariesMediasElement.appendChild(document.appendChild(auxilaryMediaElement));
                }
            }

            if (cats != null) {
                Iterator iterator = cats.iterator();
                while(iterator.hasNext()){
                    RepositoryItem cat = (RepositoryItem)iterator.next();

                    String catId = (String) cat.getPropertyValue(ID);
                    Element catIdElement = document.createElement(CAT_ID);
                    catIdElement.appendChild(document.createTextNode(catId));
                    catIdsElement.appendChild(document.appendChild(catIdElement));

                    String catName = (String) cat.getPropertyValue(DISPLAY_NAME);
                    Element catNameElement = document.createElement(CAT_NAME);
                    catNameElement.appendChild(document.createTextNode(catName));
                    catNamesElement.appendChild(document.appendChild(catNameElement));
                }
            }

            if (skuMiniature != null) {
                // skuMiniature element node value
                skuMiniatureElement.appendChild(document.createTextNode(skuMiniature));
            }
            imagesElement.appendChild(skuMiniatureElement);
            if (skuThumbnail != null) {
                // skuThumbnail element node value
                skuThumbnailElement.appendChild(document.createTextNode(skuThumbnail));
            }
            imagesElement.appendChild(skuThumbnailElement);
            if (skuCarousel != null) {
                // skuCarousel element node value
                skuCarouselElement.appendChild(document.createTextNode(skuCarousel));
            }
            imagesElement.appendChild(skuCarouselElement);
            if (skuSmall != null) {
                // skuSmall element node value
                skuSmallElement.appendChild(document.createTextNode(skuSmall));
            }
            imagesElement.appendChild(skuSmallElement);
            if (skuLarge != null) {
                // skuLarge element node value
                skuLargeElement.appendChild(document.createTextNode(skuLarge));
            }
            imagesElement.appendChild(skuLargeElement);
            if (skuSupporting != null) {
                // skuSupporting element node value
                skuSupportingElement.appendChild(document.createTextNode(skuSupporting));
            }
            imagesElement.appendChild(skuSupportingElement);
            // skuAuxilariesMedias element node value
            imagesElement.appendChild(skuAuxilariesMediasElement);
            if (skuComparator != null) {
                // skuComparator element node value
                skuComparatorElement.appendChild(document.createTextNode(skuComparator));
            }
            imagesElement.appendChild(skuComparatorElement);
            if (productThumbnail != null) {
                // productThumbnail element node value
                productThumbnailElement.appendChild(document.createTextNode(productThumbnail));
            }
            imagesElement.appendChild(productThumbnailElement);
            if (productSmall != null) {
                // productSmall element node value
                productSmallElement.appendChild(document.createTextNode(productSmall));
            }
            imagesElement.appendChild(productSmallElement);
            if (productLarge != null) {
                // productLarge element node value
                productLargeElement.appendChild(document.createTextNode(productLarge));
            }
            imagesElement.appendChild(productLargeElement);
            // productAuxilariesMedias element node value
            imagesElement.appendChild(productAuxilariesMediasElement);

            // add stockLevel element under skuElement
            skuElement.appendChild(stockLevelElement);
            // add productName element under skuElement
            skuElement.appendChild(productNameElement);
            // add productBenefit element under skuElement
            skuElement.appendChild(productBenefitElement);
            // add productPromoDescription element under skuElement
            skuElement.appendChild(productPromoDescriptionElement);
            // add promoExpirationDate element under skuElement
            skuElement.appendChild(promoExpirationDateElement);
            // add CUP element under skuElement
            skuElement.appendChild(CUPElement);
            // add PUPUV element under skuElement
            skuElement.appendChild(PUPUVElement);
            // add CUV element under skuElement
            skuElement.appendChild(CUVElement);
            // add skuName element under skuElement
            skuElement.appendChild(skuNameElement);
            // add typeArticle element under skuElement
            skuElement.appendChild(typeArticleElement);
            // add description element under skuElement
            skuElement.appendChild(descriptionElement);
            // add libelleEspaceNouveaute element under skuElement
            skuElement.appendChild(libelleEspaceNouveauteElement);
            // add mentionsLegalesObligatoires element under skuElement
            skuElement.appendChild(mentionsLegalesObligatoiresElement);
            // add garantie element under skuElement
            skuElement.appendChild(garantieElement);
            // add restrictionsUsage element under skuElement
            skuElement.appendChild(restrictionsUsageElement);
            // add listPrice element under skuElement
            skuElement.appendChild(listPriceElement);
            // add salePrice element under skuElement
            skuElement.appendChild(salePriceElement);
            // add cardPrice element under skuElement
            skuElement.appendChild(cardPriceElement);
            // add showM2PriceFirst element under skuElement
            skuElement.appendChild(showM2PriceFirstElement);
            // add pricePerUnite element under skuElement
            skuElement.appendChild(pricePerUniteElement);
            // add displayDiscount element under skuElement
            skuElement.appendChild(displayDiscountElement);
            // add parentProductIds element under skuElement
            skuElement.appendChild(parentProductIdsElement);
            // add parentProductIds element under skuElement
            skuElement.appendChild(catIdsElement);
            // add parentProductIds element under skuElement
            skuElement.appendChild(catNamesElement);
            // add images element under skuElement
            skuElement.appendChild(imagesElement);
            // add the rootElement to the document
            document.appendChild(skuElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            String writerStr = writer.toString();
            pRequest.setParameter(OUT, writerStr.substring(writerStr.indexOf("<sku id=\""+skuId+"\">")));

        } catch (TransformerException ex) {
            logError(TRANSFORMER_EXCEPTION, ex);
        } catch (ParserConfigurationException ex) {
            logError(PARSER_CONFIGURATION_EXCEPTION, ex);
        }

        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
}
