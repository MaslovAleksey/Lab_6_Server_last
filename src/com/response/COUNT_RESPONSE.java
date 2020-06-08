package com.response;

import java.io.Serializable;

public class COUNT_RESPONSE implements Serializable {
    long count;

    public long getCount() {
        return count;
    }

    public COUNT_RESPONSE(long count) {
        this.count = count;
    }
}
