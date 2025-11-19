---
status: in-progress
priority: normal
scheduled: 2025-11-19
dateCreated: 2025-11-19T16:06:07.463+08:00
dateModified: 2025-11-19T16:06:07.463+08:00
tags:
  - task
---

### 1. 在 PostgreSQL（pg）寫註解 — 何時、怎麼、為什麼寫

- 讓資料庫層（特別是存儲過程、函式）有自主可讀性，不只靠程式碼，也能顯示設計意圖、版本、注意事項
- 何時寫：在建立或修改存儲過程（procedure）、函式（function）時；對於複雜查詢、交易邏輯、異常處理、邊界條件特別重要
- 怎麼寫
	- 使用 COMMENT ON 語法為函式/procedure 加註：例如 COMMENT ON UNCTIFN my_func(...) IS '說明'
		- 這樣註解儲存在資料庫元資料中。參考 Postgres 社群做法
	- 在 PL/pgSQL 程式塊內，用 -- 或 /* … */ 行註解來說明邏輯、為什麼這樣寫，而不是註解「做什麼」
		- 根據程式碼註解的通用原則，註解應該偏重解釋「為何選這種實作」，而非重複程式碼功能
	- 如果是複雜模組或 domain 特定邏輯，可在 PL/pgSQL 前段（頂部）加一段 block comment，說明這個 module 的目的、版本、用例、限制
- 寫註解的目的
    - 降低未來維護成本 — 下次看這段 SQL/PL 的時候能快速理解設計意圖
    - 支援資料庫層邏輯與業務需求的一致性 — 註解可以形成人可讀文件
    - 幫助 code review / DBA review — 可以藉註解檢視設計決策是否合理
---

### 2. 文件即程式碼（Docs as Code）策略調整：讓文件規格與程式邏輯一致

- 問題描述：目前的註解規格文檔（文件即程式碼）與實際程式邏輯（service 層、資料層）相差太多，容易產生需求與實作不一致
- 改進方向
    - 用「規格文件（Spec）與程式碼同步」的方法：先依照 service / domain 邏輯，把文件寫成規格（類似設計文件），再以此為基準實作程式碼
    - 規格文件（document spec）應該模擬實作會做什麼：定義 service 層的 method、輸入／輸出、例外行為、資料驗證
    - 文件版本控管：把規格文件和程式碼一起放在版本控制（Git），確保每次 service 修改，都要同步更新文件規格
    - 驗證機制：在 code review 或 merge 時，同時 review規格文件是否與代碼邏輯對應
- 好處
    - 減少需求誤解／斷層 — 規格文件是單一可信來源
    - 減少迭代溝通成本 — 程式碼變動時可以反過來更新規格，使文件永遠「活著」
    - 提升開發效率與質量 — 開發者不必反覆猜設計意圖
---

### 3. 在主要 Service 層寫小規模文件註解（整合 1 與 2）

- 方式：在 service class / module 上方或方法前，寫「小文件註解區塊」來說明：
    
    1. 方法的目的（為什麼存在）
        
    2. 輸入 / 輸出（參數與回傳）
        
    3. 異常或錯誤行為（什麼情況會 throw / return error）
        
    4. 關鍵邏輯簡介（為什麼這樣實作）
        
    5. 與資料層（如存儲過程）的關聯 — 比如這個 service 調用哪幾個 pg function，彼此關係如何。
        
    
- 範例（伪 code）：
    

```java
/
 * @service: OrderService
 * @method: placeOrder
 * @description: 處理下單流程，驗證庫存、儲存資料、呼叫 PG 存儲過程
 * @params: userId, orderDto（包含 items, paymentInfo …）
 * @returns: OrderResult（訂單 ID / 成功或失敗資訊）
 * @throws: InventoryException（庫存不足）、PaymentException（付款失敗）
 * @db-interaction:
 *   - call pg function check_inventory(items)
 *   - call pg procedure create_order(...)
 */
public OrderResult placeOrder(...) {
    // 核心邏輯 …
}
```

-   
    
- 落實點：
    - 建議每個重要 service method 都寫這樣的「小文件註解」。
    - 可以在 code review 規範中強制要求：方法前註解必須寫齊「目的 /參數 /異常 / db 關係」。
    - 規格文件可以從這些註解部分自動提取（或手動整理）成更正式的技術文件。

---

## 潛在風險與注意事項

- 註解過多：過度註解會變成負擔，尤其是邏輯簡單哪裡都寫註解，未來維護反而難。根據經驗與實踐，註解應該聚焦在「非顯而易見 /決策性邏輯」
- 註解失效：如果只寫在註解裡，沒有強制同步機制，容易過時。為了避免，需要版本控制 + code review 加文件 review
- 一致性：團隊要達成註解與文件規格撰寫的一致性共識（格式、層級、內容範圍）
    
---

如果你願意，我可以幫你把「Comment Docs as Code 註解規格即程式碼.md」這份文件重新設計成符合以上原則的 template，你要我來做這件事嗎？