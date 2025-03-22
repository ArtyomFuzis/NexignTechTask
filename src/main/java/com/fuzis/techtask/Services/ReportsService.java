package com.fuzis.techtask.Services;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import com.fuzis.techtask.Repositories.IClientRepository;
import com.fuzis.techtask.Transfer.CallTime;
import com.fuzis.techtask.Transfer.UDRReport;
import com.fuzis.techtask.Utils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@Service
public class ReportsService
{
    Logger logger = LoggerFactory.getLogger(ReportsService.class);
    ICDRRecordRepository CDRRecordRepository;
    IClientRepository clientRepository;
    CDRGenerator cdrGenerator;

    @Value("${app.generate.clients}")
    Integer generateClients;
    @Value("${app.generate.CDRs}")
    Integer generateCDRs;
    @Autowired
    public ReportsService(ICDRRecordRepository CDRRecordRepository, IClientRepository clientRepository, CDRGenerator cdrGenerator){
        this.CDRRecordRepository = CDRRecordRepository;
        this.clientRepository = clientRepository;
        this.cdrGenerator = cdrGenerator;
    }

    public UDRReport getUDRReport(String clientPhoneNumber, LocalDateTime startDate){
        List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartBetween(clientPhoneNumber, startDate.minusSeconds(1), startDate.plusMonths(1).plusSeconds(1));
        return getUDRReportFromList(clientPhoneNumber, listCalls);
    }

    public UDRReport getUDRReportAllTime(String clientPhoneNumber){
        List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumber(clientPhoneNumber);
        return getUDRReportFromList(clientPhoneNumber, listCalls);
    }

    private UDRReport getUDRReportFromList (String clientPhoneNumber, List<CDRRecord> listCalls){
        if(clientRepository.findByPhoneNumber(clientPhoneNumber).isPresent()) {
            Duration durationIncome = Duration.ZERO;
            Duration durationOutcome = Duration.ZERO;
            for (CDRRecord cdrRecord : listCalls) {
                if (cdrRecord.getCallType() == CDRRecord.CallType.Income)
                    durationIncome = durationIncome.plus(Duration.between(cdrRecord.getTimeStart(), cdrRecord.getTimeEnd()));
                else
                    durationOutcome = durationOutcome.plus(Duration.between(cdrRecord.getTimeStart(), cdrRecord.getTimeEnd()));
            }
            return new UDRReport(clientPhoneNumber, new CallTime(durationIncome), new CallTime(durationOutcome));
        }
        throw new IllegalArgumentException("Client not in company's database");
    }

    public List<UDRReport> getAllUDRReport(LocalDateTime startDate){
        Iterable<Client> allClients = clientRepository.findAll();
        List<UDRReport> UDRReports = new LinkedList<>();
        for(Client client : allClients){
            UDRReports.add(getUDRReport(client.getPhoneNumber(), startDate));
        }
        return UDRReports;
    }
    public List<UDRReport> getAllUDRReportAllTime(){
        Iterable<Client> allClients = clientRepository.findAll();
        List<UDRReport> UDRReports = new LinkedList<>();
        for(Client client : allClients){
            UDRReports.add(getUDRReportAllTime(client.getPhoneNumber()));
        }
        return UDRReports;
    }

    public String createCDR(String clientPhoneNumber, LocalDateTime startDate , LocalDateTime endDate) throws NoSuchAlgorithmException, IOException {
        Files.createDirectories(Paths.get("reports/"));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        StringBuilder sb = new StringBuilder();
        if(clientRepository.findByPhoneNumber(clientPhoneNumber).isPresent()) {
            List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartBetween(clientPhoneNumber, startDate.minusSeconds(1), endDate.plusSeconds(1));
            listCalls.sort(new Utils.CmpCDR());
            for (CDRRecord cdrRecord : listCalls) {
                sb.append(cdrRecord.toCSVTypeString()).append("\n");
                digest.update(sb.toString().getBytes());
            }
            String UUID = clientPhoneNumber.substring(clientPhoneNumber.length()-7)+"-"+ Utils.bytesToHex(digest.digest());
            FileWriter f = new FileWriter("reports/" + UUID + ".csv");
            f.write(sb.toString());
            logger.info("Created CSV report {}.csv for client: {} ", UUID, clientPhoneNumber);
            f.close();
            return UUID;
        }
        throw new IllegalArgumentException("Client not in company's database");
    }

    @PostConstruct
    private void generateCDRs(){
        cdrGenerator.generateClients(generateClients);
        LocalDateTime startDate = LocalDateTime.now();
        cdrGenerator.generateCDRs(generateCDRs, startDate, startDate.plusYears(1),4 * 3600);
        logger.info("Generated phone numbers:");
        for(Client client : clientRepository.findAll()){
            logger.info(client.getPhoneNumber());
        }
    }
}
