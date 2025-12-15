---
status: done
priority: normal
scheduled: 2025-12-15T00:00:00.000Z
dateCreated: 2025-12-15T05:19:56.189Z
dateModified: 2025-12-15T06:27:34.898Z
tags:
  - task
completedDate: 2025-12-15T00:00:00.000Z
gists:
  - id: 1ec39e5861b34209f91ea3a7f0eac1ee
    url: 'https://gist.github.com/wanyutang/1ec39e5861b34209f91ea3a7f0eac1ee'
    createdAt: '2025-12-15T06:30:45Z'
    updatedAt: '2025-12-15T06:37:11Z'
    filename: winnygist.labs.Sprint 回顧與規劃會議 Sample.md
    isPublic: true
    baseUrl: 'https://api.github.com'
---

# Sprint 回顧與規劃會議 Sample 使用說明

## Table of Contents

> Share as Gist 自動生成 Contents

## 前言

這份 Sample 是一個可重複使用的 Sprint 回顧與規劃會議範本，用來協助團隊在每個 Sprint 週期中，有一致的討論結構與產出標準。可直接複製此文件作為每次 Sprint 會議的會前說明、會中對照清單，或會後補齊紀錄。

## 使用方式

- 會前
    - 複製此 Sample 建立當次 Sprint 會議文件
    - 更新 Sprint 編號、日期與相關標籤
    - 請成員依「會前準備事項」先行整理內容
- 會中
    - 依章節順序進行討論，避免跳題
    - 聚焦事實、結論與可行行動
- 會後
    - 補齊實際討論結論
    - 將改善行動轉為 Story 或 Task
    - 作為下個 Sprint 的參考依據

## 適用情境

- 開發團隊 Sprint Review + Retro + Planning 合併會議
- 小至中型團隊需要快速對齊進度與目標
- 希望降低每次重新想議程的成本，建立穩定節奏
  
此 Sample 可依團隊成熟度與專案性質微調，但建議保留整體結構，以維持 Sprint 討論的一致性與可追蹤性。

```markdown
# Sprint 2 回顧與 Sprint 3 規劃會議說明

## 會議目的

本次會議將同時進行 Sprint 2 回顧 與 Sprint 3 規劃，重點在於對齊目前實際進度、找出需要改善的地方，並確認下一個 Sprint 的可交付目標與工作內容。

---

## 會議時間與對象

- 對象：開發 Team 全體成員
- 會議形式：Sprint Review + Retrospective + Planning 合併進行
- 時間長度：依團隊規模約 1.5～2 小時

---

## Sprint 2 回顧（Review + Retro）

請以事實與具體案例為主，避免抽象描述。
- 已完成項目
    - 本 Sprint 完成的 Story / Task
    - 已交付的 Java 模組、API、批次、設定調整
- 未完成項目
    - 未完成或延遲的工作
    - 主要卡關原因（技術、需求、依賴）
- 技術與品質檢視
    - Code Review 與測試狀況
    - 技術債或潛在風險
- 流程回顧
    - 做得順的地方
    - 卡住或不順的地方
    - 可立即執行的改善行動（1–3 項）

---

## Sprint 3 規劃（Planning）

目標是規劃出「可完成、可交付」的 Sprint。
- Sprint 目標
    - 本 Sprint 要解決的核心問題        
    - 對系統或專案帶來的實際價值
- 工作項目選擇
    - 優先 Story
    - 必要的技術 Task（重構、補測、infra）
- 任務拆解
    - 開發任務（Service、API、Batch、Config）
    - 測試、部署、文件同步
- 風險與依賴
    - 技術不確定性
    - 外部依賴或跨組協作
- 分工與責任
    - 每個項目的主要負責人
    - 是否需要協作或 Review 支援

---

## 會前準備事項

請各位在會議前先完成以下準備，以利會議順利進行。
- 更新各自負責的 Story / Task 狀態
- 整理 Sprint 2 中遇到的問題與建議
- 初步檢視 Sprint 3 可承接的工作量
---

## 會議期望產出

- 清楚的 Sprint 2 回顧結論與改善行動
- 明確的 Sprint 3 目標與任務清單
- 團隊對下一個 Sprint 的共識
  
如對議程或準備事項有疑問，會前可先提出討論。
```

