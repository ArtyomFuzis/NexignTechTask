package com.fuzis.techtask.Services;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import com.fuzis.techtask.Repositories.IClientRepository;
import com.fuzis.techtask.Transfer.CallTime;
import com.fuzis.techtask.Transfer.UDRReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportsService
{
    ICDRRecordRepository CDRRecordRepository;
    IClientRepository clientRepository;
    CDRGenerator cdrGenerator;

    @Autowired
    public ReportsService(ICDRRecordRepository CDRRecordRepository, IClientRepository clientRepository, CDRGenerator cdrGenerator){
        this.CDRRecordRepository = CDRRecordRepository;
        this.clientRepository = clientRepository;
        this.cdrGenerator = cdrGenerator;
    }

    public UDRReport getUDRReport(String clientPhoneNumber, LocalDateTime startDate){
        if(clientRepository.findByPhoneNumber(clientPhoneNumber).isPresent()){
            List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartAfter(clientPhoneNumber, startDate.minusSeconds(1));
            Duration durationIncome = Duration.ZERO;
            Duration durationOutcome = Duration.ZERO;
            for(CDRRecord cdrRecord : listCalls){
                if(cdrRecord.getCallType() == CDRRecord.CallType.Income)
                    durationIncome = durationIncome.plus(Duration.between(cdrRecord.getTimeStart(), cdrRecord.getTimeEnd()));
                else
                    durationOutcome = durationOutcome.plus(Duration.between(cdrRecord.getTimeStart(), cdrRecord.getTimeEnd()));
            }
            return new UDRReport(clientPhoneNumber, new CallTime(durationIncome), new CallTime(durationOutcome));
        }
        throw new IllegalArgumentException("Client not in company's database");
    }

}
