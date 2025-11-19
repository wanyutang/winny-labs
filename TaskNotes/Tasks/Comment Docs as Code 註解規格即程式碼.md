---
status: in-progress
priority: normal
scheduled: 2025-11-18
dateCreated: 2025-11-18T17:42:51.279+08:00
dateModified: 2025-11-19T14:35:23.280+08:00
tags:
  - task
contexts:
  - labs
projects: []
---

以下示範說明如何透過程式碼註解實現文件與程式碼的同步維護

- **程式碼註解哲學：** 什麼時候應該寫行內（Inline）註解，以及寫「為什麼」（Why）而不是「做什麼」（What）的原則
- 註解既然寫在程式裡，也是**需要維護**的，否則必定發生**註解騙人**的情況
- 真要註解，語言的採用還是**英文**為好，省去閱讀者的轉換
	- 看的人是開發與維護的工程師，所以那一種語言會造成"讀者"的轉換，要看讀者是誰
	- 有時開發工程師寫英文自己維護的很好，但交給資淺工程師用英文註解可能就沒相同效果了？
	- 工程師看的懂的註解接近 java 原語法，但溝通時跟客戶交談要轉換成客戶的專有名詞，此時在程式註解標註2者的關係，可以加快 QA 跟工程師對接的認知問題
- 當測試完善時，我們就可以透過測試程式的名字了解被測試方法會**需要驗證那些情境**(Condition -> Result)這就是**最好的註解** (易讀/真實)，以單元測試來說，那些有被測試到的方法，完全不應該再用註解去解釋程式目的
- 如果程式本身寫得好，不用註解也沒關係。架構清晰、邏輯分明，一眼望去就知道在做什麼。而類別、方法、變數一開始就妥善命名，根本不用看註解就知道意思了。只剩下少數特別情況才需要用註解加以說明。

## 什麼是文件即程式碼 (Docs as Code, DaC)

- 文件即程式碼的核心理念是將技術文件視為程式碼，並採用類似的開發工具與流程來管理，包括使用版本控制系統和持續部署流程
- 將技術文件視為程式碼，並採用類似的開發工具與流程來管理，主要涉及以下實踐：
    - 文件格式和位置： 雖然 DaC 運動普遍青睞 Markdown 的簡潔性，並將文件存儲在版本控制系統中，但在 Java 環境中，Javadoc 註解正是將文件寫在程式碼中或緊密貼近程式碼的一種實現方式。這種方式確保了文件更貼近實作，且不容易過時
    - Comment-Driven Development (CDD): 這種開發方法強調先在註解（comments）中清楚寫出設計意圖（要做什麼、為什麼這樣做），然後再實作程式碼。這樣註解同時作為設計說明
    - Design Markers: 這是用程式碼本身（例如 Java 的 marker interface 加上 Javadoc）來表達設計決策的技術。開發者將設計選擇記錄在程式碼裡，而不是放在外部文件中，以降低維護成本

## 設計文件（SDD）與註解的權衡

- 有工程師提出將核心設計寫入註解，而非撰寫完整的系統設計文件（SDD），此觀點與 DaC 理念相符：撰寫完整的設計文件耗時且容易過時
- 將核心設計寫進註解（或 marker-interface）可以使設計與程式碼實作更貼近，並降低維護成本
- 開發者通常傾向於使用基於 Git 的工作流程和像 Markdown 這樣簡單的格式，因此將設計資訊嵌入到程式碼註解中，能更有效地推動開發者貢獻和維護文件

## 註解規範：優化設計要點（整合 DaC 工作流程）

為了讓註解規範更符合 DaC 實踐，可以將其與版本控制、審核和自動化工具結合

### 1. 結構化流程性註解（利用自動化工具）

- 核心要求： 每個方法必須有流程性註解，在方法前用 JavaDoc 清楚描述處理邏輯、流程步驟
- DaC 優化重點：
    - 在 Javadoc 內使用結構化的格式（如 Markdown 項目清單或 `<pre>` 標籤）來列出主要步驟，這有助於利用工具（例如：Doxygen, Javadoc 生成器）自動化生成 API 參考文件
    - DaC 鼓勵利用自動化工具從原始碼或 API 描述檔（如 OpenAPI 規範）中生成文件
    - 對於 Java，Javadoc 工具本身就是執行此類自動化的典範

### 2. 行內註解與一致性標記（利用版本控制的追蹤能力）

- 核心要求： 每個步驟，在程式碼對應行加行內註解，例如使用 `// 1. xxx說明` 標註區域。
- DaC 優化重點：
    - 這種緊鄰程式碼的行內註解，極大地提高了 Git 的差異比較 (Diff) 功能的價值
    - 當開發者提交變更時，審閱者可以透過 Diff 清楚看到程式碼變動與其對應的設計或流程註解是否同步更新，這對於程式碼審核流程（Code Review）至關重要
    - 使用明確標記，可以作為潛在的動態內容或自動化處理的錨點。例如，未來可以開發工具來擷取這些區塊，進行額外的格式化或驗證

### 3. 強制維護一致性（整合審核與品質檢查）

- 核心要求： 當邏輯或流程更新，註解（SD文件內容）也要同步修改。盡量保持註解與程式行為相符。
- DaC 優化重點：
    - 整合 Pull Request (PR) 工作流程： 必須將註解的更新納入程式碼審核流程。
    - 技術文件作者或 SME（主題專家）應參與 PR 審核，以確保程式碼變動時，文件（即註解）同步被更新並保持準確性
    - 實施自動化品質檢查
        - 採用 Git Pre-commit Hooks 或 CI/CD 管線（例如 GitHub Actions）
        - 在程式碼提交或部署前，執行自動化的風格檢查 (Linting) 或驗證腳本
        - 檢查註解的一致性或格式是否符合規範
        - 這種自動化測試是確保文件品質的關鍵 DaC 實踐

### 4. 利於除錯（利用文件作為流程追蹤工具）

- 核心要求： 每個錯誤處理或關鍵分支都要註明 SD 步驟與規則；用明確註解追蹤流程狀態。
- DaC 優化重點
    - 強調概念性文件的整合
        - 錯誤處理規則、關鍵邏輯和流程狀態，這些都屬於 API 概念性主題 (Conceptual Topics) 的一部分
        - 透過清晰的註解，可以確保這些關鍵資訊不會被遺漏
    - 保持單一真相來源
        - 由於註解與程式碼緊密結合，它成為了流程追蹤的單一真相來源
        - 當遇到除錯問題時，開發人員可以直接從程式碼註解中獲取最新的設計意圖和流程細節，從而避免翻閱過時的外部 SDD

## 總結 DaC 帶來的優勢

透過將 SD 文件規範轉化為程式碼註解規範，並採用 DaC 的工作流程，團隊可以獲得以下優勢：

1.  版本控制追溯： 所有的設計變更和文件更新都儲存在 Git 儲存庫中，可以追溯歷史、進行分支管理和輕鬆回溯
2.  提高準確性： 由於審核和自動化工具的強制執行，註解必須與程式碼同步，從而最大程度地減少文件過時的風險
3.  促進協作： 技術文件作者、開發者和 SME 可以透過統一的 Git/PR 流程在程式碼編輯器中協同工作，而不是在獨立的文件系統中工作

> 採用文件即程式碼的方法，就像是將原本分散且易失真的設計圖，直接刻印在產品的製造藍圖上，確保設計和實體產品永遠同步，並能隨時利用標準的工程工具進行審查和更新。

## 範例（Demo 版）——簡化新版，流程註解同步維護

```java 
package com.demo;

/**
 * 開戶確認 Docs as Code 程式註解規範 
 * 全流程註解與程式同步設計範例
 */
public class DemoOpenAccount {

    @Autowired
    AMLComp amlComp;

    @Autowired
    EmailComp emailComp;

    @Autowired
    DEMAPI demAPI;

    /**
     * 開戶流程主要步驟 SD 註解文件
     * <pre>
     * 1. 取得開戶主檔資料
     *     1-1: 取得主檔 DB資料
     *     1-2: 解密身分證字號
     * 2. AML 黑名單檢查
     *     2-1: 呼叫 AML 合規API
     * 3. 呼叫 DEMO379 電文API並根據回傳結果更新主檔
     *     3-1: 若回傳錯誤 -> 狀態設為 E，拋例外
     *     3-2: 若成功 -> 狀態設為 C，記錄開戶帳號
     *      - 開戶電文 DEMO379 電文API = demAPI.sendOpenAccount
     *      - 主檔 DB資料 = db.getAccountInfo
     * 4. FTP 上傳
     *     4-1: 上傳失敗僅記錄log
     * 5. 驗證 Email 認證結果
     * </pre>
     */
    public DemoOpenAccountRes openAcct() {
        // 1-1. 取得主檔 DB資料
        AccountInfo info = db.getAccountInfo(session.getCaseNo());

        // 1-2. 解密身分證字號
        info.setIdNo(EncodeUtil.base64Decode(info.getIdNo()));

        // 2-1. AML 黑名單合規API電文呼叫
        String amlCode = amlComp.check(info);

        // 3. 呼叫 DEMO379 電文API並根據回傳結果更新主檔
        try {
            Demo379Response demoRes = demAPI.sendOpenAccount(info, amlCode);
            if ("E".equals(demoRes.getStatus())) {
                // 3-1. 回傳錯誤，更新主檔狀態E並拋異常
                db.updateStatus(info.getId(), "E");
                throw new Exception("DEMO379 fail");
            } else {
                // 3-2. 成功，更新主檔帳號資訊
                db.updateAccountNo(info.getId(), demoRes.getAccountNo());
            }
        } catch (Exception e) {
            System.out.println("Error on DEMO379: " + e.getMessage());
        }

        // 4. FTP 上傳流程 - 失敗僅log
        try {
            ftpAPI.uploadSeal(info);
        } catch (Exception e) {
            System.out.println("Seal FTP error: " + e.getMessage());
        }

        // 5. Email 認證流程
        boolean mailValid = emailComp.validate(info.getMail());

        return DemoOpenAccountRes.builder()
            .mail(info.getMail())
            .emailVerified(mailValid)
            .build();
    }
}
```

---

## 實施規範重點
- 方法前大註解（可直接複用規格文件內容，且流程有明確步驟）。
- 步驟與程式行位置同步，加程式開發註解做對照。
- 行內註解直接反映規格流程，方便 review 與 debug 。
- 建議每次流程有異動、步驟新增刪除，都同步補充規格與程式註解。
- 可以設計工具/簡化流程，讓規格文件內容與 code 註解自動比對。

## 參考來源

 - [你們在程式碼裡寫註解當作文件，最好的做法是什麼？](https://www.reddit.com/r/webdev/comments/1ae3sx9/what_are_best_practices_for_you_in_terms_of/?tl=zh-hant)
 - [寫註解的必要性？| ithome](https://ithelp.ithome.com.tw/questions/10199978)