package com.example.simple_packet_tracer.dto;

public class FilterRequest {
    private String interfaceName;
    private String bpfFilter;

    // Getter, Setter
    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getBpfFilter() {
        return bpfFilter;
    }

    public void setBpfFilter(String bpfFilter) {
        this.bpfFilter = bpfFilter;
    }
}

