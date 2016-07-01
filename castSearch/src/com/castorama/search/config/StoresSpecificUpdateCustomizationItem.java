package com.castorama.search.config;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import atg.search.adapter.customization.CustomizationItem;
import atg.search.adapter.customization.CustomizationType;
import atg.search.adapter.exception.AdapterException;
import atg.search.adapter.loader.AcquireSession;
import atg.search.adapter.loader.AcquireSessionStatus;
import atg.search.adapter.loader.LoaderCommandResponse;
import atg.search.adapter.loader.LoaderCommandResponseCode;
import atg.search.adapter.loader.command.LoadAuxiliaryData;

public class StoresSpecificUpdateCustomizationItem implements CustomizationItem{
    private StoresSpecificUpdateAdapter mAdapter = null;
    private String mId = "-1";
    private String mDisplayName = "Stores specific properties update customization item";
    private long mTimestamp = 0L;

    public StoresSpecificUpdateCustomizationItem(
            StoresSpecificUpdateAdapter pAdapter, String pId) {
        setAdapter(pAdapter);
        setId(pId);
    }

    public StoresSpecificUpdateAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(StoresSpecificUpdateAdapter pAdapter) {
        mAdapter = pAdapter;
    }

    public String getId() {
        return mId;
    }

    public void setId(String pId) {
        mId = pId;
    }

    public AcquireSessionStatus acquireCustomizations(AcquireSession pSession)
            throws AdapterException {

        LoadAuxiliaryData command = null;
        try {
            /* handle null pointer exceptions */
            if (getAdapter() == null)
                throw new AdapterException("Component property adapter isn't configured");
            if (getAdapter().getIndexedItemsGroup() == null)
                throw new AdapterException("IndexedItemsGroup is null.");
            if (getAdapter().getIndexedItemsGroup().getGroupMembers() == null )
                throw new AdapterException("IndexedItemsGroup doesn't contain any group members.");

            int qty = getAdapter().getIndexedItemsGroup().getGroupMembers().length;
            int startNum = 0;
            int batchSize = getAdapter().getBatchSize();
            int endNum = startNum + (qty > (batchSize + startNum) ? batchSize : qty);
            Map<String, Object> mapOfParameters = getAdapter().getMapOfParameters(getId());
            while (startNum < qty){ 
                String batchXMLAsString = getAdapter().getBatchXMLAsString(getId(), startNum, endNum, mapOfParameters);
                command = new LoadAuxiliaryData();
                command.getSpecificSearchEngineRequest().setFileContent(batchXMLAsString);
                LoaderCommandResponse response = pSession.processCommand(command);
                if (response.getResponseCode() != LoaderCommandResponseCode.EDone) {
                    throw new AdapterException("Command " + command + " failed");
                }
                startNum = endNum;
                endNum = (qty > (batchSize + startNum)?endNum + batchSize:qty);
            } 
            return (AcquireSessionStatus.EDone);
        } catch (SQLException e) {
            throw new AdapterException("Command " + command + " failed", e);
        }

    }

    public CustomizationType getCustomizationType() {
        return (CustomizationType.AUXILIARY_DATA);
    }

    public void setDisplayName(String pDisplayName) {
        mDisplayName = pDisplayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long pTimestamp) {
        mTimestamp = pTimestamp;
    }
}
