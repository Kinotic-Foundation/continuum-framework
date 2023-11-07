package org.kinotic.continuum.core.api.crud;

import java.util.List;

/**
 * A {@link Page} defines a page of data.
 * Created by Navíd Mitchell 🤪 on 11/5/23.
 */
public class Page<T> {

    private final List<T> content;
    private final long totalElements;

    public Page(List<T> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    /**
     * @return the total number of elements available from the data source if known.
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

}
