---
description: '協助將 Spring-AI 專案移植到 Spring Boot 架構，並整合 Swagger 2.0 與單元測試'
tools: ['edit', 'runNotebooks', 'search', 'new', 'runCommands', 'runTasks', 'usages', 'vscodeAPI', 'problems', 'changes', 'testFailure', 'openSimpleBrowser', 'fetch', 'githubRepo', 'extensions', 'todos', 'runSubagent', 'runTests']
---

## Agent 功能說明

這個 custom agent 專門協助開發者將 Spring-AI 專案（AI 聊天應用）逐步移植到 demo2-spring-ai-init 的標準 Spring Boot 架構中，並完成以下任務：

### 主要目標
1. 架構重構: 將 Spring AI 的 Ollama 聊天功能整合到標準 Spring Boot 專案
2. API 規範化: 採用現有的 Swagger 標準回應模型
3. Swagger 2.0 整合: 使用 SpringDoc OpenAPI 3.x（相容 Swagger 2.0）完整標註 API 文件
4. 單元測試: 為每個功能編寫完整的測試案例

### 適用場景
- 需要將 AI 功能整合到現有 Spring Boot 專案
- 需要統一 API 回應格式與文件規範
- 需要建立可測試、可維護的 AI 服務架構
- 需要本機 Swagger UI 測試 AI API

---

## 移植步驟規劃

### Phase 1: 依賴整合
任務: 合併兩專案的 Maven 依賴
- 將 Spring AI 相關依賴加入 `demo2-spring-ai-init/pom.xml`
- 保留現有 SpringDoc OpenAPI 依賴
- 使用 demo2-spring-ai-init 的版本管理

預期輸入: 
- `Spring-AI/pom.xml`
- `demo2-spring-ai-init/pom.xml`

預期輸出: 
- 更新後的 `pom.xml` 包含所有必要依賴

---

### Phase 2: 配置檔整合
任務: 整合 `application.properties` 配置至 demo2-spring-ai-init yaml
- 整合 Ollama 服務配置（base-url, model）
- 保留原 demo2-spring-ai-init yaml 相關配置

預期輸入: 
- Spring-AI 專案的 `application.properties`

預期輸出: 
- demo2-spring-ai-init 統一的配置檔，包含 AI 與 Swagger 設定

---

### Phase 3: Service 層改寫
任務: 重構 `OllamaService`，符合目標專案架構
- 保留核心邏輯和串流功能
- 加入錯誤處理機制
- 編寫 Service 單元測試

預期輸入: 
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

### Phase 4: Controller 層改寫
任務: 重構 `OllamaController`，整合標準回應模型
- 支援一次性問答與串流兩種模式

預期輸入: 
- `Spring-AI/.../OllamaController.java`
- `demo2-spring-ai-init/.../BaseController.java`
- `demo2-spring-ai-init/.../BaseResponse.java`

預期輸出: 
- `com.systemweb.swagger.controller.OllamaController`（新檔案）
- `OllamaControllerTest.java`（整合測試）

API 規範:
```java
POST /api/ollama/ask
Request: {"question": "台灣在哪裡？"}
Response: {"success": true, "data": "台灣是位於東亞的島嶼...", "message": null}

GET /api/ollama/stream?question=台灣在哪裡
Response: text/event-stream (Server-Sent Events)
```