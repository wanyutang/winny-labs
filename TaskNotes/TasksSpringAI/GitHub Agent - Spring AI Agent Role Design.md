---
status: in-progress
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T12:00:26.447+08:00
dateModified: 2025-11-29T12:00:26.447+08:00
tags:
  - task
---

- 此任務卡做為維護 github agent 的角色設計文件

## Agent 功能說明

參考來源  


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

## 任務計劃與規劃
- 執行計劃與進度要參考 /TaskNotes/TasksSpringAI/ 下的各 md 文件
- 引用並依照參考來源內的程式碼與邏輯完成移植： /TaskNotes/TasksSpringAI/Spring AI Chat v1 Epic.md

## 角色設計使用方式

- .github/agents/spring-ai.agent.md

```markdown
---
description: '協助將 Spring-AI 專案移植到 Spring Boot 架構，並整合 Swagger 2.0 與單元測試'
tools: ['edit', 'runNotebooks', 'search', 'new', 'runCommands', 'runTasks', 'usages', 'vscodeAPI', 'problems', 'changes', 'testFailure', 'openSimpleBrowser', 'fetch', 'githubRepo', 'extensions', 'todos', 'runSubagent', 'runTests']
---

## Agent 功能說明

- 此 Agent 將引用 /TaskNotes/TasksSpringAI/GitHub Agent - Spring AI Agent Role Design.md 中的設計文件內容作為參考來源
- 執行計劃與進度要參考 /TaskNotes/TasksSpringAI/ 下的各 md 文件
- 引用並依照參考來源內的程式碼與邏輯完成移植： /TaskNotes/TasksSpringAI/Spring AI Chat v1 Epic.md
```