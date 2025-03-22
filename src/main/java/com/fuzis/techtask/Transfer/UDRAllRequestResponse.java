package com.fuzis.techtask.Transfer;

import java.util.List;

/**
 * Объект для ответа на запрос получения UDR запроса для всех пользователей
 *
 * @param status  статус запроса
 * @param errMsg  сообщение об ошибке
 * @param reports запрошенные UDR отчеты
 */
public record UDRAllRequestResponse(String status, String errMsg, List<UDRReport> reports) {
}
