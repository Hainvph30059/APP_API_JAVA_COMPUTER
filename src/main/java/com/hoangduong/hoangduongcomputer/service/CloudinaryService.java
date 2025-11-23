package com.hoangduong.hoangduongcomputer.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    private Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));
        }
        return cloudinary;
    }

    public Map<String, Object> uploadFile(MultipartFile file, String folderName) throws IOException {
        return getCloudinary().uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderName,
                        "resource_type", "auto"
                )
        );
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = uploadFile(file, "users");
        return (String) uploadResult.get("secure_url");
    }

    public Map<String, Object> deleteFile(String publicId) throws IOException {
        return getCloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}