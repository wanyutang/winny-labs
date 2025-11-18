

Comment-Driven Development (CDD)
這是一種開發方法：先在註解（comments）中清楚寫出要做什麼、為什麼這樣做，然後再實作程式碼。這樣註解同時作為設計說明。  ￼
	•	Design Markers
這是用程式碼本身（例如 Java 的 marker interface + Javadoc）來表達設計決策的一種技術。開發者將設計選擇記錄在程式碼裡，而不是放在外部文件。  ￼
	•	「文件即程式碼」
有人主張把技術文件寫在程式碼裡（或與程式碼非常貼近），這樣文件會更貼近實作、也比較不會過時。這種方式強調註解的重要性。參考文章：「文件即程式碼：寫技術文件的最佳指南」。

根據你對 Java 程式的註解設計需求，下面整理一份**SD小文件註解規範**與**demo 範例設計**。這種設計能同步維護 SD 文件及程式，幫助開發與 debug：

	•	將設計寫註解 vs. 寫完整 SDD
有文討論在公司或團隊中寫正式設計文件（如 SDD）的成本：寫完整設計文件要時間、容易過時；而把核心設計寫進註解（或 marker-interface）可以更貼近實作、降低維護成本。

---

## SD 小文件註解規範：設計要點

1. **每個方法必須有流程性註解：**
   - 方法前用 JavaDoc 清楚描述處理邏輯、流程步驟。
   - 用 `<pre>` 或項目清單列出主要處理步驟。
2. **每個步驟，在程式碼對應行加行內註解：**
   - 流程步驟可用 `// [Step x] xxx說明` 或 `// SD: xxx` 靠近相關程式碼。
   - 若步驟較多，可用 `// SD: start` 與 `// SD: end` 標註區域。
3. **維護一致性：**
   - 當邏輯或流程更新，註解（SD文件內容）也要同步修改。
   - 盡量保持註解與程式行為相符，不要只貼大方向。
4. **利於 debug：**
   - 每個錯誤處理或關鍵分支都要註明 SD 步驟與規則。
   - 用明確註解追蹤流程狀態。

---

## 範例（Demo 版）——簡化新版，流程註解同步維護

```java name=OpenAccountSDDemo.java
package com.demo.sd;

// Demo: SD 註解版開戶流程
public class OpenAccountSDDemo {

    /**
     * 開戶流程主要步驟 SD 註解
     * <pre>
     * [Step 1] 取得開戶主檔資料
     * [Step 2] AML 黑名單檢查
     * [Step 3] 呼叫 NS359 電文並根據回傳結果更新主檔
     *     3-1: 回傳錯誤 -> 狀態為 E，拋例外
     *     3-2: 成功 -> 狀態為 C，記錄開戶帳號
     * [Step 4] FTP 上傳印鑑系統
     * [Step 5] WebService 上傳印鑑系統（失敗要記錄錯誤）
     * [Step 6] 判斷並加辦信用卡流程
     *     6-1: 舊戶/新戶不同流程
     *     6-2: 失敗時更新加辦項目狀態
     * [Step 7] 加辦一箭雙開（證券帳號）
     * [Step 8] 驗證 Email 認證結果
     * </pre>
     */
    public OpenAcctRes openAcct() {
        // [Step 1] 取得主檔 DB資料
        AccountInfo info = db.getAccountInfo(session.getCaseNo()); // SD: 這裡取主檔資料
        // 解密身分證字號
        info.setIdNo(EncodeUtil.base64Decode(info.getIdNo()));

        // [Step 2] AML 黑名單電文呼叫
        String amlCode = amlAPI.check(info);

        // [Step 3] 呼叫 NS359電文 (重要)
        try {
            NS359Res ns359Res = ns359API.send(info, amlCode); // SD: NS359呼叫，重要開戶狀態判斷
            if ("E".equals(ns359Res.getStatus())) {
                // 3-1: 回傳錯誤，更新錯誤狀態
                db.updateStatus(info.getId(), "E");
                throw new Exception("NS359 fail");
            } else {
                // 3-2: 成功，更新帳號
                db.updateAccountNo(info.getId(), ns359Res.getAccountNo());
            }
        } catch (Exception e) {
            // SD: 異常處理
            System.out.println("Error on NS359: " + e.getMessage());
        }

        // [Step 4] 印鑑 FTP 上傳 (失敗僅log)
        try {
            ftpAPI.uploadSeal(info);
        } catch (Exception e) {
            // SD: FTP失敗僅記錄log
            System.out.println("Seal FTP error: " + e.getMessage());
        }

        // [Step 5] 印鑑 WebService 上傳（失敗記錯誤）
        try {
            sealAPI.upload(info);
        } catch (Exception e) {
            db.updateSealError(info.getId(), "webService failed"); // SD: WebService失敗記錄錯誤
        }

        // [Step 6] 信用卡辦理流程分舊戶新戶
        if (info.isApplyCreditCard()) {
            if (info.isOldCustomer()) {
                // 舊戶流程
                creditAPI.applyOldCustomer(info);
            } else {
                // 新戶流程
                creditAPI.applyNewCustomer(info);
            }
        }

        // [Step 7] 加辦證券帳號
        if (info.isApplySecurities()) {
            securitiesAPI.open(info);
        }

        // [Step 8] email認證
        boolean mailValid = emailAPI.validate(info.getMail());

        // 回傳結果
        return OpenAcctRes.builder()
            .mail(info.getMail())
            .emailVerified(mailValid)
            .build();
    }
}

```

---

## 實施規範重點
- 方法前大註解（可直接複用SD文件內容，且流程有明確步驟）。
- 步驟與程式行位置同步，加 SD 註解做對照。
- 行內註解直接反映 SD 流程，方便 review 與 debug 。
- 建議每次流程有異動、步驟新增刪除，都同步補充 SD 與程式註解。
- 可以設計工具/簡化流程，讓 SD 文件內容與 code 註解自動比對。

---

這樣規範設計有利於團隊協作、快速維護，也方便新手理解核心邏輯。
如需將各步驟"標準化註解格式"再細化，或要做 code 註解與 SD 文件比對自動檢查工具，可以進一步討論付諸實作！