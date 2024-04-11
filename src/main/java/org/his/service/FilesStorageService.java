package org.his.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
@Slf4j
public class FilesStorageService {

    @Value("${image_directory}")
    private String imageDirectory;
    private Path root;

    @PostConstruct
    public void init() {
        log.info("Inside init with directory at: "+imageDirectory);
        try {
            root = Paths.get(imageDirectory);
            log.info("root: "+root);
            Files.createDirectories(root);
        } catch (IOException e) {
              log.error("Exception : "+e.getMessage());
        }
    }

    public void saveImage(MultipartFile file, String newName) throws IOException {
        Files.copy(file.getInputStream(), root.resolve(newName));
        log.info("Image uploaded :" +newName);
    }

    public String loadImage(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
                //return resource.getContentAsByteArray();
            } else {
                throw new RuntimeException("Could not read the profile-image!");
            }
        } catch ( IOException e) {
            log.error("IOException occurred while loading the image : "+e.getMessage());
        }
        return null;
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

}