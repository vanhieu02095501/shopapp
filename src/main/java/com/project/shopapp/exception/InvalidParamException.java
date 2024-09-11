package com.project.shopapp.exception;

import org.aspectj.bridge.Message;

public class InvalidParamException extends Exception{

    public InvalidParamException(String message){
        super(message);

    }
}
