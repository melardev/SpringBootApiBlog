package com.melardev.spring.blogapi.dtos.response.base;

import java.util.ArrayList;
import java.util.List;

public abstract class AppResponse {
    private Boolean success;
    private List<String> fullMessages;

    public Boolean getSuccess() {
        return success;
    }

    public List<String> getFullMessages() {
        return fullMessages;
    }

    public void setFullMessages(List<String> fullMessages) {
        this.fullMessages = fullMessages;
    }

    public AppResponse() {
        System.out.println("Created AppResponse");
    }

    protected AppResponse(boolean success) {
        this.success = success;
        fullMessages = new ArrayList<>();
    }

    public boolean isSuccess() {
        return success;
    }


    protected void addFullMessage(String message) {
        if (fullMessages == null)
            fullMessages = new ArrayList<>();

        fullMessages.add(message);
    }

}
