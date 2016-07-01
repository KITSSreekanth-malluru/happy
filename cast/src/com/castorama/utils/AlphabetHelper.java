package com.castorama.utils;

import java.util.Map;

/**
 * Helper class for alhpabet maniipulations.
 *
 * @author Katsiaryna Dmitrievich
 */
public class AlphabetHelper {
    /** List of letters. */
    private Map<String, String> alphabet;

    /**
     * Gets map whith all letters.
     *
     * @return the alphabet
     */
    public Map<String, String> getAlphabet() {
        return alphabet;
    }

    /**
     * Sets maps with letters.
     *
     * @param alphabet the alphabet to set
     */
    public void setAlphabet(Map<String, String> alphabet) {
        this.alphabet = alphabet;
    }
}
