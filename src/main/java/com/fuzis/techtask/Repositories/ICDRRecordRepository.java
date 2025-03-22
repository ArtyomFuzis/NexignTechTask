package com.fuzis.techtask.Repositories;

import com.fuzis.techtask.Entities.CDRRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для Entity CDRRecord. Наследует весь интерфейс CRUD репозитория
 * и реализует метод {@code getCDRRecordsByClientPhoneNumberAndTimeStartBetween} - получение CDR записей для клиента с
 * указанным номером телефона и датой и временем начала звонка между указанными,
 * а также {@code getCDRRecordsByClientPhoneNumber} - получение CDR записей для клиента с указанным номером телефона за
 * весь период наличия CDR
 */
@Repository
public interface ICDRRecordRepository extends CrudRepository<CDRRecord, Integer> {
    List<CDRRecord> getCDRRecordsByClientPhoneNumberAndTimeStartBetween(String clientPhoneNumber, LocalDateTime timeStart, LocalDateTime timeStart2);

    List<CDRRecord> getCDRRecordsByClientPhoneNumber(String fromPhoneNumber);
}
