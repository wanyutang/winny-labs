---
status: done
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T15:54:03.772+08:00
dateModified: 2025-11-29T16:12:32.270+08:00
tags:
  - task
completedDate: 2025-11-29
---

- 此任務為 [[112900-Spring AI Chat Integration Story]]Story 的 Phase 1
	- 建立專案骨架並確認 Swagger 能正常開啟
## 任務摘要
- 建置 Spring Boot 專案骨架
- 整合 Swagger UI（SpringDoc OpenAPI）
- 建立統一回應模型 ApiResponse
- 建立 PingController 測試 endpoint
- 確保本機 Swagger 可正常查看 API

## 程式片段
- pom.xml
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>

```

- OpenApiConfig
```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring AI Chat API")
                        .version("v1")
                        .description("API Docs"));
    }
}
```

- ApiResponse 
```java


public class ApiResponse<T> {
    private String traceId;
    private T data;
    private String message;
}
```
- PingController
```java
@RestController
@RequestMapping("/api/v1/ping")
public class PingController {

    @GetMapping
    public ApiResponse<String> ping() {
        ApiResponse<String> resp = new ApiResponse<>();
        resp.setTraceId(UUID.randomUUID().toString());
        resp.setData("pong");
        resp.setMessage("success");
        return resp;
    }
}
```

- Swagger 測試網址: http://localhost:8080/swagger-ui.html

## 備註
- 專案骨架建置： 建立專案骨架並確認 Swagger 能正常開啟
- Swagger UI 整合：API 規範化、Swagger 3.x 整合
- 統一回應模型 ApiResponse：統一 API 回應格式
- PingController 測試 endpoint：本機測試 AI API 前的基礎 endpoint
- Swagger 可正常查看 API：成功啟動專案並在 Swagger 上看到所有 API

## 下一步行動建議
- 尚未建立 單元測試與整合測試
- 尚未規劃 串流 API 與 MemoryManager
- 尚未加入 AIConfig 與多 Provider 支援