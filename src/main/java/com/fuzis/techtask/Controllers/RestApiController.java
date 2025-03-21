package com.fuzis.techtask.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RestApiController
{
    /**
     * Данный endpoint обрабатывает запросы на получение данных в виде UDR отчета
     * @param ph телефонный номер абонента, для которого запрашивается UDR отчет (phone number)
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
    private String getUDR(@RequestParam String ph, @RequestParam(required = true) Integer month, @RequestParam(required = true) Integer year){
        return "None";
    }
}
