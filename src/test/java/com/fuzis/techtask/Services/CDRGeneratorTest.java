package com.fuzis.techtask.Services;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Testing.TestingCDRRecordsRepo;
import com.fuzis.techtask.Testing.TestingClientRepo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class CDRGeneratorTest {

    @Test
    void generateClients() {
        TestingClientRepo tesClientRepo = new TestingClientRepo();
        TestingCDRRecordsRepo testCDRRecordRepo = new TestingCDRRecordsRepo();
        CDRGenerator gen = new CDRGenerator(tesClientRepo, testCDRRecordRepo);
        gen.generateClients(10);
        String prefix = null;
        System.out.println("Generated phone numbers:");
        for(Client el : tesClientRepo.findAll()){
            System.out.println(el.getPhoneNumber());
            assert el.getPhoneNumber().length() == 11 || el.getPhoneNumber().length() == 12; // +7 and 8 variant
            if (prefix == null){
                if(el.getPhoneNumber().length() == 11)prefix = el.getPhoneNumber().substring(0, 4);
                else prefix = el.getPhoneNumber().substring(0, 5);
            }
            else assert el.getPhoneNumber().startsWith(prefix);
        }
    }
    @Test
    void generateCDRRecords() {
        TestingClientRepo tesClientRepo = new TestingClientRepo();
        TestingCDRRecordsRepo testCDRRecordRepo = new TestingCDRRecordsRepo();
        CDRGenerator gen = new CDRGenerator(tesClientRepo, testCDRRecordRepo);
        gen.generateClients(10);
        long maxTalkTimeSeconds = 10800;
        gen.generateCDRs(1000, LocalDateTime.now(), LocalDateTime.now().plusYears(1),maxTalkTimeSeconds);
        for(CDRRecord el : testCDRRecordRepo.findAll()){
            System.out.println(el.toCSVTypeString());
            assert tesClientRepo.findByPhoneNumber(el.getClientPhoneNumber()).isPresent();
            assert el.getOtherPhoneNumber().length() == 11 || el.getOtherPhoneNumber().length() == 12;
            assert el.getTimeStart().isBefore(el.getTimeEnd());
            assert el.getTimeStart().plusSeconds(maxTalkTimeSeconds+1).isAfter(el.getTimeEnd());
        }
    }
}