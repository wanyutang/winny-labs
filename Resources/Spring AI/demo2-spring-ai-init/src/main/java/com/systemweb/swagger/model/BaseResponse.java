package com.systemweb.swagger.model;

import java.io.Serializable;

public class BaseResponse implements Serializable {
   private static final long serialVersionUID = 1L;
   private String code;
   private String message;
   private boolean success;
   private String status;

   public BaseResponse() {
   }

   public BaseResponse(boolean success, String code, String message) {
      this.success = success;
      this.code = code;
      this.message = message;
   }

   public String getCode() {
      return this.code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean success) {
      this.success = success;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }
}
