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

## Story 任務卡規劃

### Phase 1: 依賴整合與環境驗證

**CHECK 任務清單:**
- [ ] 檢查 `pom.xml` 中 Spring AI 依賴版本
  - spring-ai-ollama: 1.1.0
  - spring-ai-client-chat: 1.1.0
  - spring-ai-advisors-vector-store: 1.0.0-M8
- [ ] 檢查 SpringDoc OpenAPI 依賴: 2.5.0
- [ ] 驗證 Spring Boot 版本: 3.5.5
- [ ] 驗證 Java 版本: 21
- [ ] 執行 `mvn clean compile` 確認無編譯錯誤
- [ ] 記錄與 Spring-AI 專案的版本差異

**關鍵技術點:**
- Maven 依賴管理
- 版本相容性驗證（Spring Boot 4.0.0 → 3.5.5，Java 17 → 21）

**預期結果:**
✅ 依賴齊全，無需修改 pom.xml
✅ 編譯成功
### Phase 2: 配置檔整合與策略決策

**CHECK 任務清單:**
- [ ] 檢查 `application.yml` 是否包含 Spring AI Ollama 配置
  - base-url: http://localhost:11434
  - model: llama3.1:8b
  
- [ ] **決策點**: AiConfig.java 處理策略
  
  **策略 A (推薦)**: 完全使用 Spring Boot 自動配置
  - ❌ 不複製 `AiConfig.java` 到 demo2
  - ✅ 僅依賴 `application.yml` 配置
  - ✅ Spring AI 自動建立 `OllamaChatModel` Bean
  - **優點**: 簡潔、符合 Spring Boot 最佳實踐
  - **檢查方式**: 建立測試類別注入 `OllamaChatModel` 驗證
  
  **策略 B**: 保留 AiConfig.java 並從 YAML 讀取
  - ✅ 複製並修改 `AiConfig.java`
  - ✅ 使用 `@Value` 或 `@ConfigurationProperties` 注入參數
  - **優點**: 可自訂 RestClient、Retry 策略等進階配置
  - **適用**: 需要更多客製化場景
  
- [ ] **執行決策**: 選擇策略 A 或 B（建議預設選 A）
- [ ] 建立測試類別驗證 Bean 注入成功

**關鍵技術點:**
- Spring Boot 自動配置機制
- OllamaChatModel Bean 管理
- @Value / @ConfigurationProperties 參數注入
- application.yml 配置結構

**預期結果:**
✅ `application.yml` 包含完整 AI 配置
✅ `OllamaChatModel` 可正常自動注入
📝 配置策略決策記錄（選擇了哪種策略及原因）
預期結果:
`application.yml` 包含完整 AI 配置
`OllamaChatModel` 可正常自動注入
配置策略文件記錄（選擇了哪種策略及原因）

---

### Phase 3: Service 層建置

CHECK 任務清單:
- [ ] 檢查 `Spring-AI/.../OllamaService.java` 原始實作
- [ ] 檢查 demo2 專案的 Service 層命名規範
- [ ] 決策點: 確認 Service 方法簽名
  ```java
  // 一次性問答
  public String ask(String question) throws IllegalArgumentException, RuntimeException
  
  // 串流問答
### Phase 3: Service 層建置

**CHECK 任務清單:**
- [ ] 檢查 `Spring-AI/.../OllamaService.java` 原始實作邏輯
- [ ] 檢查 demo2 專案的 Service 層命名規範
- [ ] **決策點**: 確認 Service 方法簽名
  - 一次性問答: `String ask(String question)`
  - 串流問答: `Flux<String> stream(String question)`

**開發任務:**
- [ ] 建立 `com.systemweb.swagger.service.OllamaService`
  - 注入 `OllamaChatModel`（依 Phase 2 策略）
  - 實作 `ask(String question)` 方法
    - 輸入驗證（null、空字串、純空白）
    - 錯誤處理（try-catch）
    - 日誌記錄（@Slf4j）
  - 實作 `stream(String question)` 方法
    - 返回 `Flux<String>` 串流
    - 錯誤處理（onErrorResume）

**測試任務:**
- [ ] 建立 `OllamaServiceTest.java`（最少 6 個測試案例）
  1. ✅ 正常問答
  2. ✅ 空字串輸入
  3. ✅ null 輸入
  4. ✅ 純空白輸入
  5. ✅ 服務異常應拋出 RuntimeException
  6. ✅ 串流空字串輸入應返回 Error
  7. 🆕 超長輸入 (可選)
  8. 🆕 串流正常運作 (可選，需整合測試)
- [ ] 執行測試，確保覆蓋率 ≥ 80%
- [ ] 檢查無 SonarLint/CheckStyle 警告

**關鍵技術點:**
- OllamaChatModel 使用方式
### Phase 4: Controller 層建置與 Swagger 整合

**CHECK 任務清單:**
- [ ] 檢查 `Spring-AI/.../OllamaController.java` 原始實作
- [ ] 檢查 `demo2/.../BaseController.java` 的繼承方法
  - `success(message, status)` 方法簽名
  - `fail(message)` 方法簽名
- [ ] **檢查 `BaseResponse` 結構**（已確認）
  - String code
  - String message（← **AI 回答放這裡**）
  - boolean success
  - String status
  - ⚠️ **無 data 欄位**
  
- [ ] **決策點**: API 回應格式策略（已確認）
  - 成功時: message 欄位包含 AI 回答
  - 失敗時: message 欄位包含錯誤訊息
  
- [ ] **決策點**: 請求模型設計
  - 檔名: `OllamaRequest.java` 還是 `AskRequest.java`？
  - 位置: `model/` 目錄
  - 是否繼承 `BaseRequest`？
  - 是否加入 `@NotBlank` 驗證？

**開發任務:**
- [ ] 建立請求模型（放在 `model/` 目錄）
  - 欄位: String question
  - 驗證: @NotBlank
  - Swagger: @Schema 註解
  
- [ ] 建立 `controller/OllamaController.java`
  - 繼承 `BaseController`
  - 注入 `OllamaService`
  - 實作 `POST /api/ollama/ask`
    - 接收: `@RequestBody` 請求模型
    - 返回: `BaseResponse` (message 包含 AI 回答)
    - 錯誤處理: try-catch
  - 實作 `GET /api/ollama/stream`
    - 接收: `@RequestParam String question`
    - 返回: `Flux<String>` 
    - Content-Type: text/event-stream
  - **完整 Swagger 註解**:
    - @Tag (Controller 類別)
    - @Operation (每個端點)
    - @ApiResponse (成功/失敗範例)
    - @Parameter (參數說明)
    - @ExampleObject (JSON 範例)

**測試任務:**
- [ ] 建立 `OllamaControllerTest.java` (4+ 整合測試)
  1. ✅ 正常問答返回 BaseResponse
  2. ✅ 空字串問題返回失敗 BaseResponse
  3. ✅ 服務異常返回失敗 BaseResponse
  4. ✅ 正常串流返回 SSE
- [ ] 啟動專案驗證 Swagger UI
  - URL: http://localhost:8080/swagger-ui.html
  - 檢查 API 文件完整性
  - 手動測試 `/api/ollama/ask`
  - 手動測試 `/api/ollama/stream` (SSE)

**關鍵技術點:**
- BaseController 繼承與方法使用
- BaseResponse 回應格式統一
- @RequestBody vs @RequestParam
- Server-Sent Events (SSE) 串流
- SpringDoc OpenAPI 註解完整性
- @Tag, @Operation, @ApiResponse, @Parameter, @Schema
- MockMvc 整合測試
- Swagger UI 互動測試

**預期交付:**
✅ `model/` 請求模型類別
✅ `controller/OllamaController.java` (完整 Swagger 註解)
✅ `test/.../OllamaControllerTest.java` (4+ 測試)
✅ Swagger UI 正常顯示且可互動測試
📸 Swagger UI 截圖存檔amaController.java` (完整 Swagger 註解)
`test/.../OllamaControllerTest.java` (4+ 測試)
Swagger UI 正常顯示且可互動測試
Swagger UI 截圖存檔

---

### Phase 5: 前端整合（待規劃）

### Phase 5: 前端整合（待規劃）

**CHECK 任務清單:**
- [ ] **決策點**: 前端技術選型
  
  **選項 A: 快速驗證 - 靜態頁面 (推薦優先)**
  - 複製 `Spring-AI/static/` 到 demo2 專案
  - 更新 API 呼叫邏輯（POST + BaseResponse 解析）
  - **優點**: 快速驗證功能，最小變更
  - **適合**: POC、內部測試
  - **工作量**: 約 2-4 小時
  
  **選項 B: 現代前端框架**
  - 使用 React/Vue + Vite 獨立專案
  - **優點**: 現代化、可擴展、元件化
  - **適合**: 正式產品、長期維護
  - **工作量**: 約 2-5 天
  
  **選項 C: 僅提供 API**
  - 不提供前端，僅透過 Swagger UI 測試
  - **優點**: 後端專注，前後端分離
  - **適合**: API 服務、微服務架構
  - **工作量**: 0（本階段跳過）

- [ ] **執行決策**: 選擇選項 A、B 或 C

---

#### 如選擇 **選項 A: 靜態頁面**

**CHECK 子任務:**
- [ ] 檢查 `Spring-AI/static/` 原始前端檔案
- [ ] 檢查 `chat.js` API 呼叫邏輯（需改為 POST + BaseResponse）
- [ ] 確認 demo2 靜態資源路徑配置

**開發任務:**
- [ ] 複製檔案到 `src/main/resources/static/`
  - chat.html, chat.css, chat.js
- [ ] 更新 `chat.js` API 整合
  - 一次性問答: 改為 POST `/api/ollama/ask` + BaseResponse 解析
  - 串流問答: 改為 GET `/api/ollama/stream` + EventSource
  - 錯誤處理與連線管理

**測試任務:**
- [ ] 手動測試一次性問答
- [ ] 手動測試串流問答
- [ ] 測試錯誤提示顯示
- [ ] 檢查 Console 無 JavaScript 錯誤

**關鍵技術點:**
- Spring Boot 靜態資源配置
- fetch POST API 呼叫
- BaseResponse JSON 解析
- EventSource SSE 接收
- JavaScript 錯誤處理

**預期交付:**
✅ `static/` 前端檔案
✅ 前端與後端 API 完全整合
✅ 功能正常運作
📸 前端操作截圖/錄影

---

#### 如選擇 **選項 B 或 C**

- 📝 建立獨立的前端開發計劃
- 📝 定義 API 契約
- ⏸️ 本 Epic 不包含此部分

**目前狀態**: ⏸️ **Phase 5 待決策後再執行**

## 測試計劃與驗收標準

### Phase 測試檢查表

#### Phase 1 驗收標準
- [ ] `mvn clean compile` 成功
- [ ] 所有依賴正常解析
- [ ] 📝 版本差異文件已記錄

#### Phase 2 驗收標準
- [ ] 配置策略已選擇並記錄（A 或 B）
- [ ] `OllamaChatModel` 可正常注入
- [ ] 測試類別驗證通過

#### Phase 3 驗收標準
- [ ] `OllamaService` 實作完成
- [ ] 單元測試覆蓋率 ≥ 80%
- [ ] 所有測試案例通過 (6+)
- [ ] 無 Code Smell 警告

#### Phase 4 驗收標準
- [ ] `OllamaController` 實作完成
- [ ] Swagger UI 正常顯示
- [ ] 整合測試通過 (4+)
- [ ] API 手動測試通過
  - POST /api/ollama/ask (Swagger UI 測試)
  - GET /api/ollama/stream (Postman/curl 測試)

#### Phase 5 驗收標準（如執行）
- [ ] 前端頁面正常載入
- [ ] 一次性問答功能正常
- [ ] 串流問答功能正常
- [ ] 錯誤處理正確顯示

---

### 整合測試計劃

前置條件檢查:
- [ ] Ollama 服務已啟動: `ollama serve`
- [ ] 模型已下載: `ollama pull llama3.1:8b`
- [ ] demo2 專案已啟動: `mvn spring-boot:run`

測試案例:
### 整合測試計劃

**前置條件檢查:**
- [ ] Ollama 服務已啟動
- [ ] 模型已下載: llama3.1:8b
- [ ] demo2 專案已啟動

**測試案例:**

1. **Ollama 服務連線測試**
   - 檢查 Ollama API 可用性
   - 驗證模型已載入

2. **API 端到端測試 - 一次性問答**
   - Swagger UI 測試 POST `/api/ollama/ask`
   - curl/Postman 測試
   - 驗證 BaseResponse 格式正確

3. **API 端到端測試 - 串流問答**
   - curl -N 測試 GET `/api/ollama/stream`
   - 驗證 SSE 事件流正常

4. **錯誤處理測試**
   - 空問題輸入
   - Ollama 服務關閉情境
   - 超長輸入（10000+ 字元）

5. **效能測試（可選）**
   - 單次問答回應時間 < 5 秒
   - 串流首字回應時間 < 1 秒
   - 並發 10 請求無異常

---

### 測試覆蓋率目標

- **Service 層**: ≥ 80%
- **Controller 層**: ≥ 75%
- **整體**: ≥ 70%

**測試報告工具**: JaCoCo ] Phase 3: `service/OllamaService.java` + 完整錯誤處理
- [ ] Phase 3: `test/.../OllamaServiceTest.java` (6+ 測試案例)
- [ ] Phase 4: `model/OllamaRequest.java` (請求模型)
- [ ] Phase 4: `controller/OllamaController.java` + 完整 Swagger 註解
- [ ] Phase 4: `test/.../OllamaControllerTest.java` (4+ 整合測試)
- [ ] Phase 5: `static/chat.html` + `chat.css` + `chat.js` (如執行)

### 配置檔案
- [ ] Phase 2: `application.yml` 已確認包含 Spring AI 配置
- [ ] Phase 2: 配置策略決策記錄文件

### 文件交付
- [ ] API 文件 (Swagger UI 自動生成)
- [ ] 測試覆蓋率報告 (JaCoCo)
- [ ] Phase 1 版本差異記錄
- [ ] Phase 2 配置策略決策記錄
- [ ] Phase 5 前端技術選型決策記錄（如執行）
- [ ] README.md 使用說明更新

### 驗收截圖/錄影
- [ ] Swagger UI 介面截圖
- [ ] API 測試成功截圖 (Postman/curl)
- [ ] 測試覆蓋率報告截圖
- [ ] 前端操作錄影（如執行 Phase 5）

### 最終檢查項目
- [ ] 所有單元測試通過
- [ ] 整合測試通過
- [ ] Swagger UI 正常顯示且可互動
- [ ] 程式碼無 Compile Error
- [ ] 程式碼無 SonarLint/CheckStyle 警告
- [ ] Git commit 訊息清晰（每個 Phase 獨立 commit）
- [ ] README.md 已更新使用說明

---

### 開發建議
1. 優先順序: Phase 1 → 2 → 3 → 4 → 5
2. 測試驅動: 每完成一個 Phase 立即測試
3. 版本控制: 每個 Phase 建立獨立 commit
4. 文件同步: 即時更新 README 與註解

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

## 🎯 執行順序與 Story 任務卡

依照分析結果，建議按以下順序執行 Story 任務卡:

### Story 1: 環境驗證與依賴確認 (Phase 1)
估時: 0.5 - 1 小時  
優先級: ⭐⭐⭐ 最高  
AC (Acceptance Criteria):
- Maven 編譯成功
- 所有依賴正常解析
- 版本差異文件已記錄

---

### Story 2: 配置檔整合與策略決策 (Phase 2)
估時: 1 - 2 小時  
優先級: ⭐⭐⭐ 最高  
前置依賴: Story 1 完成  
AC (Acceptance Criteria):
- 配置策略已選擇（A 或 B）並記錄原因
- `OllamaChatModel` 可正常自動注入
- 測試類別驗證通過

關鍵決策點:
- [ ] 決定是否需要 `AiConfig.java`（預設建議：不需要）

---

### Story 3: Service 層重構與單元測試 (Phase 3)
估時: 3 - 4 小時  
優先級: ⭐⭐⭐ 最高  
前置依賴: Story 2 完成  
AC (Acceptance Criteria):
- `OllamaService` 實作完成，含錯誤處理
- 單元測試覆蓋率 ≥ 80%
- 所有測試案例通過 (6+)
- 程式碼無 Code Smell

DoD (Definition of Done):
- [ ] Service 類別建立
- [ ] ask() 和 stream() 方法實作
- [ ] 6+ 單元測試全數通過
- [ ] JaCoCo 報告覆蓋率 ≥ 80%

---

### Story 4: Controller 層重構與 Swagger 整合 (Phase 4)
估時: 4 - 6 小時  
優先級: ⭐⭐⭐ 最高  
前置依賴: Story 3 完成  
AC (Acceptance Criteria):
- `OllamaController` 實作完成，含完整 Swagger 註解
- `OllamaRequest` 請求模型建立
- 整合測試通過 (4+)
- Swagger UI 正常顯示且可互動測試
- API 手動測試通過（Postman/curl）

關鍵決策點:
- [ ] 確認 BaseResponse 回應格式（message 欄位放 AI 回答）
- [ ] 決定請求模型檔名（OllamaRequest vs AskRequest）

DoD (Definition of Done):
- [ ] Controller 類別建立
- [ ] POST /api/ollama/ask 實作
- [ ] GET /api/ollama/stream 實作
- [ ] Swagger 註解完整
- [ ] 4+ 整合測試全數通過
- [ ] Swagger UI 手動測試成功

---

### Story 5: 前端整合與端到端測試 (Phase 5)
估時: 2 - 4 小時 (選項 A) / 2-5 天 (選項 B)  
優先級: ⭐⭐ 中等 (可選)  
前置依賴: Story 4 完成  
AC (Acceptance Criteria):
- 前端技術選型已決定
- 如執行選項 A: 靜態頁面正常運作
- 一次性問答功能正常
- 串流問答功能正常
- 錯誤處理正確顯示

關鍵決策點:
- [ ] 選擇前端策略（A: 靜態頁面 / B: 現代框架 / C: 僅 API）

DoD (Definition of Done) (如執行):
- [ ] 前端檔案複製/建立完成
- [ ] API 整合完成
- [ ] 瀏覽器手動測試通過
- [ ] 無 JavaScript 錯誤

---

### Story 6: 整體測試與文件完善
估時: 2 - 3 小時  
優先級: ⭐⭐ 中等  
前置依賴: Story 4 完成 (Story 5 可選)  
AC (Acceptance Criteria):
- 整合測試計劃全數執行通過
- 測試覆蓋率報告產出
- README.md 更新完成
- 所有交付清單項目勾選完成

DoD (Definition of Done):
- [ ] Ollama 端到端測試通過
- [ ] JaCoCo 測試報告產出
- [ ] 文件與截圖齊全
- [ ] Git commit 整理完成
- [ ] 專案可交付

---

## 📊 整體時程估算

- 必要任務 (Story 1-4 + Story 6): 約 11-16 小時
- 含前端 (加上 Story 5 選項 A): 約 13-20 小時
- 完整前端框架 (Story 5 選項 B): 約 3-5 天

---

## ⚠️ 風險與依賴

### 外部依賴
- ⚠️ Ollama 服務必須正常運作
- ⚠️ llama3.1:8b 模型必須已下載
- ⚠️ 網路連線穩定（首次下載模型需時）

### 技術風險
1. Spring Boot 版本差異 (4.0.0 → 3.5.5)
   - 緩解: 優先測試核心功能
2. Java 版本差異 (17 → 21)
   - 緩解: 編譯階段驗證
3. 串流測試複雜度
   - 緩解: 使用整合測試 + 手動測試

---

## 🚀 快速開始

執行前準備:
```bash
# 1. 確認 Ollama 已安裝
ollama --version

# 2. 啟動 Ollama 服務
ollama serve

# 3. 下載模型（首次執行，約 4.7GB）
ollama pull llama3.1:8b

# 4. 驗證模型已載入
ollama list
```

開始執行 Story 1:
```bash
cd demo2-spring-ai-init
mvn clean compile
# 檢查編譯結果，記錄版本資訊
```


## 🚀 快速開始

**執行前準備:**
1. 確認 Ollama 已安裝
2. 啟動 Ollama 服務: `ollama serve`
3. 下載模型（首次約 4.7GB）: `ollama pull llama3.1:8b`
4. 驗證模型已載入: `ollama list`

**開始執行 Story 1:**
- 進入 demo2-spring-ai-init 目錄
- 執行 `mvn clean compile`
- 檢查編譯結果，記錄版本資訊

---

## 📝 API 規格範例

**POST /api/ollama/ask**
- Request: `{"question": "台灣在哪裡？"}`
- Response: `{"success": true, "code": "T0000", "message": "台灣是位於東亞的島嶼...", "status": null}`

**GET /api/ollama/stream**
- Query: `?question=台灣在哪裡`
- Response: text/event-stream (Server-Sent Events)