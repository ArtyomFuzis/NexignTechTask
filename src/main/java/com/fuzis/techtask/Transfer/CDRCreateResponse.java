package com.fuzis.techtask.Transfer;

/**
 * Объект для ответа на запрос создания файла с CDR отчетом.
 *
 * @param status     статус запроса
 * @param errMsg     сообщение об ошибке
 * @param reportUUID UUID запрошенного CDR отчета
 */
public record CDRCreateResponse(String status, String errMsg, String reportUUID) {

}
