package com.fuzis.techtask;

import com.fuzis.techtask.Entities.CDRRecord;

import java.util.Comparator;

/**
 * Класс с дополнительными методами и классами, что не могут быть отнесены к конкретному модулю
 */
public class Utils {
    /**
     * Преобразовать массив байтов в строку
     *
     * @param hash массив байтов, что надо отобразить в строке
     * @return строку с массивом байтов, представленным в шестнадцатеричном формате
     */
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

    /**
     * Класс-компаратор для объектов типа CDRRecord. Сначала идет сортировка по времени начала звонка по возрастанию,
     * потом по времени окончания звонка по возрастанию, потом по типу звонка. Если все эти три параметра равны, то
     * эти объекты считаются равными для сортировочного алгоритма
     */
    public static class CmpCDR implements Comparator<CDRRecord> {

        @Override
        public int compare(CDRRecord o1, CDRRecord o2) {
            if (o1.getTimeStart().equals(o2.getTimeStart())) {
                if (o1.getTimeEnd().equals(o2.getTimeEnd())) {
                    return o1.getCallType().compareTo(o2.getCallType());
                }
                return o1.getTimeEnd().compareTo(o2.getTimeEnd());
            }
            return o1.getTimeStart().compareTo(o2.getTimeStart());
        }
    }
}
