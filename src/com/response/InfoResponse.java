package com.response;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InfoResponse implements Serializable {
    static final long serialVersionUID = -4862926644813433708L;
    String collectionType;
    int size;
    String elementType;
    LocalDateTime creationDate;

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
