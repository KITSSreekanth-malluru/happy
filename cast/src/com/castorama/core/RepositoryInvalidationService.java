package com.castorama.core;

import java.util.HashSet;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import atg.adapter.gsa.GSARepository;
import atg.dms.patchbay.MessageSource;
import atg.dms.patchbay.MessageSourceContext;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

/**
 */
public class RepositoryInvalidationService extends GenericService implements MessageSource {

    private MessageSourceContext mMessageSourceContext;
    private int mItemsThreshold = 1000;
    private boolean mStarted = false;
    private boolean mEnabled = true;

    public void invalidateRepository(Repository pRepository) {

        if (isEnabled()) {
            GSARepository repository = (GSARepository) pRepository;

            RepositoryInvalidationMessage rim = new RepositoryInvalidationMessage(repository.getAbsoluteName());
            final MessageSourceContext ctx = getMessageSourceContext();
            try {
                ObjectMessage msg = ctx.createObjectMessage("RepositoryInvalidate");
                msg.setObject(rim);
                msg.setJMSType("com.castorama.dms.InvalidateMessage");

                ctx.sendMessage("RepositoryInvalidate", msg);

            } catch (JMSException jmse) {
                if (isLoggingError()) {
                    vlogError(jmse, "Failed to send invalidation message for {0} repository", repository.getAbsoluteName());
                }
            }
        }

    }

    public void invalidateRepository(Repository pRepository, String pItemDescriptor, Set<String> pItems) {

        if (isEnabled()) {
            final GSARepository repository = (GSARepository) pRepository;
            final RepositoryInvalidationMessage rim = new RepositoryInvalidationMessage(repository.getAbsoluteName(),
                    pItemDescriptor, pItems);
            final MessageSourceContext ctx = getMessageSourceContext();
            try {
                final ObjectMessage msg = ctx.createObjectMessage("RepositoryInvalidate");
                msg.setObject(rim);
                msg.setJMSType("com.castorama.dms.InvalidateMessage");

                ctx.sendMessage("RepositoryInvalidate", msg);

            } catch (JMSException jmse) {
                if (isLoggingError()) {
                    vlogError(jmse, "Failed to send invalidation message for {0} repository", repository.getAbsoluteName());
                }
            }
        }

    }

    /**
     * TODO: Document Me !!!
     * 
     * @param pRepository
     * @param pItemDescriptor
     * @param pItems
     */
    public void invalidateRepository(Repository pRepository, String pItemDescriptor, RepositoryItem[] pItems) {

        if (pItems != null) {

            if (pItems.length < getItemsThreshold()) {

                final Set<String> itemIds = new HashSet<String>();

                for (RepositoryItem item : pItems) {
                    itemIds.add(item.getRepositoryId());
                }
                invalidateRepository(pRepository, pItemDescriptor, itemIds);

            } else {
                invalidateRepository(pRepository);
            }
        }

    }

    /**
     * @param pStarted
     *          the started to set
     */
    public void setStarted(boolean pStarted) {
        mStarted = pStarted;
    }

    /**
     * TODO: Document Me !!!
     * 
     * @see atg.dms.patchbay.MessageSource#setMessageSourceContext(atg.dms.patchbay.MessageSourceContext)
     */
    @Override
    public void setMessageSourceContext(MessageSourceContext pMessageSourceContext) {
        mMessageSourceContext = pMessageSourceContext;
    }

    /**
     * @return the messageSourceContext
     */
    public MessageSourceContext getMessageSourceContext() {
        return mMessageSourceContext;
    }

    /**
     * TODO: Document Me !!!
     * 
     * @see atg.dms.patchbay.MessageSource#startMessageSource()
     */
    @Override
    public void startMessageSource() {
        mStarted = true;
    }

    /**
     * TODO: Document Me !!!
     * 
     * @see atg.dms.patchbay.MessageSource#stopMessageSource()
     */
    @Override
    public void stopMessageSource() {
        mStarted = false;
    }

    /**
     * @return the started
     */
    public boolean isStarted() {
        return mStarted;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * @param pEnabled
     *          the enabled to set
     */
    public void setEnabled(boolean pEnabled) {
        mEnabled = pEnabled;
    }

    /**
     * @return the itemThreshold
     */
    public int getItemsThreshold() {
        return mItemsThreshold;
    }

    /**
     * @param pItemThreshold
     *          the itemThreshold to set
     */
    public void setItemsThreshold(int pItemsThreshold) {
        mItemsThreshold = pItemsThreshold;
    }

}
