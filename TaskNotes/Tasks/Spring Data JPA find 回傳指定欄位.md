---
status: done
priority: normal
scheduled: 2026-01-08T00:00:00.000Z
dateCreated: 2026-01-08T07:01:54.863Z
dateModified: 2026-01-08T07:12:21.588Z
tags:
  - task
gists:
  - id: cb27718689d71852a7664601621e05a9
    url: 'https://gist.github.com/wanyutang/cb27718689d71852a7664601621e05a9'
    createdAt: 2026-01-08T07:12:10.000Z
    updatedAt: '2026-01-08T08:58:46Z'
    filename: winnygist.labs.Spring Data JPA find 回傳指定欄位.md
    isPublic: true
    baseUrl: 'https://api.github.com'
completedDate: 2026-01-08T00:00:00.000Z
---

## 目標

在 Spring Data JPA 中，我們經常需要查詢資料：

- 整個 Entity（例如 findById）
- 單個欄位（使用 Projection Interface，不需整個 entity）

以下示範如何使用 projection interface 取單欄位，並與原生 findById 返回整個 entity 做對比。


## Table of Contents

> Share as Gist 自動生成 Contents


---

## Entity 定義

```java
@Data
@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "POINTS")
    private Long points;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;
}
```

> 示例使用 UserAccount 作為通用使用者資料範例。

---

## 單欄位 Projection Interface 定義

```java
public interface PointsOnly {
    Long getPoints();
}
```

- 只需要取 points 欄位
- 方法名稱與 entity 欄位對應
- 返回型別需與 entity 欄位型別一致

---

## Repository 定義

```java
@Repository
public interface UserAccountRepository 
        extends JpaRepository<UserAccount, String>, JpaSpecificationExecutor<UserAccount> {

    // 單欄位 projection
    PointsOnly findByUserId(String userId);
}
```

- findByUserId 使用 projection interface，只回傳指定欄位
- 查不到資料會回 null
- 無需使用 @Query

---

## 使用示範

### 單欄位取值（Projection）

```java
@Test
@Transactional
void demoProjection() {
    String userId = "user123";
    Long points = Optional.ofNullable(repository.findByUserId(userId))
                          .map(PointsOnly::getPoints)
                          .orElse(null);
    log.debug("userId {} points: {}", userId, points);
}
```

- 查不到資料：points 為 null
- 查到資料：取得 points 值

### 整個 Entity 取值（原生 findById 對比）

```java
@Test
@Transactional
void demoFullEntity() {
    String userId = "user123";
    log.debug("points: {}", 
    Optional.ofNullable(repository.findById(userId).orElse(null))
            .map(UserAccount::getPoints)
            .orElse(null));
}
```

- 查不到資料：返回 null entity
- 查到資料：可以使用整個 entity 的欄位
- 對比 projection：取欄位更輕量，性能更好

---

## 設計說明

- Projection Interface：
    - 只定義需要欄位 getter
    - 回傳型別需對應 entity 欄位
    - 可放同 package 或單獨檔案
- Repository 方法：
    - findByXxx 自動生成 SQL，只抓對應欄位
    - 查不到資料回 null
    - 與 findById 對比，projection 更輕量
- 呼叫方式：
    - 使用 Optional.ofNullable(...).map(...).orElse(...) 避免 NullPointerException
    - Projection 不建立整個 entity，適合只需部分欄位的場景

### 小結

- Projection Interface 可 只取需要欄位，比 findById 輕量且效能好
- 查不到資料安全回傳 null
- 方法通用，可套用到不同使用者資料或其他 entity
- 對比原生 findById：projection 更適合單欄位查詢需求

## 相關連結

- https://github.com/wanyutang/winny-labs/tree/main/Notes
- https://gist.github.com/search?q=winnygist.labs
