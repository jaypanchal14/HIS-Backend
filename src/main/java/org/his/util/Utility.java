package org.his.util;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Utility {

    public static String customPattern = "yyyy-MM-dd HH:mm:ss";

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customPattern);

    public static String getUniqueId(){
        String str = UUID.randomUUID().toString();
        str = str.replace("-","").substring(0,16);
        return str+System.currentTimeMillis();
    }

    final static String passwordString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    final static SecureRandom rnd = new SecureRandom();

    public static String generateRandomPassword(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(passwordString.charAt(rnd.nextInt(passwordString.length())));
        return sb.toString();
    }

    public static String initialForgotPass = "Hello User, \n" +
            "Please use the below generated password for logging-in.\n" +
            "PASSWORD : ";
    public static String endForgotPass = "\n" +
            "Regards, \n" +
            "HIS-Admin";

    public static String getFormattedTime(ZonedDateTime zdt){
        return formatter.format(zdt);
    }

    public static String getFormattedOffsetTime(OffsetDateTime odt){
        return odt.format(formatter);
    }
}
