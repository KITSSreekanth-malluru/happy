package com.castorama.repository.search.indexing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import atg.search.routing.command.IncompleteCommandException;
import atg.search.routing.command.indexing.Metadata;
import atg.search.routing.command.indexing.UpdateDocument;
import atg.search.routing.utils.FastXMLModel;

/**
 * Extend CastUpdateDocument for overriding method getBodyXML for correct
 * handling metadata data for indexing.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastUpdateDocument extends UpdateDocument {
    /** castMetadata property */
    private Collection mCastMetadata = new ArrayList();

    /**
     * Returns castMetadata property.
     *
     * @return castMetadata property.
     */
    public Collection getCastMetadata() {
        return mCastMetadata;
    }

    /**
     * Add element to the castMetadata list
     *
     * @param pCastMeta parameter to add to the castMetadata list
     */
    public void addCastMetadata(Metadata pCastMeta) {
        mCastMetadata.add(pCastMeta);
    }

    /**
     * Sets castMetadata property
     *
     * @param pCastMetadata to set
     */
    public void setMetadata(Collection pCastMetadata) {
        mCastMetadata = pCastMetadata;
    }

    /**
     * Override method for correct handling metadata data.
     *
     * @return body of xml for indexing
     *
     * @throws IncompleteCommandException exception
     */
    @Override protected void getBodyXML(StringBuilder pStringBuilder) throws IncompleteCommandException {
        //String parentMetadata = 
    	super.getBodyXML(pStringBuilder);
        if (/*(parentMetadata != null) && */(getCastMetadata() != null) && !getCastMetadata().isEmpty()) {
            StringBuilder castCMD = pStringBuilder;//new StringBuilder(parentMetadata);
            castCMD.append("<MetaData>");
            for (Iterator iterator = mCastMetadata.iterator(); iterator.hasNext();) {
                Metadata metadata = (Metadata) iterator.next();

                castCMD.append("<meta");
                if (!atg.core.util.StringUtils.isBlank(metadata.getName())) {
                    castCMD.append(" ");
                    castCMD.append("name");
                    castCMD.append("=");
                    castCMD.append(FastXMLModel.encodeXML(metadata.getName()));

                } else {
                    throw new IncompleteCommandException(getRequestTag(), "meta");
                }
                if (!atg.core.util.StringUtils.isBlank(metadata.getContent())) {
                    castCMD.append(" ");
                    castCMD.append("content");
                    castCMD.append("=");
                    castCMD.append(FastXMLModel.encodeXML(metadata.getContent()));

                } else {
                    throw new IncompleteCommandException(getRequestTag(), "meta");
                }
                if (!atg.core.util.StringUtils.isBlank(metadata.getScheme())) {
                    castCMD.append(" ");
                    castCMD.append("scheme");
                    castCMD.append("=");
                    castCMD.append(FastXMLModel.encodeXML(metadata.getScheme()));

                }
                if (metadata.getMode() != null) {
                    castCMD.append(" ");
                    castCMD.append("mode");
                    castCMD.append("=");
                    castCMD.append(FastXMLModel.encodeXML(metadata.getMode().toString()));

                }

                castCMD.append("/>");
            }  // end for
            castCMD.append("</MetaData>");
            //return castCMD.toString();
        }  // end if
        //return parentMetadata;
    }
}
