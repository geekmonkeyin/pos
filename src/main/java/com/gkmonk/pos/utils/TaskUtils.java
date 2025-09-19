package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.logs.TaskLogs;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskUtils {

    public static String extractDeviceId(String meta) {
        if(meta == null){
            return POSConstants.EMPTY;
        }
        Pattern p = Pattern.compile("\\bDevice\\s*ID\\s*:\\s*([^,]*)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(meta);
        if (!m.find()) return null;

        String raw = m.group(1).trim();
        return (raw.isEmpty() || raw.equalsIgnoreCase("null")) ? null : raw;
    }

   public static void extractDeviceId(List<TaskLogs> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        logs.forEach(log -> log.setDeviceId(extractDeviceId(log.getMetaData())));
       logs.forEach(log -> log.setEmpName(getEmpName(log.getDeviceId())));

   }

    private static String getEmpName(String deviceId) {
        if(deviceId == null){
            return "DESKTOP";
        }
        if(deviceId.contains("vivo24") || deviceId.contains("V2407") || deviceId.contains("V24")){
            return "Radhika";
        }
        if(deviceId.contains("vivo23") || deviceId.contains("VIVO 2333") || deviceId.contains("V2333")){
            return "Suhani";
        }
        if(deviceId.contains("OnePlus")){
            return "Punam";
        }
        return "DESKTOP";
    }
}
