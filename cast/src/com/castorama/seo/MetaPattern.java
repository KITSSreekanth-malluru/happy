package com.castorama.seo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrei_Raichonak
 */
public class MetaPattern {
    /** formattedMsg property. */
    private String mFormattedMsg;

    /** metaParameters property. */
    private MetaParameter[] mMetaParameters;

    /**
     * Creates a new MetaPattern object.
     *
     * @param pFormattedMsg   parameter
     * @param pMetaParameters parameter
     */
    private MetaPattern(String pFormattedMsg, MetaParameter[] pMetaParameters) {
        mFormattedMsg = pFormattedMsg;
        mMetaParameters = pMetaParameters;
    }

    /**
     * Returns formattedMsg property.
     *
     * @return formattedMsg property.
     */
    public String getFormattedMsg() {
        return mFormattedMsg;
    }

    /**
     * Returns metaParameters property.
     *
     * @return metaParameters property.
     */
    public MetaParameter[] getMetaParameters() {
        return mMetaParameters;
    }

    /**
     *
     * @param  pTemplateFormat parameter
     *
     * @return 
     */
    public static MetaPattern compile(String pTemplateFormat) {
        String format = pTemplateFormat;
        List<MetaParameter> args = new ArrayList<MetaParameter>();
        StringBuffer result = new StringBuffer(format.length());
        char[] formatArr = format.toCharArray();
        int index = 0;
        char c = ' ';
        MetaParameter param = null;
        char endChar = '}';

        boolean isNamePart = true;

        while (index < formatArr.length) {
            c = formatArr[index++];

            if (c == '{') {
                endChar = '}';
            } else {
                result.append(c);
                continue;
            }

            result.append('{');

            isNamePart = true;
            param = null;
            StringBuffer paramBuffer = new StringBuffer();
            String paramPart = null;
            while (true) {
                if (index >= format.length()) {
                    result.append(paramBuffer.toString());
                    break;
                }

                c = formatArr[index++];
                if ((c == endChar) || (c == ',')) {
                    paramPart = paramBuffer.toString()  /* .trim() */;
                    if (isNamePart) {
                        result.append(args.size());
                        param = new MetaParameter(paramPart);
                        args.add(param);
                        isNamePart = false;
                    } else {
                        if (c == ',') {
                            param.setSeparator(',');
                        } else {
                            if (paramPart.startsWith("sep=")) {
                                param.setSeparator(paramPart.toString().charAt(paramPart.toString().length() - 1));
                            }
                        }
                    }
                    if (c == endChar) {
                        result.append("}");
                        break;
                    }
                    paramBuffer = new StringBuffer();
                } else {
                    paramBuffer.append(c);
                }  // end if-else
            }
        }

        // Create array from list
        MetaParameter[] metaParameters = (MetaParameter[]) args.toArray(new MetaParameter[0]);

        return new MetaPattern(result.toString(), metaParameters);
    }
}
