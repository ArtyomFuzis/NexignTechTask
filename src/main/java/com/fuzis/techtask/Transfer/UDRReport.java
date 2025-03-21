package com.fuzis.techtask.Transfer;

import java.io.Serializable;

public record UDRReport(String msisdn, CallTime incomingCall, CallTime outcomingCall) implements Serializable {

}
