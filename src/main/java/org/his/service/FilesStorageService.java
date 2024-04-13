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


    @Value("${patient_directory}")
    private String patientDirectory;

    //For staff-members
    private Path userRoot;

    //For patients' image/report/file
    private Path patientRoot;

    @PostConstruct
    public void init() {
        log.info("Inside init with directory at: "+imageDirectory);
        try {
            userRoot = Paths.get(imageDirectory);
            log.info("User-root: "+userRoot);
            Files.createDirectories(userRoot);

            patientRoot = Paths.get(patientDirectory);
            log.info("Patient-root: "+patientRoot);
            Files.createDirectories(patientRoot);

        } catch (IOException e) {
              log.error("Exception : "+e.getMessage());
        }
    }

    public void saveUserImage(MultipartFile file, String newName) throws IOException {
        Files.copy(file.getInputStream(), userRoot.resolve(newName));
        log.info("Image uploaded :" +newName);
    }

    public String loadUserImage(String filename) {
        try {
            Path file = userRoot.resolve(filename);
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
        FileSystemUtils.deleteRecursively(patientRoot.toFile());
        FileSystemUtils.deleteRecursively(userRoot.toFile());
    }

    //For patientDetails
    public void savePatientProfile(MultipartFile file, String newName) throws IOException {
        Files.copy(file.getInputStream(), patientRoot.resolve(newName));
        log.info("Patient-image uploaded :" +newName);
    }

    public String loadPatientImage(String filename) {
        try {
            Path file = patientRoot.resolve(filename);
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

    private Resource loadPatientFileAsResource(String filename) throws IOException {
        Path file = patientRoot.resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new RuntimeException("File not found: " + filename);
        }
    }

}