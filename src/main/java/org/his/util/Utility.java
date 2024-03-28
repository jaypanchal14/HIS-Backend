package org.his.util;

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

    public static String getFormattedTime(ZonedDateTime zdt){
        return formatter.format(zdt);
    }

    public static String getFormattedOffsetTime(OffsetDateTime odt){
        return odt.format(formatter);
    }
}
