package com.example.stock.Util;

import org.apache.catalina.webresources.FileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Component
public class FileUpload {

    @Value("${com.example.upload.path}")
    private String uploadPath;

    // *************** 파일업로드 ***************** //
    public String uploadFile(MultipartFile imgFile) {
        if (imgFile == null || imgFile.isEmpty()) {
            return null;
        }

        String fileName = imgFile.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String saveFileName = UUID.randomUUID() + extension;

        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()) uploadDir.mkdirs();

        try {
            imgFile.transferTo(new File(uploadDir, saveFileName));
            return saveFileName;
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패" , e);
        }
    }

    // *************** 파일삭제 ***************** //
    public void deleteFile(String fileName) {
        if(fileName == null || fileName.isEmpty())  return;
        File targetFile = new File(uploadPath, fileName);
        if(targetFile.exists()) {
            targetFile.delete();
        }
    }

    // *************** 파일 다운로드용 Resource 반환 ***************** //
    public Resource getFileAsResourse(String fileName) {
        if(fileName == null || fileName.isEmpty()) return null;

        File file = new File(uploadPath, fileName);
        if(!file.exists()) return null;

        return new FileSystemResource(file);
    }
}
