package com.response;

import java.io.Serializable;

public class CountResponse implements Serializable {
    long count;

    public long getCount() {
        return count;
    }

    public CountResponse(long count) {
        this.count = count;
    }
}
