package com.fuzis.techtask.Services;


import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Entities.Client;
import com.fuzis.techtask.Repositories.ICDRRecordRepository;
import com.fuzis.techtask.Repositories.IClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Random;


@Service
public class CDRGenerator {
    private final Random rand;
    private final IClientRepository clientRepository;
    private final ICDRRecordRepository cdrRecordRepository;

    @Autowired
    public CDRGenerator(IClientRepository clientRepository, ICDRRecordRepository cdrRecordRepository) {
        rand = new Random();
        this.clientRepository = clientRepository;
        this.cdrRecordRepository = cdrRecordRepository;
    }

    private String nextNumberString(int length) {
        if (length >= 10) {
            throw new IllegalArgumentException("nextNumberString can only provide strings up to 9 characters long.");
        }
        int mod = 1;
        for (int i = 0; i < length; i++) {
            mod *= 10;
        }
        int randValue = (rand.nextInt() % mod + mod) % mod;
        StringBuilder result = new StringBuilder((randValue) + "");
        if (result.length() < length) {
            int diff = length - result.length();
            for (int i = 0; i < diff; i++) {
                result.insert(0, "0");
            }
        }
        return result.toString();
    }


    public void generateClients(int cnt) {
        String clientsPhonePrefix = (rand.nextBoolean() ? "+" : "") + nextNumberString(4);
        for (int i = 0; i < cnt; i++) {
            String currentClientPhoneNumber = clientsPhonePrefix + nextNumberString(7);
            Client client = new Client(currentClientPhoneNumber);
            clientRepository.save(client);
        }
    }

    public void generateCDRs(int cnt, LocalDateTime startDate, LocalDateTime finalDate, long maxTalkTimeSeconds) {
        ArrayList<Client> clients = new ArrayList<Client>();
        for (Client el : clientRepository.findAll()) clients.add(el);
        long gap = finalDate.toEpochSecond(ZoneOffset.UTC) - startDate.toEpochSecond(ZoneOffset.UTC);
        if (gap <= 0) throw new IllegalArgumentException("startDate show be earlier than finalDate");
        for(int i = 0 ; i < cnt ; i ++){
            long startTimeOffset = (rand.nextLong() % gap + gap) % gap;
            long durationMod = Math.min(gap - startTimeOffset, maxTalkTimeSeconds);
            long duration = (rand.nextLong() % durationMod + durationMod) % durationMod;
            LocalDateTime callStartDate = startDate.plusSeconds(startTimeOffset);
            LocalDateTime callEndDate = callStartDate.plusSeconds(duration);
            CDRRecord cdr = new CDRRecord(
                    rand.nextBoolean() ? CDRRecord.CallType.Income : CDRRecord.CallType.Outcome,
                    clients.get(rand.nextInt(clients.size())).getPhoneNumber(),
                    (rand.nextBoolean() ? "+" : "")+nextNumberString(4)+nextNumberString(7),
                    callStartDate,
                    callEndDate
            );
            cdrRecordRepository.save(cdr);
        }
    }
}
