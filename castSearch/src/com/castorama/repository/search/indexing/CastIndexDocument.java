package com.castorama.repository.search.indexing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import atg.search.routing.command.IncompleteCommandException;
import atg.search.routing.command.indexing.IndexDocument;
import atg.search.routing.command.indexing.Metadata;
import atg.search.routing.utils.FastXMLModel;

/**
 * Extend IndexDocument for overriding method getBodyXML for correct handling
 * metadata data for indexing.
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastIndexDocument extends IndexDocument {
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
     * Sets the value of the castMetadata property.
     *
     * @param pCastMetadata parameter to set.
     */
    public void setCastMetadata(Collection pCastMetadata) {
        mCastMetadata = pCastMetadata;
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
     * Override method for correct handling metadata data.
     *
     * @return body of xml for indexing
     *
     * @throws IncompleteCommandException exception
     */
    @Override protected void getBodyXML(StringBuilder pStringBuilder) throws IncompleteCommandException {
        //String parentMetadata = super.getBodyXML(pStringBuilder);
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
