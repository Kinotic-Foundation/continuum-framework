package org.kinotic.continuum.core.api.crud;

import java.util.List;

/**
 * A {@link Page} defines a page of data.
 * Created by Navíd Mitchell 🤪 on 11/5/23.
 */
public class Page<T> {

    private final int size;
    private final long totalElements;
    private final List<T> content;

    public Page(int size, long totalElements, List<T> content) {
        this.size = size;
        this.totalElements = totalElements;
        this.content = content;
    }

    /**
     * @return the number of elements currently on this page
     */
    public int getSize(){
        return size;
    }

    /**
     * @return the total number of elements available from the data source
     */
    public long getTotalElements(){
        return totalElements;
    }

    /**
     * @return the page content as {@link List}.
     */
    public List<T> getContent(){
        return content;
    }

    /**
     * @return the total number of pages available
     */
    public long getTotalPages() {
        return (long) Math.ceil((double) totalElements / (double) size);
    }
}
