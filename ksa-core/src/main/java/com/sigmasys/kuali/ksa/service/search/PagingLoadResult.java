package com.sigmasys.kuali.ksa.service.search;


import java.io.Serializable;
import java.util.List;

/**
 * PagingResult.
 *
 * @author Michael Ivanov
 */
public class PagingLoadResult<T extends Serializable> implements Serializable {

    private int offset;
    private int totalLength;
    private List<T> data;

    public PagingLoadResult() {
    }

    public PagingLoadResult(List<T> data) {
        this(data, 0, data.size());
    }

    public PagingLoadResult(List<T> data, int offset, int totalLength) {
        this.data = data;
        this.offset = offset;
        this.totalLength = totalLength;
    }

    public int getOffset() {
        return offset;
    }

    public int getTotalLength() {
        return totalLength;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setTotalLength(int totalLength) {
        this.totalLength = totalLength;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
