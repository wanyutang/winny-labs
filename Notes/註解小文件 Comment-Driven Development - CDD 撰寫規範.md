---
status: done
priority: normal
scheduled: 2025-12-17T00:00:00.000Z
dateCreated: 2025-12-17T07:36:33.586Z
dateModified: 2025-12-17T07:57:14.188Z
tags:
  - task
completedDate: 2025-12-17T00:00:00.000Z
gists:
  - id: 3c1a97c5b0497a1cc154bb34e399c500
    url: 'https://gist.github.com/wanyutang/3c1a97c5b0497a1cc154bb34e399c500'
    createdAt: '2025-12-17T07:59:50Z'
    updatedAt: '2025-12-17T08:00:25Z'
    filename: winnygist.labs.註解小文件 Comment-Driven Development - CDD 撰寫規範.md
    isPublic: true
    baseUrl: 'https://api.github.com'
---
# 註解小文件 Comment-Driven Development (CDD) 撰寫規範

- 先寫註解再寫程式，註解即設計說明（Design in Comments）
- 設計決策直接存在程式碼中，而非依賴外部、易過期的文件
- 程式碼與文件高度貼近，降低維護成本
- 透過 JavaDoc、marker interface、annotation 表達設計意圖與限制
- 文件即程式碼（Docs as Code）
- 技術文件與實作共存，避免文件與程式脫鉤
## Table of Contents

> Share as Gist 自動生成 Contents

---
## 為何不寫完整 SDD，而改用註解式開發邏輯？

- 完整 SDD 撰寫成本高，實作後容易過期
- 註解式開發邏輯直接貼近程式流程，修改成本低
- 對工程師而言，閱讀程式碼即等同閱讀設計

適用情境：

- 中大型專案
- 流程複雜、規則多的商業邏輯
- 需要頻繁 debug、交接、新人快速理解

---
## 註解小文件規範

- 每個 public / 核心方法必須有 JavaDoc
- JavaDoc 需描述
	- 為何存在（Why）
	- 整體流程（How）
- 使用條列或 pre 格式，清楚標號流程步驟
- 每個程式處理邏輯步驟需在對應程式碼附近標註
- 註解格式建議
	- // 1: xxx
	- // 2-1: xxx
- 若為區段流程，可使用 start / end 標示
- 修改流程 = 同步修改註解式開發邏輯
- 禁止只寫概念、不反映實際行為的註解
- 關鍵分支與例外處理必須標註程式處理規則
- 註解需說明「為什麼這裡要這樣處理」

---

## Demo 範例

```java
/**
 - 開戶流程 API
 - <pre>
 - 1: 取得開戶主檔資料
 -   1-1: 讀取 DB 主檔
 -   1-2: 解密身分證字號
 - 2: AML 黑名單檢核
 - 3: 呼叫 DEMO379 開戶電文
 -   3-1: 回傳錯誤 -> 狀態 E + 丟出例外
 -   3-2: 成功 -> 更新帳號
 - 4: FTP 上傳（失敗僅記錄 log）
 - 5: Email 認證檢核
 - </pre>
 */
public DemoOpenAccountRes openAcct() {

    // 1-1: 讀取 DB 主檔
    AccountInfo info = db.getAccountInfo(session.getCaseNo());

    // 1-2: 解密身分證字號
    info.setIdNo(EncodeUtil.base64Decode(info.getIdNo()));

    // 2: AML 黑名單檢核
    String amlCode = amlComp.check(info);

    // 3: 呼叫 DEMO379 開戶電文
    try {
        Demo379Response res = demAPI.sendOpenAccount(info, amlCode);

        if ("E".equals(res.getStatus())) {
            // 3-1: 電文失敗，狀態設為 E
            db.updateStatus(info.getId(), "E");
            throw new RuntimeException("DEMO379 fail");
        }

        // 3-2: 電文成功，更新帳號
        db.updateAccountNo(info.getId(), res.getAccountNo());

    } catch (Exception e) {
        log.error("DEMO379 error", e);
    }

    // 4: FTP 上傳（失敗僅 log）
    try {
        ftpAPI.uploadSeal(info);
    } catch (Exception e) {
        log.warn("FTP upload fail", e);
    }

    // 5: Email 認證
    boolean mailValid = emailComp.validate(info.getMail());

    return DemoOpenAccountRes.builder()
        .mail(info.getMail())
        .emailVerified(mailValid)
        .build();
}
```

---
## 實施重點總結

- 方法註解 = 開發流程文件
- 行內註解 = 開發文件與程式的對照錨點
- Review 程式碼同時 Review 設計
- 流程異動時，註解邏輯必須一併調整
- 制定開發註解格式規範（如 x-x）
- 搭配工具檢查註解邏輯步驟與程式是否遺漏

這種做法可有效提升可讀性、維護性與團隊共識，特別適合工程導向團隊。

## 相關連結

- https://github.com/wanyutang/winny-labs/tree/main/Notes
- https://gist.github.com/search?q=winnygist.labs
