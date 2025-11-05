package com.hoangduong.hoangduongcomputer.reponsitory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hoangduong.hoangduongcomputer.dto.ImageInfo;

@Repository
public class ImageStorageRepository {

    @Value("${app.file.storage-dir}")
    String storageDir;

    @Value("${app.file.access-url}")
    String accessUrl;

    public ImageInfo store(List<MultipartFile> fileImages) throws IOException {
        List<String> urls = new ArrayList<>();
        Path dirPath = Paths.get(storageDir);
        Files.createDirectories(dirPath); // đảm bảo thư mục tồn tại

        for (MultipartFile file : fileImages) {
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + (fileExtension != null ? "." + fileExtension : "");
            Path fullPath = dirPath.resolve(fileName);

            Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
            urls.add(accessUrl + fileName);
        }

        return ImageInfo.builder().urls(urls).build();
    }
}
