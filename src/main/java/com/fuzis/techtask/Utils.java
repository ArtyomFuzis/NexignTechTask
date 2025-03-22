package com.fuzis.techtask;

import com.fuzis.techtask.Entities.CDRRecord;

import java.util.Comparator;

public class Utils {
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static class CmpCDR implements Comparator<CDRRecord> {

        @Override
        public int compare(CDRRecord o1, CDRRecord o2) {
            if(o1.getTimeStart().equals(o2.getTimeStart())){
                if(o1.getTimeEnd().equals(o2.getTimeEnd())){
                    return o1.getCallType().compareTo(o2.getCallType());
                }
                return o1.getTimeEnd().compareTo(o2.getTimeEnd());
            }
            return o1.getTimeStart().compareTo(o2.getTimeStart());
        }
    }
}
