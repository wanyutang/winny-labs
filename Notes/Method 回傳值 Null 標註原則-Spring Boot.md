---
status: done
priority: normal
scheduled: 2025-12-17
dateCreated: 2025-12-17T14:46:04.093+08:00
dateModified: 2025-12-17T15:17:01.413+08:00
tags:
  - task
completedDate: 2025-12-17
---

本文件說明在 Spring Boot 專案中，如何使用 `@NotNull` 與 `@Nullable` 來明確定義 method 回傳值的 null 行為，以降低 `NullPointerException` 發生風險，並提升程式碼可讀性與 IDE 輔助效果。

---
> 摘要重點：  
> 看到 `@Nullable` → 呼叫端一定要處理 null
## 基本原則

- 通常專案預設 method 回傳值不應為 `null`
	- 僅在「可能回傳 `null`」的情況下標註 `@Nullable`
	- `@NotNull` 用於明確宣告「此方法保證不回傳 `null`」
	- 透過標註，讓 IDE 進行靜態分析，協助提醒潛在的 NPE 問題
- 當 method 標註 `@Nullable` 時 IDE 會提示可能發生 `NullPointerException`
	- 呼叫端未處理 null 的警告
	- 靜態分析工具（如 IntelliJ IDEA、SonarQube、SpotBugs）能更精準進行 null-flow 分析

---

## Method 定義

```java
@NotNull
String getTitle();   // 保證不回傳 null

@Nullable
String getComment(); // 可能回傳 null
```

說明：

- `getTitle()`：呼叫端可直接使用，不需進行 null 檢查  
- `getComment()`：呼叫端必須處理 null 情況

---

## 未處理 @Nullable

```java
String comment = service.getComment();
int length = comment.length(); // IDE 會警告可能發生 NPE
```

## 明確處理 null

```java
String comment = service.getComment();
if (comment != null) {
    int length = comment.length();
}
```

或使用 `Optional`：

```java
Optional.ofNullable(service.getComment())
        .ifPresent(c -> System.out.println(c.length()));
```

- 讓 `@Nullable` 成為「需要特別注意的明確訊號」
- 避免大量標註造成噪音
- 讓 API 的 null contract 一眼可辨
## 參考來源
在檢視 Spring Boot JPA 套件時可以看到各別 function 都有押上 `@Nullable` 的標示，建議大型專案可以參考在共用 function 明確標示 `@Nullable` 的規範以維持專案品質。

![[./attachments/20251217W-MethodNull-SpringBoot.png]]