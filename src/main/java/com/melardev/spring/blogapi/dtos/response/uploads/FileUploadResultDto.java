package com.melardev.spring.blogapi.dtos.response.uploads;

import com.melardev.spring.blogapi.dtos.response.base.SuccessResponse;

public class FileUploadResultDto extends SuccessResponse {
    String url;

    public FileUploadResultDto(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
