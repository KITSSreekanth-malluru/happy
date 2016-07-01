package com.castorama.utils;

/**
 * ToDo: DOCUMENT ME!
 *
 * @author Andrew_Logvinov
 */
public class Tab {
    /** visible property. */
    private boolean visible = true;

    /** page property. */
    private String page = "";

    /** title property. */
    private String title = "";

    /**
     * Creates a new Tab object.
     */
    public Tab() {
    }

    /**
     * Creates a new Tab object.
     *
     * @param title parameter
     * @param page  parameter
     */
    public Tab(String title, String page) {
        this.title = title;
        this.page = page;
    }

    /**
     * Returns visible property.
     *
     * @return visible property.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the value of the visible property.
     *
     * @param visible parameter to set.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Returns page property.
     *
     * @return page property.
     */
    public String getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param page parameter to set.
     */
    public void setPage(String page) {
        this.page = page;
    }

    /**
     * Returns title property.
     *
     * @return title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param title parameter to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns string representation of tab
     * @return string representation of tab
     */
    @Override public String toString() {
        return title;
    }
}
