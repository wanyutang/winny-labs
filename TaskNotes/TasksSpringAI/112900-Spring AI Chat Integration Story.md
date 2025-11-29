---
status: open
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T11:43:59.657+08:00
dateModified: 2025-11-29T16:44:42.019+08:00
tags:
  - task
---

採由淺入深的分階段實作 - 先 Swagger + 本地模型呼叫，再引入 Spring AI、串流與記憶管理，每個階段搭配單元與整合測試，降低風險並確保可驗證性以下以 Obsidian 筆記格式整理

## Overview
- 目標：在既有 Spring Boot 專案建置可測試、可擴充的 AI Chat API - 含非串流與串流、上下文記憶、多模型支援
- 方法：分階段實作並為每個功能撰寫單元與整合測試，先確認 Swagger 文件與本地模型可呼叫，再逐步引入 Spring AI 與記憶管理
- 成果驗證：Swagger 可見 API、Local AI 可回應、測試覆蓋主要邏輯、上下文隔離驗證通過

## Goals
- 在 swagger-ui 可見所有 AI API
- 本機 AI 模型能被呼叫並回應字串
- Spring AI 客戶端能與本機或遠端模型穩定整合
- AIConfig 可管理模型設定並通過設定載入測試
- 提供一般版與串流版 Chat API
- 支援 session-based 記憶並避免污染
- 每項功能附單元與整合測試

## Scope
- 包含：API 定義、LocalAIService 實作、Spring AI client、串流支援、MemoryManager、AIConfig、多 Provider 選擇
- 不包含：前端 UI - 除 Swagger UI 測試、外部大型模型託管與商業化監控 - 可另作 Epic

## Tasks
- [x] [[112901-建置 Swagger 初始 UI]] ✅ 2025-11-29
- [x] [[112902-實作 LocalAIService 本機模型呼叫與單元測試]] ✅ 2025-11-29
- [x] [[112903-引入 Spring AI 套件並建立基本呼叫樣板與測試]] ✅ 2025-11-29
- [x] [[112904-建置 AIConfig 屬性 and 測試覆蓋]] ✅ 2025-11-29
- [ ] [[112905-實作非串流 Chat API 與對應單元 and 整合測試]]
- [ ] 112906-實作串流 Chat API SSE or WebFlux 與事件順序測試
- [ ] 112907-建構 MemoryManager session-based 與隔離測試
- [ ] 112908-新增多模型 Provider 選擇機制 config and fallback

## Implementation Notes
- API 規範：使用 SpringDoc OpenAPI 3.x 注釋，統一回應模型 - 符合現有 Swagger 標準
- LocalAIService：抽象介面 + Local impl - 便於替換為遠端 Provider
- Spring AI：作為可選 client 層，封裝呼叫邏輯與轉換責任
- 串流：建議採 SSE - Server-Sent Events或用 WebFlux / Reactor 實作，以便逐段回傳
- MemoryManager：以 sessionId 為 key，短期記憶存在 Redis - 可選，記憶存取加 TTL 與容量上限
- Config：使用 @ConfigurationProperties 綁定 AI 參數 - model, provider, timeouts, streamingEnabled
- 測試：Local 模擬 provider - mock 或 embedded，串流測試用 StepVerifier 或自訂事件驗證器

## Testing Strategy
- 單元測試：Service、Config、MemoryManager、Provider adapter
- 整合測試：啟動 Spring context - 測試 profile，對 API 做非串流與串流端到端測試
- 串流驗證：驗證事件順序、分段內容、complete 事件
- 隔離測試：多 session 並發呼叫，確認記憶互不污染

## Acceptance Criteria
- Swagger UI 顯示並能呼叫所有定義 API
- LocalAIService 的單元測試與整合測試通過並回傳預期字串
- 非串流與串流 Chat API 的端到端測試通過 - 包含事件順序
- MemoryManager 通過隔離與並發測試 - session 隔離無污染

## Next Steps
- 建立 Repo 與初始 Spring Boot 骨架 - 含 SpringDoc
- 實作 LocalAIService 抽象與 Mock Provider，先完成非串流 API 測試
- 逐步加入 Spring AI 客戶端、串流支援與 MemoryManager，並為每階段補齊測試