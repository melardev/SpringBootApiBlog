package com.melardev.spring.blogapi.dtos.response.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse extends AppResponse {
    Map<String, Object> errors;

    public ErrorResponse(Map<String, Object> errors) {
        super(false);
        this.errors = errors;
        if (getFullMessages() == null)
            setFullMessages(new ArrayList<>());

        errors.forEach((key, value) -> getFullMessages().add(value.toString()));
    }

    public ErrorResponse(String errors) {
        super(false);
        getFullMessages().add(errors);
    }

    public Map<String, Object> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, Object> errors) {
        this.errors = errors;
    }

    public ErrorResponse() {
        this(new HashMap<>());
    }

}
