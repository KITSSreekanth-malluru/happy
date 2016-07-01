package com.castorama.droplet;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import com.castorama.utils.HmacEncoder;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GenerateHmacDroplet extends DynamoServlet {

    public static final String PBX_HASH = "SHA512";
    public static final String PBX_SECRETKEY_PARAMETER = "PBX_SECRET_KEY";
    private static final ParameterName OUTPUT = ParameterName.getParameterName("output");

    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+1:00");
        TimeZone tz = TimeZone.getTimeZone("CET");
        df.setTimeZone(tz);
        Date date = new Date();
        return df.format(date);
    }

    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        String key = pRequest.getParameter(PBX_SECRETKEY_PARAMETER);
        String hMacParameter = null;
        String currentTime = getCurrentTime();
        String resultString = generateDataFromUrl(pRequest, currentTime);

        try {
            hMacParameter = HmacEncoder.calculateHmac(resultString, key);
        } catch (InvalidKeyException e) {
            if (isLoggingError()) {
                logInfo("Invalid key exception");
            }
        } catch (NoSuchAlgorithmException e) {
            if (isLoggingError()) {
                logInfo("Invalid key exception");
            }
        }
        pRequest.setParameter("PBX_HMAC", hMacParameter);
        pRequest.setParameter("PBX_HASH", PBX_HASH);
        pRequest.setParameter("PBX_TIME", currentTime);
        pRequest.serviceParameter(OUTPUT, pRequest, pResponse);

    }

    public String generateDataFromUrl(DynamoHttpServletRequest pRequest, String currentTime) {
        String pbx_site = pRequest.getParameter("PBX_SITE");
        String pbx_rang = pRequest.getParameter("PBX_RANG");
        String pbx_total = pRequest.getParameter("PBX_TOTAL");
        String pbx_devise = pRequest.getParameter("PBX_DEVICE");
        String pbx_cmd = pRequest.getParameter("PBX_CMD");
        String pbx_porteur = pRequest.getParameter("PBX_PORTEUR");
        String pbx_annule = pRequest.getParameter("PBX_ANNULE");
        String pbx_effectue = pRequest.getParameter("PBX_EFFECTUE");
        String pbx_refuse = pRequest.getParameter("PBX_REFUSE");
        String pbx_retour = pRequest.getParameter("PBX_RETOUR");
        String pbx_repondre_a = pRequest.getParameter("PBX_REPONDRE_A");
        String pbx_typepaiment = pRequest.getParameter("PBX_TYPEPAIMENT");
        String pbx_typecarte = pRequest.getParameter("PBX_TYPECARTE");
        String pbx_langue = pRequest.getParameter("PBX_LANGUE");
        String pbx_identifiant = pRequest.getParameter("PBX_IDENTIFIANT");
        String pbx_nbcarteskdo = pRequest.getParameter("PBX_NBCARTESKDO");
        String pbx_codefamille = pRequest.getParameter("PBX_CODEFAMILLE");
        StringBuilder resultUrl = new StringBuilder();
        if (pbx_site != null) {
            resultUrl.append("PBX_SITE=").append(pbx_site).append("&");
        }
        if (pbx_rang != null) {
            resultUrl.append("PBX_RANG=").append(pbx_rang).append("&");
        }
        if (pbx_total != null) {
            resultUrl.append("PBX_TOTAL=").append(pbx_total).append("&");
        }
        if (pbx_devise != null) {
            resultUrl.append("PBX_DEVISE=").append(pbx_devise).append("&");
        }
        if (pbx_cmd != null) {
            resultUrl.append("PBX_CMD=").append(pbx_cmd).append("&");
        }
        if (pbx_porteur != null) {
            resultUrl.append("PBX_PORTEUR=").append(pbx_porteur).append("&");
        }
        if (pbx_annule != null) {
            resultUrl.append("PBX_ANNULE=").append(pbx_annule).append("&");
        }
        if (pbx_effectue != null) {
            resultUrl.append("PBX_EFFECTUE=").append(pbx_effectue).append("&");
        }
        if (pbx_refuse != null) {
            resultUrl.append("PBX_REFUSE=").append(pbx_refuse).append("&");
        }
        if (pbx_retour != null) {
            resultUrl.append("PBX_RETOUR=").append(pbx_retour).append("&");
        }
        if (pbx_repondre_a != null) {
            resultUrl.append("PBX_REPONDRE_A=").append(pbx_repondre_a).append("&");
        }
        if (pbx_typepaiment != null) {
            resultUrl.append("PBX_TYPEPAIEMENT=").append(pbx_typepaiment).append("&");
        }
        if (pbx_typecarte != null) {
            resultUrl.append("PBX_TYPECARTE=").append(pbx_typecarte).append("&");
        }
        if (pbx_typecarte != null) {
            resultUrl.append("PBX_LANGUE=").append(pbx_langue).append("&");
        }
        if (pbx_identifiant != null) {
            resultUrl.append("PBX_IDENTIFIANT=").append(pbx_identifiant).append("&");
        }
        if (pbx_nbcarteskdo != null) {
            resultUrl.append("PBX_NBCARTESKDO=").append(pbx_nbcarteskdo).append("&");
        }
        if (pbx_codefamille != null) {
            resultUrl.append("PBX_CODEFAMILLE=").append(pbx_codefamille).append("&");
        }
        resultUrl.append("PBX_HASH=").append(PBX_HASH).append("&PBX_TIME=").append(currentTime);

        return resultUrl.toString();
    }

}
