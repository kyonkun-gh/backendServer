package com.my.backend.server.enums;

import java.util.HashMap;
import java.util.Map;

public enum DnsType {
    A("A", "1", 1),
    NS("NS", "2", 2),
    CNAME("CNAME", "5", 5),
    SOA("SOA", "6", 6),
    PTR("PTR", "12", 12),
    MX("MX", "15", 15),
    TXT("TXT", "16", 16),
    AAAA("AAAA", "28", 28),
    SRV("SRV", "33", 33),
    CAA("CAA", "257", 257);

    private final String name;
    private final String value;
    private final int code;

    DnsType(String name, String value, int code) {
        this.name = name;
        this.value = value;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public int getCode() {
        return code;
    }

    private static final Map<String, DnsType> NAME_VALUE_MAP = new HashMap<>();

    static {
        for (DnsType type : DnsType.values()) {
            NAME_VALUE_MAP.put(type.getName(), type);
            NAME_VALUE_MAP.put(type.getValue(), type);
        }
    }

    public static DnsType fromNameOrValue(String nameOrValue) {
        return NAME_VALUE_MAP.get(nameOrValue);
    }
}
