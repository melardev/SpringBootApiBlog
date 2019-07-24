package com.melardev.spring.blogapi.controllers;


import com.melardev.spring.blogapi.dtos.response.uploads.FileUploadResultDto;
import com.melardev.spring.blogapi.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    @Autowired
    private StorageService storageService;

    @Autowired
    Environment environment;

    @PostMapping
    public FileUploadResultDto create(
            @RequestParam("file") MultipartFile uploadingFiles, HttpServletRequest request) throws IOException {
        File file = storageService.upload(uploadingFiles, "/temp/images/articles");
        String path = "http://" + InetAddress.getLocalHost().getHostName() + ":8080" + // environment.getProperty("server.port") +
                "/api" + file.getAbsolutePath().replace(new File("uploads").getAbsolutePath(), "")
                .replace("\\", "/");

        return new FileUploadResultDto(path);

    }

}
