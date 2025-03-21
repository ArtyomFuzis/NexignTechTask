package com.fuzis.techtask.Services;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import com.fuzis.techtask.Repositories.IClientRepository;
import com.fuzis.techtask.Testing.TestingCDRRecordsRepo;
import com.fuzis.techtask.Testing.TestingClientRepo;
import com.fuzis.techtask.Transfer.CallTime;
import com.fuzis.techtask.Transfer.UDRReport;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReportsServiceTest {
    @Test
    public void getUDRReport(){
        ICDRRecordRepository CDRRepo = new TestingCDRRecordsRepo();
        IClientRepository clientRepo = new TestingClientRepo();
        CDRGenerator CDRGen = new CDRGenerator(clientRepo, CDRRepo);
        ReportsService reportsService = new ReportsService(CDRRepo, clientRepo,CDRGen );
        LocalDateTime DTStart = LocalDateTime.of(2025, 1, 1, 5, 30, 0);
        LocalDateTime DTStart_five_minutes = LocalDateTime.of(2025, 1, 1, 5, 35, 0);
        LocalDateTime DTStart_ten_minutes = LocalDateTime.of(2025, 1, 1, 5, 40, 0);
        LocalDateTime DTStart_thirty_seconds = LocalDateTime.of(2025, 1, 1, 5, 30, 30);
        Client client = new Client("+75556664152");
        clientRepo.save(client);
        for(int i = 0 ; i < 10; i++){
            CDRRecord CDRRec = new CDRRecord(CDRRecord.CallType.Income, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_five_minutes);
            CDRRepo.save(CDRRec);
        }
        CDRRecord CDRRec = new CDRRecord(CDRRecord.CallType.Outcome, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_ten_minutes);
        CDRRecord CDRRec2 = new CDRRecord(CDRRecord.CallType.Outcome, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_thirty_seconds);
        CDRRepo.save(CDRRec);
        CDRRepo.save(CDRRec2);
        UDRReport realReport = new UDRReport(client.getPhoneNumber(), new CallTime(Duration.ofMinutes(50)), new CallTime(Duration.ofSeconds(630)));
        UDRReport providedReport = reportsService.getUDRReport(client.getPhoneNumber(), DTStart);
        System.out.println("Report should be: " + realReport);
        System.out.println("Report provided: " + providedReport);
        assertEquals(realReport, providedReport);

    }
}