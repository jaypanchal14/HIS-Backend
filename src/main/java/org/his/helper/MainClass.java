package org.his.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.his.config.Roles;
import org.his.util.Utility;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainClass {

    public static void main(String[] args){
        /*Roles a = Roles.DOCTOR;
        System.out.println("Main class: "+a);

        Date d = Date.valueOf("2023-10-10");
        System.out.println("Date:"+d);
        Map<String, Integer> m = new HashMap<>();
        m.put("a",1);
        m.put("b",1);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writeValueAsString(m));
        } catch (JsonProcessingException e) {
            System.out.println("Exception : "+e.getMessage());
        }

        System.out.println(URLDecoder.decode("hello%20world", StandardCharsets.UTF_8));*/
        Date d = Date.valueOf("2023-10-10");
        System.out.println(d);
        System.out.println(d.toString());


    }

    public static void encryption(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("DjBaSdJbAs");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String cipher = encryptor.encrypt("bmwjjyazkycqnemm");
        System.out.println("Encrypted : "+cipher);
        String plain = encryptor.decrypt(cipher);
        System.out.println("Decrypted : "+plain);

        cipher = encryptor.encrypt("12345678");
        System.out.println("Encrypted : "+cipher);
        plain = encryptor.decrypt(cipher);
        System.out.println("Decrypted : "+plain);
    }
}