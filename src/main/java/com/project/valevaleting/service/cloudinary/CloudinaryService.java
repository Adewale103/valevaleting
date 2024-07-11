package com.project.valevaleting.service.cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(byte[] byteArray, String folderName);
}
