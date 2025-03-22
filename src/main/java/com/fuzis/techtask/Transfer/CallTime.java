package com.fuzis.techtask.Transfer;

import java.io.Serializable;
import java.time.Duration;

/**
 * Рекорд для представления времени {@code totalTime} в UDR. Перевод из {@code Duration} в String производится с
 * помощью конструктора
 */
public record CallTime(String totalTime) implements Serializable {
    public CallTime(Duration totalTime) {
        this(String.format("%d:%02d:%02d", totalTime.toHours(), totalTime.toMinutesPart(), totalTime.toSecondsPart()));
    }
}
