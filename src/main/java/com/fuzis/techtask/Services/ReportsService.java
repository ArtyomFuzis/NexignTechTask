package com.fuzis.techtask.Services;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import com.fuzis.techtask.Repositories.IClientRepository;
import com.fuzis.techtask.Transfer.CallTime;
import com.fuzis.techtask.Transfer.UDRReport;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartAfter(clientPhoneNumber, startDate.minusSeconds(1));
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
