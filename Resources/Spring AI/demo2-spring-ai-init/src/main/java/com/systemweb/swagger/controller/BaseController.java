package com.systemweb.swagger.controller;

import com.systemweb.swagger.model.BaseResponse;

public abstract class BaseController {

    public BaseController() {}

    // 標準成功回應（可設定 status 欄位）
    protected BaseResponse success(String message, String status) {
        BaseResponse res = new BaseResponse();
        res.setSuccess(true);
        res.setCode("T0000");
        res.setMessage(message);
        res.setStatus(status);
        return res;
    }

    // 標準失敗回應
    protected BaseResponse fail(String message) {
        BaseResponse res = new BaseResponse();
        res.setSuccess(false);
        res.setCode("E0001");
        res.setMessage(message);
        return res; 
    }
}
