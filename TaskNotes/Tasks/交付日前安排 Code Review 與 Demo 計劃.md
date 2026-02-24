---
status: done
priority: normal
scheduled: 2025-12-25T00:00:00.000Z
dateCreated: 2025-12-25T03:38:13.163Z
dateModified: 2025-12-25T03:48:09.692Z
tags:
  - task
completedDate: 2025-12-25T00:00:00.000Z
gists:
  - id: ffdc733769a01000a5da88d54f142904
    url: 'https://gist.github.com/wanyutang/ffdc733769a01000a5da88d54f142904'
    createdAt: '2025-12-25T04:05:29Z'
    updatedAt: '2025-12-25T04:28:22Z'
    filename: winnygist.labs.交付日前安排 Code Review 與 Demo 計劃.md
    isPublic: true
    baseUrl: 'https://api.github.com'
---
                                                                                      # 交付日前安排 Code Review 與 Demo 計劃

回顧過往 Sprint 經驗，部分任務在 Due Date 前缺乏正式 Review，導致問題與風險集中在交付前才被發現，影響交付品質與時程可預期性。

## Table of Contents

> Share as Gist 自動生成 Contents


## 改善作法與原則

- 所有有 Due Date 的任務，需在交付日前安排一次 Code Review 會議
- 由任務負責工程師進行 Demo，說明「目前即可交付」的成果狀態
- Code Review 必須對齊「最終交付型態」，而非中間整理或草稿
  - 若最終交付為 CSV，則 Review 應以接近完成的 CSV 為主
  - 中途使用 DOC 僅作為整理或討論，不作為主要 Review 對象
  - 避免只在中間格式 Review，導致最終交付內容仍不可用
- 以 Java API 開發為例
  - Review 時 API 應已可實際部署並呼叫
  - 不僅檢視結構或草稿，而是驗證行為、錯誤處理與可用性
  - Review 的目標是確認「此狀態是否已具備交付價值」

## 預期成效

- 提前暴露問題與交付風險
- 確保 Review 成果可直接反映在最終交付物
- 提升交付品質、穩定度與時程可預期性
- 降低交付前臨時修改與重工成本

## 驗收方式

- Sprint 中至少一項任務實際於交付日前完成 Code Review 與 Demo
- 任務卡上明確記錄 Review 時間與簡要結論
- 於下次 Sprint Retrospective 回顧此改善項目是否有效提升交付品質

## 相關連結

- https://github.com/wanyutang/winny-labs/tree/main/Notes
- https://gist.github.com/search?q=winnygist.labs
