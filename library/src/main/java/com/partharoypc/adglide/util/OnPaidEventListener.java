package com.partharoypc.adglide.util;

public interface OnPaidEventListener {
    void onPaidEvent(double valueMicros, String currencyCode, String precisionType, String adNetwork, String adUnitId);
}
