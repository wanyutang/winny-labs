---
status: done
priority: normal
scheduled: 2026-01-04
dateCreated: 2026-01-04T13:03:38.770+08:00
dateModified: 2026-01-04T13:07:41.788+08:00
tags:
  - task
completedDate: 2026-01-04
---



# Spring Boot AOP TraceId Demo 實作指南

## 目標與前提

- 示範 Web Request 層級的 TraceId 初始化與清理
- 使用 MDC + AOP + Controller 範例驗證效果

## Table of Contents

> Share as Gist 自動生成 Contents


## 套件結構

- com.demo.web.aspect  
  - TraceAspect
- com.demo.web.controller  
  - HelloController
- com.demo.web.pointcut  
  - WebPointcuts
- com.demo.support  
  - LogConstants

## 常數定義 - LogConstants

```java
package com.demo.support;

public final class LogConstants {

    private LogConstants() {}

    public static final String TRACE_ID = "traceId";
    public static final int TRACE_ID_LENGTH = 16;
}
```

## Pointcut 定義 - WebPointcuts

```java
package com.demo.web.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class WebPointcuts {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerLayer() {
    }
}
```

## TraceAspect

```java
package com.demo.web.aspect;

import com.demo.support.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(0)
public class TraceAspect {

    @Before("com.demo.web.pointcut.WebPointcuts.restControllerLayer()")
    public void beforeRequest() {
        String traceId = RandomStringUtils.randomAlphanumeric(LogConstants.TRACE_ID_LENGTH);
        MDC.put(LogConstants.TRACE_ID, traceId);
        log.debug("TraceId initialized: {}", traceId);
    }

    @AfterReturning("com.demo.web.pointcut.WebPointcuts.restControllerLayer()")
    public void afterRequest() {
        MDC.clear();
    }
}
```

## Controller Demo - HelloController

```java
package com.demo.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        log.info("Handling hello request");
        return "hello world";
    }
}
```

## Logback 設定

```xml
<pattern>
    %d{HH:mm:ss.SSS} [%thread] %-5level %logger - traceId=%X{traceId} %msg%n
</pattern>
```

## 驗證方式

- 啟動 Spring Boot
- 呼叫 /hello API
- 觀察 console log 是否包含 traceId
- 多次請求應產生不同 traceId

## 專案的延伸設計方向

- 將 TraceId 改為從 Header 傳入或向下游傳遞
- 加入 UserId、SessionId
- 與 Filter、Interceptor 或 Gateway 整合
- 串接 APM（如 OpenTelemetry）

## 相關連結

- https://github.com/wanyutang/winny-labs/tree/main/Notes
- https://gist.github.com/search?q=winnygist.labs
