---
tags:
  - task
status: done
priority: null
due: null
scheduled: null
projects: null
contexts: null
blockedBy: null
recurrence: null
complete_instances: null
dateModified: 2026-01-09T06:55:16.271Z
completedDate: 2026-01-09T00:00:00.000Z
gists:
  - id: 362cbd5ccae145292b6ee4bfafc1b609
    url: 'https://gist.github.com/wanyutang/362cbd5ccae145292b6ee4bfafc1b609'
    createdAt: '2026-01-09T06:55:52Z'
    updatedAt: '2026-01-09T07:11:35Z'
    filename: winnygist.labs.建置符合泛型安全設計含BaseController-Base Req-Res 設計的 ApiLogAspect.md
    isPublic: true
    baseUrl: 'https://api.github.com'
---

以下提供示範如何在確保 ApiLogAspect 只攔截型別安全的 BaseController<T execute ReqBase, E execute ResBase>，並在 logRequest、logResponse、logException 中正確取得泛型 Request/Response 物件以進行 JSON log。

## Table of Contents

> Share as Gist 自動生成 Contents


## Base Request / Response

### ApiReqBase.java
- 定義所有 API 的統一請求結構，便於驗證與 AOP 攔截
```java
package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiReqBase<T execute ReqBase> {
    private String txDateTime;

    @Valid
    private T params;
}
```

### ApiResBase.java
- 定義所有 API 的統一回應結構，使返回結果標準化
```java
package com.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResBase<T execute ResBase> {
    private String msgCode;
    private String msgContent;
    private T result;
}
```

---

## BaseService
- 提供統一的服務接口，強制傳入 Base Request 並回傳結果
```java
package com.demo.service;

import com.demo.model.ApiReqBase;

public interface BaseService<T execute ReqBase, E execute ResBase> {
    E execute(ApiReqBase<T> request);
}
```

---

## BaseController
- 提供統一的服務接口，強制傳入 Base Request 並回傳結果
```java
package com.demo.controller;

import com.demo.model.ApiReqBase;
import com.demo.model.ApiResBase;
import com.demo.service.BaseService;
import org.springframework.validation.BindingResult;

public abstract class BaseController<T execute ReqBase, E execute ResBase> {

    protected abstract BaseService<T, E> service();

    public abstract String apiUid();

    public ApiResBase<E> execute(ApiReqBase<T> req, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RuntimeException("Invalid request");
        }
        E result = service().execute(req);
        return ApiResBase.<E>builder()
                .msgCode("M0000")
                .msgContent("SUCCESS")
                .result(result)
                .build();
    }
}
```

---

## Demo Service
- 實作 BaseService，處理業務邏輯並回傳 DemoRes
```java
package com.demo.service;

import com.demo.model.ApiReqBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class DemoService implements BaseService<DemoReq, DemoRes> {

    @Override
    public DemoRes execute(ApiReqBase<DemoReq> request) {
        String name = request.getParams().getName();
        if ("error".equalsIgnoreCase(name)) {
            throw new RuntimeException("Demo error");
        }
        return new DemoRes("Hello " + name);
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class DemoReq execute ReqBase {
    private String name;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class DemoRes execute ResBase  {
    private String message;
}
```

---

## Demo Controller
- 繼承 BaseController，接收請求並返回標準化回應
```java
package com.demo.controller;

import com.demo.model.ApiReqBase;
import com.demo.model.ApiResBase;
import com.demo.service.BaseService;
import com.demo.service.DemoService;
import com.demo.service.DemoReq;
import com.demo.service.DemoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo")
public class DemoController extends BaseController<DemoReq, DemoRes> {

    @Autowired
    private DemoService demoService;

    @PostMapping("/hello")
    public ApiResBase<DemoRes> hello(
            @RequestBody ApiReqBase<DemoReq> req,
            BindingResult bindingResult
    ) {
        return execute(req, bindingResult);
    }

    @Override
    protected BaseService<DemoReq, DemoRes> service() {
        return demoService;
    }

    @Override
    public String apiUid() {
        return "DEMO_HELLO";
    }
}
```

---

## PointcutDefinition (Demo)
- 定義 AOP 切點，攔截 Controller 層所有 API 方法
```java
package com.demo.aop;

import org.aspectj.lang.annotation.Pointcut;

public class PointcutDefinition {
    @Pointcut("execution(* com.demo.controller..*(..))")
    public void apiLayer() {}
}
```

---

## ApiLogAspect (Demo JSON log using StringBuilder)
- Base AOP 機制，攔截 BaseController，統一印出請求、回應與異常 JSON log
```java
package com.demo.aop;

import com.demo.controller.BaseController;
import com.demo.model.ApiReqBase;
import com.demo.model.ApiResBase;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(1)
public class ApiLogAspect {

    // 記錄 Request
    @Before("com.demo.aop.PointcutDefinition.apiLayer()")
    public <T execute ReqBase, E execute ResBase> void logRequest(JoinPoint joinPoint) {
        BaseController<T, E> controller = getBaseController(joinPoint);
        ApiReqBase<T> req = findReqArg(joinPoint);
        log.info("[API-REQ] apiUid={}, req={}", controller.apiUid(), ReflectionToStringBuilder.toString(req));
    }

    // 記錄 Response
    @AfterReturning(
            pointcut = "com.demo.aop.PointcutDefinition.apiLayer()",
            returning = "res"
    )
    public <T execute ReqBase, E execute ResBase> void logResponse(JoinPoint joinPoint, ApiResBase<E> res) {
        BaseController<T, E> controller = getBaseController(joinPoint);
        log.info("[API-RES] apiUid={}, res={}", controller.apiUid(),
ReflectionToStringBuilder.toString(res));
    }

    // 記錄 Exception
    @AfterThrowing(
            pointcut = "com.demo.aop.PointcutDefinition.apiLayer()",
            throwing = "ex"
    )
    public <T execute ReqBase, E execute ResBase> void logException(JoinPoint joinPoint, Throwable ex) {
        BaseController<T, E> controller = getBaseController(joinPoint);
        log.error("[API-ERR] apiUid={}, ex={}", controller.apiUid(), ex.getMessage(), ex);
    }

    // 取得 BaseController 並限制泛型
    @SuppressWarnings("unchecked")
    private <T execute ReqBase, E execute ResBase> BaseController<T, E> getBaseController(JoinPoint joinPoint) {
        Object controllerObj = joinPoint.getTarget();
        if (!(controllerObj instanceof BaseController)) {
            throw new RuntimeException("Not a BaseController with execute ReqBase/ResBase");
        }
        return (BaseController<T, E>) controllerObj;
    }

    // 從方法參數中取得 ApiReqBase<T>
    @SuppressWarnings("unchecked")
    private <T execute ReqBase> ApiReqBase<T> findReqArg(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof ApiReqBase) {
                return (ApiReqBase<T>) arg;
            }
        }
        return null;
    }
```

---

## ErrorHandlerController
- 統一捕獲 Exception，返回標準化 ApiResBase 錯誤回應
```java
package com.demo.controller;

import com.demo.model.ApiResBase;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlerController {

    @ExceptionHandler(Exception.class)
    public ApiResBase<Object> handle(Exception e) {
        return ApiResBase.builder()
                .msgCode("M9999")
                .msgContent(e.getMessage())
                .result(null)
                .build();
    }
}
```


## 相關連結

- https://github.com/wanyutang/winny-labs/tree/main/Notes
- https://gist.github.com/search?q=winnygist.labs
