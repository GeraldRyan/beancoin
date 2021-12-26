package com.ryan.gerald.beancoin.exceptions;

public class EntityNotLoadedException extends Exception {
    String classname;

    public EntityNotLoadedException(String classname, String errorMessage) {
        super(errorMessage);
        this.classname = classname;

    }
}
