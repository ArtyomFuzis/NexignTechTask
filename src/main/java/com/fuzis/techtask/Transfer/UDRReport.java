package com.fuzis.techtask.Transfer;

import java.io.Serializable;

/**
 * Объект для отображения UDR отчета
 *
 * @param msisdn        номер телефона абонента
 * @param incomingCall  объект типа CallTime, суммарное время входящих звонков
 * @param outcomingCall объект типа CallTime, суммарное время исходящих звонков
 */
public record UDRReport(String msisdn, CallTime incomingCall, CallTime outcomingCall) implements Serializable {

}
