package com.castorama.searchadmin.adminui.navigation;

import java.util.Map;

import atg.searchadmin.adminui.navigation.SourceTypeComponent;

/**
 * Determine SourceTypeComponent for castorama document content adapter
 *
 * @author Katsiaryna Sharstsiuk
 */
public class CastoramaDocumentSourceComponent extends SourceTypeComponent {
    /** IS_LOCAL constant. */
    public static final String IS_LOCAL = "IsLocal";

    /** HOST_MACHINE constant. */
    public static final String HOST_MACHINE = "HostMachine";

    /** PORT constant. */
    public static final String PORT = "Port";

    /** PATH constant. */
    public static final String PATH = "Path";

    /** CastoramaCatalogDocumentScanner constant. */
    public static final String CDS_PATH = "CatalogDocumentScanner";

    /** Local. */
    public static final String LOCAL = "Local";

    /**
     * Returns display name for content item.
     *
     * @param  pSettings parameter
     *
     * @return displayName for content item.
     */
    @Override public String getDisplayName(Map pSettings) {
        return getSourceTypeInternalName() + " - " +
               (pSettings.get(getSourceTypeInternalName() + HOST_MACHINE) + ":" +
                pSettings.get(getSourceTypeInternalName() + PORT)) + " - " +
               pSettings.get(getSourceTypeInternalName() + PATH);
    }

    /**
     * Returns localized display name for content item.
     *
     * @param  pSettings      parameter
     * @param  pLocalizedName parameter
     *
     * @return localized display name for content item.
     */
    @Override public String getLocalizedDisplayName(Map pSettings, String pLocalizedName) {
        return pLocalizedName + " - " +
               (pSettings.get(getSourceTypeInternalName() + HOST_MACHINE) + ":" +
                pSettings.get(getSourceTypeInternalName() + PORT)) + " - " +
               pSettings.get(getSourceTypeInternalName() + PATH);
    }

    /**
     * Load settings for content
     *
     * @param pProps    parameter
     * @param pSettings parameter
     */
    @Override public void loadSettings(Map pProps, Map pSettings) {
        pSettings.put(getSourceTypeInternalName() + PATH, pProps.get(CDS_PATH));
        pSettings.put(IS_LOCAL + getSourceTypeInternalName(), pProps.get(LOCAL));
        pSettings.put(getSourceTypeInternalName() + HOST_MACHINE, pProps.get(HOST_MACHINE));
        pSettings.put(getSourceTypeInternalName() + PORT, pProps.get(PORT));
    }

    /**
     * Save settings for content
     *
     * @param pProps    parameter
     * @param pSettings parameter
     */
    @Override public void saveSettings(Map pProps, Map pSettings) {
        pProps.put("$class", "com.castorama.searchadmin.adapter.content.impl.CastoramaDocumentAdapter");
        pProps.put(CDS_PATH, pSettings.get(getSourceTypeInternalName() + PATH));
        pProps.put(PORT, pSettings.get(getSourceTypeInternalName() + PORT));
        pProps.put(HOST_MACHINE, pSettings.get(getSourceTypeInternalName() + HOST_MACHINE));
    }

}
