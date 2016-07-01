/**
 * 
 */
package com.castorama.integration.backoffice.exp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.castorama.integration.backoffice.exp.UtilFormat;

/**
 * @author Andrew_Logvinov
 */
class CCARecord {
    
    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private final long journalId;
    private final String magasinName;
    private final String magasinId;

    /**
     * 
     */
    public CCARecord(long journalId,String magasinId, String magasinName) {
        this.journalId = journalId;
        this.magasinName = magasinName;
        this.magasinId = magasinId;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("CCA|");
        
        UtilFormat.fillStart(sb, "" + journalId, 18, '0').append('|');
        UtilFormat.fillEnd(sb, df.format(new Date()), 20, ' ').append('|');
        UtilFormat.fillEnd(sb, magasinId, 4, ' ').append('|');
        UtilFormat.fillEnd(sb, magasinName, 32, ' ').append('|').append('\n');
        
        return sb.toString();
    }

}
