package com.melardev.spring.blogapi.entities;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "upload_type", discriminatorType = DiscriminatorType.INTEGER)
public class FileUpload extends TimestampedEntity {

    String fileName;
    String originalFileName;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
