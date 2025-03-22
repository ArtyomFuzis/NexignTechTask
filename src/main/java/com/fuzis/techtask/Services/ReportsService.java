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

/**
 * Основный сервис приложения. Содержит методы для генерации UDR отчетов или сохранения CDR в файл. <br>
 * При запуске Spring приложения (PostConstruct) вызывает методы из {@code CDRGenerator} и генерирует
 * {@code app.generate.clients} абонентов и {@code app.generate.CDRs} CDR записей. <br>
 * Данные параметры устанавливаются в {@code application.properties}
 */
@Service
public class ReportsService {
    Logger logger = LoggerFactory.getLogger(ReportsService.class);
    ICDRRecordRepository CDRRecordRepository;
    IClientRepository clientRepository;
    CDRGenerator cdrGenerator;

    @Value("${app.generate.clients}")
    Integer generateClients;
    @Value("${app.generate.CDRs}")
    Integer generateCDRs;

    @Autowired
    public ReportsService(ICDRRecordRepository CDRRecordRepository, IClientRepository clientRepository, CDRGenerator cdrGenerator) {
        this.CDRRecordRepository = CDRRecordRepository;
        this.clientRepository = clientRepository;
        this.cdrGenerator = cdrGenerator;
    }

    /**
     * Данный метод генерирует UDR запрос для абонента с указанным номером телефона на месяц начиная с указанной даты
     *
     * @param clientPhoneNumber номер телефона абонента текущей сети
     * @param startDate         дата, начиная с которой отсчитывается месяц для формирования отчета
     * @return запрошенный отчет, либо {@code IllegalArgumentException} если указанный телефонный номер не принадлежит
     * текущей сети
     */
    public UDRReport getUDRReport(String clientPhoneNumber, LocalDateTime startDate) {
        List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartBetween(clientPhoneNumber, startDate.minusSeconds(1), startDate.plusMonths(1).plusSeconds(1));
        return getUDRReportFromList(clientPhoneNumber, listCalls);
    }

    /**
     * Данный метод генерирует UDR запрос для абонента с указанным номером телефона за весь отчетный период
     *
     * @param clientPhoneNumber номер телефона абонента текущей сети
     * @return запрошенный отчет, либо {@code IllegalArgumentException} если указанный телефонный номер не принадлежит
     * текущей сети
     */
    public UDRReport getUDRReportAllTime(String clientPhoneNumber) {
        List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumber(clientPhoneNumber);
        return getUDRReportFromList(clientPhoneNumber, listCalls);
    }

    /**
     * Вспомогательный метод для формирования UDR отчета из переданного списка CDR. ВНИМАНИЕ: данный метод проводит
     * валидацию номера телефона, но не проверяет принадлежность CDR к указанному номеру телефона
     *
     * @param clientPhoneNumber номер телефона абонента текущей сети
     * @param listCalls         список CDR записей на основе которых нужно сформировать отчет
     * @return запрошенный отчет, либо {@code IllegalArgumentException} если указанный телефонный номер не принадлежит
     * текущей сети
     */
    private UDRReport getUDRReportFromList(String clientPhoneNumber, List<CDRRecord> listCalls) {
        if (clientRepository.findByPhoneNumber(clientPhoneNumber).isPresent()) {
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

    /**
     * Данный метод генерирует UDR запрос для всех абонентов на месяц начиная с указанной даты
     *
     * @param startDate дата, начиная с которой отсчитывается месяц для формирования отчета
     * @return запрошенный отчет
     */
    public List<UDRReport> getAllUDRReport(LocalDateTime startDate) {
        Iterable<Client> allClients = clientRepository.findAll();
        List<UDRReport> UDRReports = new LinkedList<>();
        for (Client client : allClients) {
            UDRReports.add(getUDRReport(client.getPhoneNumber(), startDate));
        }
        return UDRReports;
    }

    /**
     * Данный метод генерирует UDR запрос для всех абонентов за весь отчетный период
     *
     * @return запрошенный отчет
     */
    public List<UDRReport> getAllUDRReportAllTime() {
        Iterable<Client> allClients = clientRepository.findAll();
        List<UDRReport> UDRReports = new LinkedList<>();
        for (Client client : allClients) {
            UDRReports.add(getUDRReportAllTime(client.getPhoneNumber()));
        }
        return UDRReports;
    }

    /**
     * Данный метод сохраняет CDR отчет для указанного абонента за некоторый промежуток времени.
     *
     * @param clientPhoneNumber номер телефона абонента текущей сети
     * @param startDate         дата, начало промежутка для выбора записей, что войдут в отчет
     * @param endDate           дата, конец промежутка для выбора записей, что войдут в отчет
     * @return UUID сгенерированного отчета. Отчет будет лежать по пути: resources/[UUID].csv
     * @throws NoSuchAlgorithmException если не найдено алгоритма SHA-256
     * @throws IOException              если невозможно создать отчет
     */
    public String createCDR(String clientPhoneNumber, LocalDateTime startDate, LocalDateTime endDate) throws NoSuchAlgorithmException, IOException {
        Files.createDirectories(Paths.get("reports/"));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        StringBuilder sb = new StringBuilder();
        if (clientRepository.findByPhoneNumber(clientPhoneNumber).isPresent()) {
            List<CDRRecord> listCalls = CDRRecordRepository.getCDRRecordsByClientPhoneNumberAndTimeStartBetween(clientPhoneNumber, startDate.minusSeconds(1), endDate.plusSeconds(1));
            listCalls.sort(new Utils.CmpCDR());
            for (CDRRecord cdrRecord : listCalls) {
                sb.append(cdrRecord.toCSVTypeString()).append("\n");
                digest.update(sb.toString().getBytes());
            }
            String UUID = clientPhoneNumber.substring(clientPhoneNumber.length() - 7) + "-" + Utils.bytesToHex(digest.digest());
            FileWriter f = new FileWriter("reports/" + UUID + ".csv");
            f.write(sb.toString());
            logger.info("Created CSV report {}.csv for client: {} ", UUID, clientPhoneNumber);
            f.close();
            return UUID;
        }
        throw new IllegalArgumentException("Client not in company's database");
    }

    @PostConstruct
    private void generateCDRs() {
        cdrGenerator.generateClients(generateClients);
        LocalDateTime startDate = LocalDateTime.now();
        cdrGenerator.generateCDRs(generateCDRs, startDate, startDate.plusYears(1), 4 * 3600);
        logger.info("Generated phone numbers:");
        for (Client client : clientRepository.findAll()) {
            logger.info(client.getPhoneNumber());
        }
    }
}
