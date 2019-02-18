package com.melardev.spring.blogapi.errors.exceptions;

public class UnexpectedStateException extends IllegalStateException {

    public UnexpectedStateException() {
        super("This is the kind of bug I was not expecting while making this app, please if you encounter this expection," +
                "report it, it is a priority to understand why this happened");
    }
}
