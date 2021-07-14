package br.com.cee.libraryapi.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String s){
        super(s);
    }
}
