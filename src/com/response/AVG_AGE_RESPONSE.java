package com.response;

import java.io.Serializable;

public class AVG_AGE_RESPONSE implements Serializable {
    double averageAge;

    public AVG_AGE_RESPONSE(double averageAge) {
        this.averageAge = averageAge;
    }

    public double getAverageAge() {
        return averageAge;
    }

    public void setAverageAge(double averageAge) {
        this.averageAge = averageAge;
    }
}
