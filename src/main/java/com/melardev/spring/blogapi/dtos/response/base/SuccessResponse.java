package com.melardev.spring.blogapi.dtos.response.base;

public class SuccessResponse extends AppResponse {
    public SuccessResponse() {
        super(true);
    }

    public SuccessResponse(String message) {
        this();
        addFullMessage(message);
    }


}
