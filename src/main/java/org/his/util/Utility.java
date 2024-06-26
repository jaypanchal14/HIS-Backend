package org.his.util;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utility {

    public static String customPattern = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customPattern);

    public static String getUniqueId(){
        String str = UUID.randomUUID().toString();
        str = str.replace("-","").substring(0,17);
        return str+System.currentTimeMillis();
    }

    public static String getEmergencyId(){
        return "EMER"+System.currentTimeMillis();
    }

    final static String passwordString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    final static SecureRandom rnd = new SecureRandom();

    public static String generateRandomPassword(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(passwordString.charAt(rnd.nextInt(passwordString.length())));
        return sb.toString();
    }

    public static String initialForgotPass = "Hello User, \n\n" +
            "Please use the below generated password for logging-in.\n" +
            "PASSWORD : ";
    public static String endForgotPass = "\n\n\n" +
            "Regards, \n" +
            "HIS-Admin";

    public static String getFormattedOffsetTime(OffsetDateTime odt){
        return odt.format(formatter);
    }

    public static OffsetDateTime getOffSetStartDateFromString(String date){
        return LocalDate.parse(date).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(5,30));
    }

    public static OffsetDateTime getOffSetEndDateFromString(String date){
        return LocalDate.parse(date).plusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(5,30));
    }

    public static OffsetDateTime getOffSetDateOf30Days(){
        return LocalDate.now().minusDays(30).atStartOfDay().atOffset(ZoneOffset.ofHoursMinutes(5,30));
    }

    public static boolean isImage(Path filePath) throws IOException {
        String mimeType = Files.probeContentType(filePath);
        // Check if the MIME type starts with "image/"
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex);
            }
        }
        return null;
    }

    public static String generateNewImageName(String extension){
        String str = UUID.randomUUID().toString();
        str = str.replace("-","").substring(0,15);
        return str+System.currentTimeMillis()+extension;
    }


    public static MediaType determineMediaType(String filePath) {
        // Extract file extension from the file path
        String fileExtension = filePath.substring(filePath.lastIndexOf('.') + 1).toLowerCase();

        // Map file extensions to MediaTypes
        return switch (fileExtension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "txt" -> MediaType.TEXT_PLAIN;
            case "doc" -> MediaType.valueOf("application/msword");
            case "docx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            case "xls" -> MediaType.valueOf("application/vnd.ms-excel");
            case "xlsx" -> MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.APPLICATION_OCTET_STREAM; // Fallback to binary data
        };
    }


}
