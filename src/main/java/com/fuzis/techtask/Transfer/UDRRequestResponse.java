package com.fuzis.techtask.Transfer;


import java.io.Serializable;

/**
 * Объект для ответа на запрос получения UDR запроса для одного пользователя
 *
 * @param status статус запроса
 * @param errMsg сообщение об ошибке
 * @param report запрошенный UDR отчет
 */
public record UDRRequestResponse(String status, String errMsg, UDRReport report) implements Serializable {

}
