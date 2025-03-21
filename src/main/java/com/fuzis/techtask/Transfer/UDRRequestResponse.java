package com.fuzis.techtask.Transfer;


import java.io.Serializable;

public record UDRRequestResponse(String status, String errMsg, UDRReport report) implements Serializable {

}
