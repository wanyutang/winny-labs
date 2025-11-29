package com.systemweb.swagger.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "狀態請求物件")
public class StatusReq {
    @Schema(description = "狀態", required = true, example = "ACTIVE")
    private String status;

    public StatusReq() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
