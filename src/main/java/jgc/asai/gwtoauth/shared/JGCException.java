package jgc.asai.gwtoauth.shared;

import java.io.Serializable;

public class JGCException extends Exception implements Serializable {
    private String message;

    public JGCException(){}

    public JGCException(String message){
        super(message);
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
