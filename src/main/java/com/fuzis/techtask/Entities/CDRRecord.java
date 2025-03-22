package com.fuzis.techtask.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity для хранения CDR в базе данных. Содержит в себе все поля CDR: <br>
 * {@code callType} - объект типа CallType (внутренний enum), что может принимать два значения:
 * {@code Income} - входящий звонок (код 02), {@code Outcome} - исходящий звонок (код 01) <br>
 * {@code clientPhoneNumber} - номер телефона абонента текущей сети<br>
 * {@code otherPhoneNumber} - номер телефона абонента, с которым {@code clientPhoneNumber} разговаривал<br>
 * {@code timeStart} - дата и время начала звонка<br>
 * {@code timeEnd} - дата и время окончания звонка<br>
 * В данной классе представлены getтеры и setтеры для обращения к полям CDR и индексу в БД. <br>
 * Кроме них, в классе реализован метод {@code toCSVTypeString}, который преобразует все поля CDR к CSV виду для репрезентации
 */
@Entity
public class CDRRecord implements Serializable {
    public enum CallType {
        Income, Outcome
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private CallType callType;
    private String clientPhoneNumber;
    private String otherPhoneNumber;
    private LocalDateTime timeStart;
    private LocalDateTime timeEnd;

    public CDRRecord() {

    }

    public CDRRecord(CallType callType, String clientPhoneNumber, String otherPhoneNumber, LocalDateTime timeStart, LocalDateTime timeEnd) {
        this.callType = callType;
        this.clientPhoneNumber = clientPhoneNumber;
        this.otherPhoneNumber = otherPhoneNumber;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getOtherPhoneNumber() {
        return otherPhoneNumber;
    }

    public void setOtherPhoneNumber(String otherPhoneNumber) {
        this.otherPhoneNumber = otherPhoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CallType getCallType() {
        return callType;
    }

    public void setCallType(CallType callType) {
        this.callType = callType;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * Метод для репрезентации данной CDR записи в CSV формате
     * @return строку CSV, где разделитель - запятая и пробел
     */
    public String toCSVTypeString() {
        return (callType == CallType.Income ? "02" : "01") + ", " + clientPhoneNumber + ", " + otherPhoneNumber + ", " + timeStart.truncatedTo(ChronoUnit.SECONDS) + ", " + timeEnd.truncatedTo(ChronoUnit.SECONDS);
    }
}
