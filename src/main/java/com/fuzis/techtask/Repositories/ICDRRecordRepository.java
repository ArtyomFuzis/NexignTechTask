package com.fuzis.techtask.Repositories;

import com.fuzis.techtask.Entities.CDRRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface  ICDRRecordRepository extends CrudRepository<CDRRecord, Integer> {
    List<CDRRecord> getCDRRecordsByClientPhoneNumberAndTimeStartAfter(String fromPhoneNumber, LocalDateTime timeStartAfter);
    List<CDRRecord> getCDRRecordsByClientPhoneNumber(String fromPhoneNumber);
}
