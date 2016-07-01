package com.castorama.cache.lowmemory;

import com.castorama.search.droplet.CastCarouselSearchDroplet;

import atg.nucleus.GenericService;

/**
 * Given recovery action flushes cache of CastCarouselSearchDroplet
 *
 * @author  EPAM team
 * @version ToDo: INSERT VERSION NUMBER
 */
public class CarouselCacheLowMemoryRecoveryAction extends GenericService implements LowMemoryRecoveryAction {
    /** castCarouselSearchDroplet property */
    private CastCarouselSearchDroplet mCastCarouselSearchDroplet;

    /**
     * Flushes the cache
     */
    public void runAction() {
        if (mCastCarouselSearchDroplet != null) {
        	if (isLoggingDebug()) {
                logDebug("Flushing cache");
            }
            mCastCarouselSearchDroplet.flushCache();
        }
    }

    /**
     * Returns castCarouselSearchDroplet property.
     *
     * @return castCarouselSearchDroplet property.
     */
    public CastCarouselSearchDroplet getCastCarouselSearchDroplet() {
        return mCastCarouselSearchDroplet;
    }

    /**
     * Sets the value of the castCarouselSearchDroplet property.
     *
     * @param pCastCarouselSearchDroplet parameter to set.
     */
    public void setCastCarouselSearchDroplet(CastCarouselSearchDroplet pCastCarouselSearchDroplet) {
        mCastCarouselSearchDroplet = pCastCarouselSearchDroplet;
    }

}
