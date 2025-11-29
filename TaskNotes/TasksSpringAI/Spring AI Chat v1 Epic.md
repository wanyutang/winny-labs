---
status: in-progress
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T11:43:59.657+08:00
dateModified: 2025-11-29T11:43:59.657+08:00
tags:
  - task
---

### 主要目標
1. 建置 Spring AI 的聊天功能整合到標準 Spring Boot 專案
2. API 規範化: 採用現有的 Swagger 標準回應模型
3. Swagger 2.0 整合: 使用 SpringDoc OpenAPI 3.x（相容 Swagger 2.0）完整標註 API 文件
4. 單元測試: 為每個功能編寫完整的測試案例

### 適用場景
- 需要將 AI 功能整合到現有 Spring Boot 專案
- 需要統一 API 回應格式與文件規範
- 需要建立可測試、可維護的 AI 服務架構
- 需要本機 Swagger UI 測試 AI API

---

## 建置步驟規劃

### Phase 1: 依賴整合
任務: 整合專案的 Maven 依賴
- 將 Spring AI 相關依賴加入 `demo2-spring-ai-init/pom.xml`
- 保留現有 SpringDoc OpenAPI 依賴
- 使用 demo2-spring-ai-init 的版本管理

預期輸出: 
- 更新後的 `pom.xml` 包含所有必要依賴

---

### Phase 2: 配置檔整合
任務: 整合 `application.yml` 配置
- 整合 Ollama 服務配置（base-url, model）
- 保留原 demo2-spring-ai-init yaml 相關配置

預期輸出: 
- demo2-spring-ai-init 統一的配置檔，包含 Spring AI 相關設定

---

### Phase 3: Service 層建置
任務: 建置 `OllamaService`，符合目標專案架構
- 保留核心邏輯和串流功能
- 加入錯誤處理機制
- 編寫 Service 單元測試

參考來源預: 
- `Spring-AI/.../OllamaService.java`
- `Spring-AI/.../AiConfig.java`

預期輸出: 
- `com.systemweb.swagger.service.OllamaService`（新檔案）
- `OllamaServiceTest.java`（單元測試）

測試涵蓋範圍:
- 正常問答流程
- 空字串或 null 輸入
- Ollama 服務連線異常處理

---

### Phase 4: Controller 層建置
任務: 整合 `OllamaController`，整合標準回應模型
- 支援一次性問答與串流兩種模式

參考來源: 
- `Spring-AI/.../OllamaController.java`
- `demo2-spring-ai-init/.../BaseController.java`
- `demo2-spring-ai-init/.../BaseResponse.java`

預期輸出: 
- `com.systemweb.swagger.controller.OllamaController`（新檔案）
- `OllamaControllerTest.java`（整合測試）

測試案例:
```java
POST /api/ollama/ask
Request: {"question": "台灣在哪裡？"}
Response: {"success": true, "data": "台灣是位於東亞的島嶼...", "message": null}

GET /api/ollama/stream?question=台灣在哪裡
Response: text/event-stream (Server-Sent Events)
```

---

## 📊 專案架構分析與對比

### Spring-AI 原始專案架構
```
Spring-AI/
├── pom.xml (Spring Boot 4.0.0, Java 17)
│   └── Dependencies:
│       ├── spring-boot-starter-webmvc
│       ├── spring-ai-ollama (1.1.0)
│       ├── spring-ai-client-chat (1.1.0)
│       └── spring-ai-advisors-vector-store (1.0.0-M8)
│
├── src/main/java/com/example/demo/
│   ├── SpringAiApplication.java
│   ├── config/
│   │   └── AiConfig.java
│   │       ├── @Bean OllamaApi (手動建立)
│   │       ├── @Bean OllamaChatModel (手動建立)
│   │       └── 硬編碼配置: baseUrl, model
│   ├── controller/
│   │   └── OllamaController.java
│   │       ├── GET /ollama/ask?q=xxx (返回 String)
│   │       ├── GET /ollama/stream?q=xxx (返回 Flux<String>)
│   │       └── 無 Swagger 註解
│   └── service/
│       └── OllamaService.java
│           ├── ask(String q): String
│           └── 簡單封裝 chatModel.call()
│
└── src/main/resources/
    ├── application.properties
    │   └── 配置被註解掉，未使用 Spring Boot 自動配置
    └── static/
        ├── chat.html (前端介面)
        ├── css/chat.css
        └── js/chat.js
            ├── ask() - 呼叫 /ollama/ask
            └── stream() - 未實作 EventSource
```

### demo2-spring-ai-init 目標專案架構
```
demo2-spring-ai-init/
├── pom.xml (Spring Boot 3.5.5, Java 21)
│   └── Dependencies:
│       ├── spring-boot-starter-web
│       ├── springdoc-openapi-starter-webmvc-ui (2.5.0)
│       ├── spring-ai-ollama (1.1.0) ✅ 已包含
│       ├── spring-ai-client-chat (1.1.0) ✅ 已包含
│       └── spring-ai-advisors-vector-store (1.0.0-M8) ✅ 已包含
│
├── src/main/java/com/systemweb/swagger/
│   ├── DemoApplication.java
│   ├── controller/
│   │   ├── BaseController.java
│   │   │   ├── success(message, status): BaseResponse
│   │   │   └── fail(message): BaseResponse
│   │   └── StatusController.java (範例)
│   │       └── 使用 @Tag, @Operation, @RequestBody 等 Swagger 註解
│   ├── model/
│   │   ├── BaseRequest.java (基礎請求模型)
│   │   ├── BaseResponse.java (標準回應模型)
│   │   │   ├── code: String
│   │   │   ├── message: String
│   │   │   ├── success: boolean
│   │   │   └── status: String
│   │   ├── RequestBody.java
│   │   ├── ResponseBody.java
│   │   └── StatusReq.java (範例)
│   ├── constant/
│   │   └── MessageConst.java (訊息常數定義)
│   └── validator/
│       └── EnumNamePatternValidator.java
│
└── src/main/resources/
    └── application.yml
        └── spring.ai.ollama 配置 ✅ 已正確配置
```

---

## 🔍 關鍵差異分析

### 1. **Spring Boot 版本差異**
| 項目 | Spring-AI | demo2-spring-ai-init | 影響 |
|------|-----------|----------------------|------|
| Spring Boot | 4.0.0 | 3.5.5 | API 可能有差異 |
| Java 版本 | 17 | 21 | 需確認語法相容性 |
| Web Starter | webmvc | web | 功能相同 |

### 2. **配置方式差異**
| 項目 | Spring-AI | demo2-spring-ai-init | 移植策略 |
|------|-----------|----------------------|----------|
| 配置方式 | 手動 @Bean + 硬編碼 | application.yml | **移除 AiConfig.java**，使用 Spring Boot 自動配置 |
| OllamaApi | 手動建立 | 自動注入 | 刪除手動建立邏輯 |
| OllamaChatModel | 手動建立 | 自動注入 | 直接注入使用 |
| 配置檔 | properties (未使用) | yml (已配置) | 無需變更 |

### 3. **API 回應格式差異**
| 項目 | Spring-AI | demo2-spring-ai-init | 重構方案 |
|------|-----------|----------------------|----------|
| 一次性問答 | `String` | `BaseResponse<String>` | 包裝成 `BaseResponse` |
| 串流問答 | `Flux<String>` | 保持 `Flux<String>` | **不變更**，SSE 需原始串流 |
| 錯誤處理 | 無 | try-catch + fail() | 新增異常處理 |
| HTTP Status | 200 (總是) | 200/400/500 | 維持 200，用 success 欄位 |

### 4. **Swagger 文件差異**
| 項目 | Spring-AI | demo2-spring-ai-init | 需新增 |
|------|-----------|----------------------|--------|
| Swagger UI | ❌ 無 | ✅ 有 (springdoc) | 保持 |
| @Tag | ❌ | ✅ | 新增類別標籤 |
| @Operation | ❌ | ✅ | 新增方法說明 |
| @Schema | ❌ | ✅ | 新增模型說明 |
| @Parameter | ❌ | ❌ | 新增參數說明 |
| @ApiResponse | ❌ | ✅ | 新增回應範例 |

### 5. **前端整合差異**
| 項目 | Spring-AI | demo2-spring-ai-init | 處理方式 |
|------|-----------|----------------------|----------|
| 靜態頁面 | chat.html + JS | 無 | **Phase 5 新增** |
| API URL | /ollama/ask | /api/ollama/ask | 更新前端 URL |
| 回應格式 | 純字串 | BaseResponse JSON | 更新前端解析邏輯 |
| stream() | 未實作 | 需實作 | 完成 EventSource |

---

## 📝 詳細技術規格補充

### Phase 1: 依賴整合（詳細版）

**任務細項:**
1. ✅ 驗證 Spring AI 依賴已存在於 demo2-spring-ai-init
   - spring-ai-ollama: 1.1.0
   - spring-ai-client-chat: 1.1.0
   - spring-ai-advisors-vector-store: 1.0.0-M8
2. ✅ 驗證 SpringDoc OpenAPI 依賴: 2.5.0
3. ⚠️ 檢查 Spring Boot 版本相容性 (4.0.0 → 3.5.5)

**驗證檢查清單:**
- [ ] `mvn clean compile` 無錯誤
- [ ] 依賴衝突解決（如有）
- [ ] IDE 無紅線錯誤

**預期結果:**
- 無需修改 `pom.xml`（依賴已齊全）
- 文件記錄版本差異注意事項

---

### Phase 2: 配置檔整合（詳細版）

**任務細項:**
1. ✅ 保留現有 `application.yml` 的 Spring AI 配置:
   ```yaml
   spring:
     ai:
       ollama:
         base-url: http://localhost:11434
         chat:
           model: llama3.1:8b
   ```
2. ❌ **移除** `Spring-AI/config/AiConfig.java`（改用自動配置）
3. ✅ 驗證 Spring Boot 自動配置生效

**配置對比:**

**原始手動配置 (需移除):**
```java
@Configuration
public class AiConfig {
    private String ollamaApiURL = "http://localhost:11434";
    private String defaultModel = "llama3.1:8b";
    
    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder().baseUrl(ollamaApiURL).build();
    }
    
    @Bean
    public OllamaChatModel chatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(OllamaChatOptions.builder().model(defaultModel).build())
            .build();
    }
}
```

**新版自動配置 (application.yml):**
```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        model: llama3.1:8b
        options:
          temperature: 0.7  # 可選：控制回答隨機性
          top-p: 0.9        # 可選：控制回答多樣性
```

**驗證方法:**
```java
@Autowired
private OllamaChatModel chatModel; // 直接注入測試
```

**預期結果:**
- 無 `config/AiConfig.java` 檔案
- `application.yml` 包含完整 AI 配置
- OllamaChatModel 可正常注入

---

### Phase 3: Service 層建置（詳細版）

**檔案位置:**
- 來源: `Spring-AI/.../OllamaService.java`
- 目標: `demo2-spring-ai-init/src/main/java/com/systemweb/swagger/service/OllamaService.java`

**原始程式碼:**
```java
@Service
public class OllamaService {
    private final OllamaChatModel chatModel;
    
    public OllamaService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    public String ask(String q) {
        return chatModel.call(q);
    }
}
```

**重構後程式碼:**
```java
package com.systemweb.swagger.service;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OllamaService {
    
    private final OllamaChatModel chatModel;
    
    public OllamaService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    /**
     * 一次性問答
     * @param question 使用者問題
     * @return AI 回答
     * @throws IllegalArgumentException 當問題為空時
     * @throws RuntimeException 當 Ollama 服務異常時
     */
    public String ask(String question) {
        log.info("收到問題: {}", question);
        
        if (question == null || question.trim().isEmpty()) {
            throw new IllegalArgumentException("問題不能為空");
        }
        
        try {
            String answer = chatModel.call(question);
            log.info("AI 回答: {}", answer);
            return answer;
        } catch (Exception e) {
            log.error("Ollama 服務呼叫失敗", e);
            throw new RuntimeException("AI 服務暫時無法使用，請稍後再試", e);
        }
    }
    
    /**
     * 串流問答
     * @param question 使用者問題
     * @return 串流回答
     * @throws IllegalArgumentException 當問題為空時
     */
    public Flux<String> stream(String question) {
        log.info("收到串流問題: {}", question);
        
        if (question == null || question.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("問題不能為空"));
        }
        
        try {
            return chatModel.stream(new Prompt(question))
                .map(chunk -> chunk.getResult().getOutput().getText())
                .doOnError(e -> log.error("串流處理失敗", e))
                .onErrorResume(e -> Flux.just("串流處理發生錯誤，請重試"));
        } catch (Exception e) {
            log.error("串流初始化失敗", e);
            return Flux.error(new RuntimeException("串流服務無法啟動", e));
        }
    }
}
```

**測試案例 (OllamaServiceTest.java):**
```java
package com.systemweb.swagger.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OllamaServiceTest {
    
    @Mock
    private OllamaChatModel chatModel;
    
    @InjectMocks
    private OllamaService ollamaService;
    
    @Test
    void testAsk_正常問答() {
        // Given
        String question = "台灣在哪裡？";
        String expectedAnswer = "台灣是位於東亞的島嶼國家";
        when(chatModel.call(question)).thenReturn(expectedAnswer);
        
        // When
        String answer = ollamaService.ask(question);
        
        // Then
        assertEquals(expectedAnswer, answer);
        verify(chatModel, times(1)).call(question);
    }
    
    @Test
    void testAsk_空字串輸入() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ollamaService.ask(""));
        assertThrows(IllegalArgumentException.class, () -> ollamaService.ask("   "));
        verify(chatModel, never()).call(anyString());
    }
    
    @Test
    void testAsk_null輸入() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> ollamaService.ask(null));
        verify(chatModel, never()).call(anyString());
    }
    
    @Test
    void testAsk_服務異常() {
        // Given
        when(chatModel.call(anyString())).thenThrow(new RuntimeException("連線失敗"));
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> ollamaService.ask("測試問題"));
        assertTrue(exception.getMessage().contains("AI 服務暫時無法使用"));
    }
    
    @Test
    void testStream_正常串流() {
        // Given (需要更複雜的 Mock，可選實作)
        // 此測試可能需要整合測試環境
    }
    
    @Test
    void testStream_空字串輸入() {
        // When
        Flux<String> result = ollamaService.stream("");
        
        // Then
        StepVerifier.create(result)
            .expectError(IllegalArgumentException.class)
            .verify();
    }
}
```

**預期輸出:**
- ✅ `OllamaService.java` (含完整錯誤處理)
- ✅ `OllamaServiceTest.java` (6+ 測試案例)
- ✅ 測試覆蓋率 > 80%

---

### Phase 4: Controller 層建置（詳細版）

**檔案位置:**
- 來源: `Spring-AI/.../OllamaController.java`
- 目標: `demo2-spring-ai-init/src/main/java/com/systemweb/swagger/controller/OllamaController.java`

**重構後完整程式碼:**
```java
package com.systemweb.swagger.controller;

import com.systemweb.swagger.model.BaseResponse;
import com.systemweb.swagger.service.OllamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@Tag(name = "Ollama AI Chat API", description = "基於 Ollama 的 AI 聊天服務")
@RestController
@RequestMapping("/api/ollama")
public class OllamaController extends BaseController {
    
    private final OllamaService ollamaService;
    
    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }
    
    @Operation(
        summary = "一次性 AI 問答",
        description = "向 Ollama AI 提問並等待完整回答。適合短問答或需要完整回應的場景。"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功取得 AI 回答",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponse.class),
                examples = @ExampleObject(
                    name = "成功範例",
                    value = "{\"success\":true,\"code\":\"T0000\",\"message\":\"台灣是位於東亞的島嶼國家，位於太平洋西岸...\",\"status\":null}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "請求參數錯誤",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BaseResponse.class),
                examples = @ExampleObject(
                    name = "錯誤範例",
                    value = "{\"success\":false,\"code\":\"E0001\",\"message\":\"問題不能為空\",\"status\":null}"
                )
            )
        )
    })
    @PostMapping("/ask")
    public BaseResponse ask(
        @Parameter(description = "使用者問題", required = true, example = "台灣在哪裡？")
        @RequestBody AskRequest request
    ) {
        try {
            log.info("收到問答請求: {}", request.getQuestion());
            String answer = ollamaService.ask(request.getQuestion());
            BaseResponse response = success(answer, null);
            return response;
        } catch (IllegalArgumentException e) {
            log.warn("參數驗證失敗: {}", e.getMessage());
            return fail(e.getMessage());
        } catch (Exception e) {
            log.error("AI 問答失敗", e);
            return fail("AI 服務暫時無法使用，請稍後再試");
        }
    }
    
    @Operation(
        summary = "串流 AI 問答",
        description = "向 Ollama AI 提問並即時接收串流回答。使用 Server-Sent Events (SSE) 技術，適合長回答或即時互動場景。"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功建立串流連線",
            content = @Content(
                mediaType = "text/event-stream",
                schema = @Schema(type = "string"),
                examples = @ExampleObject(
                    name = "串流範例",
                    value = "data: 台灣\ndata: 是\ndata: 位於\ndata: 東亞..."
                )
            )
        )
    })
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(
        @Parameter(description = "使用者問題", required = true, example = "台灣在哪裡？")
        @RequestParam String question
    ) {
        log.info("收到串流請求: {}", question);
        return ollamaService.stream(question);
    }
}

// 請求模型
@Schema(description = "AI 問答請求")
class AskRequest {
    @Schema(description = "使用者問題", example = "台灣在哪裡？", required = true)
    private String question;
    
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
}
```

**測試案例 (OllamaControllerTest.java):**
```java
package com.systemweb.swagger.controller;

import com.systemweb.swagger.model.BaseResponse;
import com.systemweb.swagger.service.OllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(OllamaController.class)
class OllamaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OllamaService ollamaService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testAsk_正常問答() throws Exception {
        // Given
        String question = "台灣在哪裡？";
        String answer = "台灣是位於東亞的島嶼國家";
        when(ollamaService.ask(question)).thenReturn(answer);
        
        String requestBody = "{\"question\":\"" + question + "\"}";
        
        // When & Then
        mockMvc.perform(post("/api/ollama/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.code").value("T0000"))
            .andExpect(jsonPath("$.message").value(answer));
        
        verify(ollamaService, times(1)).ask(question);
    }
    
    @Test
    void testAsk_空字串問題() throws Exception {
        // Given
        when(ollamaService.ask(anyString()))
            .thenThrow(new IllegalArgumentException("問題不能為空"));
        
        String requestBody = "{\"question\":\"\"}";
        
        // When & Then
        mockMvc.perform(post("/api/ollama/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.code").value("E0001"))
            .andExpect(jsonPath("$.message").value(containsString("空")));
    }
    
    @Test
    void testAsk_服務異常() throws Exception {
        // Given
        when(ollamaService.ask(anyString()))
            .thenThrow(new RuntimeException("連線失敗"));
        
        String requestBody = "{\"question\":\"測試\"}";
        
        // When & Then
        mockMvc.perform(post("/api/ollama/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("無法使用")));
    }
    
    @Test
    void testStream_正常串流() throws Exception {
        // Given
        Flux<String> mockFlux = Flux.just("台", "灣", "是", "...");
        when(ollamaService.stream(anyString())).thenReturn(mockFlux);
        
        // When & Then
        mockMvc.perform(get("/api/ollama/stream")
                .param("question", "台灣在哪裡？"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE));
    }
}
```

**預期輸出:**
- ✅ `OllamaController.java` (完整 Swagger 註解)
- ✅ `AskRequest.java` (請求模型)
- ✅ `OllamaControllerTest.java` (4+ 整合測試)
- ✅ Swagger UI 可正常顯示 API 文件

---

### Phase 5: 前端整合（新增階段）

**任務:** 移植並更新前端聊天介面

**檔案清單:**
- 來源: `Spring-AI/src/main/resources/static/`
- 目標: `demo2-spring-ai-init/src/main/resources/static/`

**需要更新的內容:**

1. **chat.html** (無需大改)
2. **chat.css** (無需大改)
3. **chat.js** (需大幅修改)

**重構後 chat.js:**
```javascript
const chatBox = document.getElementById("chat-box");
const questionInput = document.getElementById("question");

// 一次性回答: 呼叫 /api/ollama/ask (注意: 改用 POST + BaseResponse)
function ask() {
    const question = questionInput.value.trim();
    
    if(!question) {
        alert("請輸入問題");
        return;
    }
    
    chatBox.innerText += `You: ${question}\n`;
    questionInput.value = '';
    
    fetch('http://localhost:8080/api/ollama/ask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ question: question })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            chatBox.innerText += `AI: ${data.message}\n\n`;
        } else {
            chatBox.innerText += `錯誤: ${data.message}\n\n`;
        }
        chatBox.scrollTop = chatBox.scrollHeight;
    })
    .catch(err => {
        chatBox.innerText += "網路錯誤，請檢查連線\n\n";
        console.error(err);
    });
}

// 逐字回答: 呼叫 /api/ollama/stream
function stream() {
    const question = questionInput.value.trim();
    
    if(!question) {
        alert("請輸入問題");
        return;
    }
    
    chatBox.innerText += `You: ${question}\n`;
    chatBox.innerText += `AI: `;
    questionInput.value = '';
    
    const eventSource = new EventSource(
        `http://localhost:8080/api/ollama/stream?question=${encodeURIComponent(question)}`
    );
    
    eventSource.onmessage = function(event) {
        chatBox.innerText += event.data;
        chatBox.scrollTop = chatBox.scrollHeight;
    };
    
    eventSource.onerror = function(err) {
        console.error("EventSource 錯誤:", err);
        chatBox.innerText += "\n[串流結束]\n\n";
        eventSource.close();
    };
    
    // 自動關閉連線 (避免資源洩漏)
    setTimeout(() => {
        if (eventSource.readyState !== EventSource.CLOSED) {
            eventSource.close();
            chatBox.innerText += "\n\n";
        }
    }, 60000); // 60秒後自動關閉
}
```

**驗證清單:**
- [ ] 前端可正常載入 (http://localhost:8080/chat.html)
- [ ] 一次性問答功能正常
- [ ] 串流問答功能正常
- [ ] 錯誤提示正確顯示
- [ ] UI 互動流暢

**預期輸出:**
- ✅ 完整前端頁面 (HTML + CSS + JS)
- ✅ 前端與後端 API 完全整合
- ✅ EventSource 串流正常運作

---

## 🧪 整體測試計劃

### 單元測試覆蓋率目標
- Service 層: ≥ 80%
- Controller 層: ≥ 75%
- 總體: ≥ 70%

### 整合測試案例
1. **Ollama 服務連線測試**
   - 啟動 Ollama 本機服務
   - 驗證 model 載入正常
   
2. **API 端到端測試**
   - Swagger UI 測試 /api/ollama/ask
   - Swagger UI 測試 /api/ollama/stream
   - Postman 測試完整流程
   
3. **前端整合測試**
   - 瀏覽器測試一次性問答
   - 瀏覽器測試串流問答
   - 測試錯誤處理流程

### 效能測試
- 單次問答回應時間 < 5 秒
- 串流首字回應時間 < 1 秒
- 並發 10 請求無異常

---

## 📦 交付清單

### 程式碼檔案
- [ ] `OllamaService.java` + 測試
- [ ] `OllamaController.java` + 測試
- [ ] `AskRequest.java` (請求模型)
- [ ] `chat.html` + `chat.css` + `chat.js`
- [ ] 更新後的 `application.yml`
- [ ] ~~AiConfig.java~~ (刪除)

### 文件
- [ ] API 文件 (Swagger UI 自動生成)
- [ ] README.md (使用說明)
- [ ] 測試報告 (覆蓋率報告)

### 驗證項目
- [ ] 所有單元測試通過
- [ ] 整合測試通過
- [ ] Swagger UI 正常顯示
- [ ] 前端功能正常
- [ ] 程式碼無 Warning

---

## ⚠️ 風險與注意事項

### 技術風險
1. **Spring Boot 版本差異 (4.0.0 → 3.5.5)**
   - 可能的 API 不相容
   - 建議: 優先測試核心功能
   
2. **Java 版本差異 (17 → 21)**
   - 語法相容性問題
   - 建議: 編譯階段驗證
   
3. **Ollama 服務依賴**
   - 需本機啟動 Ollama
   - 需下載 llama3.1:8b 模型
   - 建議: 提供 Mock 測試模式

### 開發建議
1. **優先順序**: Phase 1 → 2 → 3 → 4 → 5
2. **測試驅動**: 每完成一個 Phase 立即測試
3. **版本控制**: 每個 Phase 建立獨立 commit
4. **文件同步**: 即時更新 README 與註解

---

## 📚 參考資源

### Spring AI 官方文件
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/)
- [Ollama Chat Client](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)

### Swagger 文件規範
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Swagger 2.0 Annotations](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)

### 測試框架
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Test](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

## 🎯 下一步行動建議

依照分析結果，建議按以下順序執行 Story 任務卡:

1. **Story 1: 環境驗證與依賴確認** (Phase 1)
2. **Story 2: 配置檔整合與自動配置遷移** (Phase 2)
3. **Story 3: Service 層重構與單元測試** (Phase 3)
4. **Story 4: Controller 層重構與 Swagger 整合** (Phase 4)
5. **Story 5: 前端整合與端到端測試** (Phase 5)
6. **Story 6: 整體測試與文件完善**

每個 Story 應包含:
- 明確的 AC (Acceptance Criteria)
- 單元測試覆蓋率要求
- 驗收測試腳本
- 完成的定義 (Definition of Done)