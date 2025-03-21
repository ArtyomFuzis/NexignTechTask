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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportsServiceTest {

    record PreparedData(ICDRRecordRepository CDRRepo, IClientRepository clientRepo, CDRGenerator CDRGen, ReportsService reportsService, LocalDateTime DTStart, Client client, Client client2) {}

    private PreparedData prepare(){
        ICDRRecordRepository CDRRepo = new TestingCDRRecordsRepo();
        IClientRepository clientRepo = new TestingClientRepo();
        CDRGenerator CDRGen = new CDRGenerator(clientRepo, CDRRepo);
        ReportsService reportsService = new ReportsService(CDRRepo, clientRepo,CDRGen );
        LocalDateTime DTStart = LocalDateTime.of(2025, 1, 1, 5, 30, 0);
        LocalDateTime DTStart_five_minutes = LocalDateTime.of(2025, 1, 1, 5, 35, 0);
        LocalDateTime DTStart_ten_minutes = LocalDateTime.of(2025, 1, 1, 5, 40, 0);
        LocalDateTime DTStart_thirty_seconds = LocalDateTime.of(2025, 1, 1, 5, 30, 30);
        Client client = new Client("+75556664152");
        Client client2 = new Client("+75556664153");
        clientRepo.save(client);
        clientRepo.save(client2);
        for(int i = 0 ; i < 10; i++){
            CDRRecord CDRRec = new CDRRecord(CDRRecord.CallType.Income, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_five_minutes);
            CDRRecord CDRRec2 = new CDRRecord(CDRRecord.CallType.Outcome, client.getPhoneNumber(), "+111122211111", DTStart_thirty_seconds, DTStart_five_minutes);
            CDRRepo.save(CDRRec);
            CDRRepo.save(CDRRec2);
        }
        CDRRecord CDRRec = new CDRRecord(CDRRecord.CallType.Outcome, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_ten_minutes);
        CDRRecord CDRRec2 = new CDRRecord(CDRRecord.CallType.Outcome, client.getPhoneNumber(), "+11111111111", DTStart, DTStart_thirty_seconds);
        CDRRecord CDRRec3 = new CDRRecord(CDRRecord.CallType.Income, client.getPhoneNumber(), "+11111111111", DTStart_thirty_seconds, DTStart_ten_minutes);
        CDRRecord CDRRec4 = new CDRRecord(CDRRecord.CallType.Income, client2.getPhoneNumber(), "+11111111111", DTStart, DTStart_ten_minutes);
        CDRRecord CDRRec5 = new CDRRecord(CDRRecord.CallType.Income, client2.getPhoneNumber(), "+11111111111", DTStart_five_minutes, DTStart_ten_minutes);
        CDRRepo.save(CDRRec);
        CDRRepo.save(CDRRec2);
        CDRRepo.save(CDRRec3);
        CDRRepo.save(CDRRec4);
        CDRRepo.save(CDRRec5);
        return new PreparedData(CDRRepo, clientRepo, CDRGen, reportsService, DTStart, client, client2);
    }
    @Test
    public void getUDRReport(){
        PreparedData preparedData = prepare();
        UDRReport realReport = new UDRReport(preparedData.client.getPhoneNumber(), new CallTime(Duration.ofSeconds(9*60 + 30)), new CallTime(Duration.ofSeconds(45 * 60)));
        UDRReport providedReport = preparedData.reportsService.getUDRReport(preparedData.client.getPhoneNumber(), preparedData.DTStart.plusSeconds(5));
        System.out.println("Report should be: " + realReport);
        System.out.println("Report provided: " + providedReport);
        assertEquals(realReport, providedReport);
    }
    @Test
    public void getUDRReportAllTime(){
        PreparedData preparedData = prepare();
        UDRReport realReport = new UDRReport(preparedData.client.getPhoneNumber(), new CallTime(Duration.ofSeconds(59 * 60 + 30)), new CallTime(Duration.ofSeconds(55 * 60 + 30)));
        UDRReport providedReport = preparedData.reportsService.getUDRReport(preparedData.client.getPhoneNumber(), preparedData.DTStart);
        System.out.println("Report should be: " + realReport);
        System.out.println("Report provided: " + providedReport);
        assertEquals(realReport, providedReport);
    }
    @Test
    public void getAllUDRReport(){
        PreparedData preparedData = prepare();
        UDRReport rep1 = new UDRReport(preparedData.client.getPhoneNumber(), new CallTime(Duration.ofSeconds(9*60 + 30)), new CallTime(Duration.ofSeconds(45 * 60)));
        UDRReport rep2 = new UDRReport(preparedData.client2.getPhoneNumber(), new CallTime(Duration.ofSeconds(5*60)), new CallTime(Duration.ofSeconds(0)));
        List<UDRReport> realReports = new ArrayList<UDRReport>();
        realReports.add(rep1);
        realReports.add(rep2);
        List<UDRReport> providedReports = preparedData.reportsService.getAllUDRReport(preparedData.DTStart.plusSeconds(10));
        System.out.println("Reports should be: " + realReports.get(0) + " " + realReports.get(1));
        System.out.println("Reports provided: " + providedReports.get(0) + " " + providedReports.get(1));
        assertEquals(realReports, providedReports);
    }
    @Test
    public void getAllUDRReportAllTime(){
        PreparedData preparedData = prepare();
        UDRReport rep1 = new UDRReport(preparedData.client.getPhoneNumber(), new CallTime(Duration.ofSeconds(59*60 + 30)), new CallTime(Duration.ofSeconds(55 * 60 + 30)));
        UDRReport rep2 = new UDRReport(preparedData.client2.getPhoneNumber(), new CallTime(Duration.ofSeconds(15*60)), new CallTime(Duration.ofSeconds(0)));
        List<UDRReport> realReports = new ArrayList<UDRReport>();
        realReports.add(rep1);
        realReports.add(rep2);
        List<UDRReport> providedReports = preparedData.reportsService.getAllUDRReportAllTime();
        System.out.println("Reports should be: " + realReports.get(0) + " " + realReports.get(1));
        System.out.println("Reports provided: " + providedReports.get(0) + " " + providedReports.get(1));
        assertEquals(realReports, providedReports);
    }
}