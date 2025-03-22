package com.fuzis.techtask.Controllers;

import com.fuzis.techtask.Entities.CDRRecord;
import com.fuzis.techtask.Services.ReportsService;
import com.fuzis.techtask.Services.ValidateService;
import com.fuzis.techtask.Transfer.CDRCreateResponse;
import com.fuzis.techtask.Transfer.UDRAllRequestResponse;
import com.fuzis.techtask.Transfer.UDRReport;
import com.fuzis.techtask.Transfer.UDRRequestResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
public class RestApiController
{
    ValidateService validateService;
    ReportsService reportsService;

    @Autowired
    public RestApiController(ValidateService validateService, ReportsService reportsService){
        this.validateService = validateService;
        this.reportsService = reportsService;
    }
    /**
     * Данный endpoint обрабатывает запросы на получение данных в виде UDR отчета
     * @param phoneNumber телефонный номер абонента, для которого запрашивается UDR отчет (phone number)
     * @param month необязательный параметр: месяц за который нужно сгенерировать UDR отчет.
     *              Если данный параметр не указан, то будет сгенерирован отчет за все время
     * @param year  необязательный параметр: год за который нужно сгенерировать UDR отчет.
     *              Данный параметр обязательно должен присутствовать в запросе, если указан параметр {@code month}.
     *              Если параметр {@code month} не представлен в запросе, то данный параметр будет игнорироваться.
     * @return UDR отчет в json формате по выбранному абоненту за выбранный промежуток.
     *         Либо {@code 400 Bad Request} если указан необязательный параметр {@code month}, но не указан параметр {@code year}
     *
     */
    @GetMapping("/getudr")
    private UDRRequestResponse getUDR(@RequestParam(name="phone_number") String phoneNumber, @RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year, HttpServletResponse response){
        if (month != null && year == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new UDRRequestResponse("err", "Year not set", null);
        }
        if (!validateService.validatePhoneNumber(phoneNumber)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new UDRRequestResponse("err", "Wrong format phone number", null);
        }
        try {
            UDRReport report;
            if(month != null) report = reportsService.getUDRReport(phoneNumber,  LocalDateTime.of(year, month, 1, 0, 0));
            else report = reportsService.getUDRReportAllTime(phoneNumber);
            return new UDRRequestResponse("ok", "", report);
        }
        catch (IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new UDRRequestResponse("err", "Phone number not found in our clients database", null);
        }
    }
    @GetMapping("/getudrall")
    private UDRAllRequestResponse getUDRAll(@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year, HttpServletResponse response){
        if (month != null && year == null)
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new UDRAllRequestResponse("err", "Year not set", null);
        }
        try {
            List<UDRReport> report;
            if(month != null) report = reportsService.getAllUDRReport(LocalDateTime.of(year, month, 1, 0, 0));
            else report = reportsService.getAllUDRReportAllTime();
            return new UDRAllRequestResponse("ok", "", report);
        }
        catch (IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new UDRAllRequestResponse("err", "Phone number not found in our clients database", null);
        }
    }
    @GetMapping("/createcdr")
    private CDRCreateResponse createCDR(@RequestParam(name="phone_number") String phoneNumber, @RequestParam(name="day_start") Integer dayStart, @RequestParam(name="day_end") Integer dayEnd, @RequestParam(name="month_start") Integer monthStart, @RequestParam(name="month_end") Integer monthEnd, @RequestParam(name="year_start") Integer yearStart, @RequestParam(name="year_end") Integer yearEnd, HttpServletResponse response){
        if (!validateService.validatePhoneNumber(phoneNumber)){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new CDRCreateResponse("err", "Wrong format phone number", null);
        }

        LocalDateTime dateStart;
        LocalDateTime dateEnd;
        try {
            dateStart = LocalDateTime.of(yearStart, monthStart, dayStart, 0, 0);
            dateEnd = LocalDateTime.of(yearEnd, monthEnd, dayEnd, 0, 0);
            if(dateEnd.isBefore(dateStart)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new CDRCreateResponse("err", "Date start should be before date end", null);
            }
            String UUID = reportsService.createCDR(phoneNumber, dateStart, dateEnd);
            return new CDRCreateResponse("ok", "", UUID);
        }
        catch (IllegalArgumentException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new CDRCreateResponse("err", "Phone number not found in our clients database", null);
        }
        catch (DateTimeParseException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new CDRCreateResponse("err", "Wrong Dates: " + e.getMessage(), null);
        }
        catch (NoSuchAlgorithmException e){
            response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
            return new CDRCreateResponse("err", "Algorithm SHA-256 required for this function not found on server", null);
        }
        catch (IOException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new CDRCreateResponse("err", "Unable to create report: " + e.getMessage(), null);
        }
    }
}
