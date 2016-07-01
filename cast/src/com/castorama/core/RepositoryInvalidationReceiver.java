package com.castorama.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceMap;
import atg.repository.ItemDescriptorImpl;
import atg.repository.RepositoryException;

/**
 */
public class RepositoryInvalidationReceiver extends GenericService implements MessageSink {

    private ServiceMap mRepositoryPathMethodInvalidatorService;
    private int mInvalidationThreadshold = 1000;

    /**
     * Process repository invalidation messages.
     * 
     * @see atg.dms.patchbay.MessageSink#receiveMessage(java.lang.String, javax.jms.Message)
     */
    @Override
    public void receiveMessage(final String pPortName, final Message pMessage) throws JMSException {

        if ((pMessage instanceof ObjectMessage)
                && (((ObjectMessage) pMessage).getObject() instanceof RepositoryInvalidationMessage)) {
            final RepositoryInvalidationMessage rim = (RepositoryInvalidationMessage) ((ObjectMessage) pMessage).getObject();

            final Set<String> itemsForInvalidation = rim.getItemIds();
            final ServiceMap rpmis = getRepositoryPathMethodInvalidatorService();
            final MethodInvocationPolicy invalidationPolicy = (MethodInvocationPolicy) rpmis.get(rim.getRepositoryPath());
            final String itemDescriptorNameForInvalidation = rim.getItemDescriptorName();

            if (invalidationPolicy != null) {
                if ((itemsForInvalidation != null) && (itemsForInvalidation.size() < getInvalidationThreadshold())) {

                    if (isLoggingDebug()) {
                        vlogDebug("Passed items have not reached threadshold {0}. Invalidating item by item",
                                getInvalidationThreadshold());
                    }

                    handleItemsInvalidation(invalidationPolicy, itemsForInvalidation, itemDescriptorNameForInvalidation);

                } else {

                    if (isLoggingDebug()) {
                        if (itemsForInvalidation == null) {
                            vlogDebug("Items not passed in message. Invalidating repository {0}", rim.getRepositoryPath());
                        } else {
                            vlogDebug("Items passed limit {0}. Invalidating repository whole repository {0}",
                                    getInvalidationThreadshold(), rim.getRepositoryPath());
                        }
                    }

                    handleRepositoryItemTypeInvalidation(invalidationPolicy, itemDescriptorNameForInvalidation);
                }
            }
        }
    }

    /**
     * Invalidates items defined in the message.
     * 
     * @param pInvalidationPolicy
     * @param pItemsForInvalidation
    * @param pItemDescriptorNameForInvalidation 
     */
    protected void handleItemsInvalidation(MethodInvocationPolicy pInvalidationPolicy, Set<String> pItemsForInvalidation,
            String pItemDescriptorNameForInvalidation) {

        final GSARepository gsar = (GSARepository) pInvalidationPolicy.getRepository();

        final String itemType = pItemDescriptorNameForInvalidation;
        if (pItemsForInvalidation.size() == 0) {
            try {
                ItemDescriptorImpl itemDescriptor = (ItemDescriptorImpl) gsar.getItemDescriptor(itemType);
                if (itemDescriptor == null ){
                    gsar.invalidateCaches();
                    return;
                }
                itemDescriptor.invalidateItemCache(true);
            } catch (RepositoryException e) {
                if (isLoggingError()) {
                    vlogError(e, "During invalidation item {0} while items list is empty", itemType);
                }
            }
            return;
        }
        for (final String itemId : pItemsForInvalidation) {
            try {
                final GSAItemDescriptor itemDescriptor = (GSAItemDescriptor) gsar.getItemDescriptor(itemType);
                itemDescriptor.invalidateCachedItem(itemId);

                if (isLoggingInfo()) {
                    vlogInfo("Item {0} with id {1} has been invalidated", itemType, itemId);
                }

            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    vlogError(re, "During invalidation item {0} with id {1}", itemType, itemId);
                }
            }

        }

        methodInvalidation(pInvalidationPolicy);
    }

    /**
     * Invalidates item type cache in repository
     * 
     * @param pInvalidationPolicy
    * @param pItemDescriptorNameForInvalidation 
     */
    protected void handleRepositoryItemTypeInvalidation(MethodInvocationPolicy pInvalidationPolicy,
            String pItemDescriptorNameForInvalidation) {

        if (pInvalidationPolicy.isActive()) {
            final GSARepository gsar = (GSARepository) pInvalidationPolicy.getRepository();
            try {
                final GSAItemDescriptor itemDescriptor = (GSAItemDescriptor) gsar.getItemDescriptor(pItemDescriptorNameForInvalidation);
                itemDescriptor.invalidateCaches();
                if (isLoggingInfo()) {
                    vlogInfo("Invalidated repository {0}, repository item descriptor {1}", gsar, pItemDescriptorNameForInvalidation);
                }
            } catch (RepositoryException re) {
                if (isLoggingError()) {
                    vlogError(re, "During invalidation repository {0},  item type {1}", gsar, pItemDescriptorNameForInvalidation);
                }
            }

            methodInvalidation(pInvalidationPolicy);

        }

    }

    /**
     * Invalidates method specified object.
     * 
     * @param pInvalidationPolicy
     */
    private void methodInvalidation(MethodInvocationPolicy pInvalidationPolicy) {
        final Object object = pInvalidationPolicy.getObject();
        try {
            final Method method = pInvalidationPolicy.getMethod();

            if ((method != null) && (object != null)) {
                method.invoke(object, null);

                if (isLoggingInfo()) {
                    vlogInfo("Invalidated object {0} for method {1}", object.getClass(), method.getName());
                }
            }

        } catch (SecurityException se) {
            if (isLoggingError()) {
                vlogError(se, "Invalidation failed for object {0} using invalidation service {1}", object.getClass(),
                        getAbsoluteName());
            }
        } catch (NoSuchMethodException nsme) {
            if (isLoggingError()) {
                vlogError(nsme, "Invalidation failed for object {0} using invalidation service {1}", object.getClass(),
                        getAbsoluteName());
            }
        } catch (IllegalArgumentException iae) {
            if (isLoggingError()) {
                vlogError(iae, "Invalidation failed for object {0} using invalidation service {1}", object.getClass(),
                        getAbsoluteName());
            }
        } catch (IllegalAccessException iae) {
            if (isLoggingError()) {
                vlogError(iae, "Invalidation failed for object {0} using invalidation service {1}", object.getClass(),
                        getAbsoluteName());
            }
        } catch (InvocationTargetException ite) {
            if (isLoggingError()) {
                vlogError(ite, "Invalidation failed for object {0} using invalidation service {1}", object.getClass(),
                        getAbsoluteName());
            }
        }
    }

    /**
     * @return the repositoryPathMethodInvalidatorService
     */
    public ServiceMap getRepositoryPathMethodInvalidatorService() {
        return mRepositoryPathMethodInvalidatorService;
    }

    /**
     * @param pRepositoryPathMethodInvalidatorService
     *          the repositoryPathMethodInvalidatorService to set
     */
    public void setRepositoryPathMethodInvalidatorService(ServiceMap pRepositoryPathMethodInvalidatorService) {
        mRepositoryPathMethodInvalidatorService = pRepositoryPathMethodInvalidatorService;
    }

    /**
     * @return the invalidationThreadshold
     */
    public int getInvalidationThreadshold() {
        return mInvalidationThreadshold;
    }

    /**
     * @param pInvalidationThreadshold
     *          the invalidationThreadshold to set
     */
    public void setInvalidationThreadshold(int pInvalidationThreadshold) {
        mInvalidationThreadshold = pInvalidationThreadshold;
    }

}
