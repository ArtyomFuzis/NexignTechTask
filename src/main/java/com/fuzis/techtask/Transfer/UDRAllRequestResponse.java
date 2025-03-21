package com.fuzis.techtask.Transfer;

import java.util.List;

public record UDRAllRequestResponse(String status, String errMsg, List<UDRReport> reports)
{
}
