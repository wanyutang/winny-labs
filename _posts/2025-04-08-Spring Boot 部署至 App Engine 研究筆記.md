---
layout: post
title: Spring Boot 部署至 App Engine 研究筆記
date: 2025-04-08 11:40 +08:00
categories:
  - Inbox
tags:
---
tags: ![Java](https://img.shields.io/badge/Language-Java-orange?logo=java&style=flat) ![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-blue?logo=spring&style=flat) ![GCP](https://img.shields.io/badge/Cloud-GCP-brightgreen?logo=googlecloud&style=flat) ![Markdown](https://img.shields.io/badge/Format-Markdown-lightgrey?logo=markdown&style=flat) ![App Engine](https://img.shields.io/badge/Service-App%20Engine-yellow?logo=googlecloud&style=flat) ![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven&style=flat)

## 📁 新增專案

- 新增專案時，**專案 ID 不可重複**

  ![[Screenshot 2025-04-05 at 4.18.41 PM.jpg | 500]]
  ![[Screenshot 2025-04-05 at 4.19.10 PM.jpg | 500]]

- 啟用 Cloud Shell

  ![[Screenshot 2025-04-05 at 11.52.13 AM.jpg | 100]]
  ![[Screenshot 2025-04-05 at 4.21.24 PM.jpg | 300]]
  ![[Screenshot 2025-04-05 at 4.21.07 PM.jpg]]

---

## 🚀 初始化 Spring Boot 並 push 至 GitHub

- 確認已通過驗證：
```bash
gcloud auth list
```

- 輸出範例：

```bash
Credentialed Accounts
ACTIVE  ACCOUNT
*       <my_account>@<my_domain.com>

To set the active account, run:
    $ gcloud config set account `ACCOUNT`
```

- 確認目前的 GCP 專案：
```bash
gcloud config list project
```

- 輸出範例：
```bash
[core]
project = <PROJECT_ID>
```

- 使用 Spring Initializr 指令建置專案：
```bash
curl https://start.spring.io/starter.tgz \
  -d bootVersion=3.3.0 \
  -d dependencies=web \
  -d type=maven-project \
  -d baseDir=gae-standard-example | tar -xzvf -
```

![[Screenshot 2025-04-05 at 5.20.34 PM.jpg | 300]]

- 初始化 Git 並推送到 GitHub：
```bash
cd gae-standard-example
git init
git add .
git commit -m "Initial commit from Spring Initializr"
git branch -M main
git remote add origin https://github.com/<USER NAME>/demo-gae-standard-example.git
git push https://<USER NAME>@github.com/<USER NAME>/demo-gae-standard-example.git -u origin main --force
git push -u https://<YOUR_USERNAME>:<YOUR_TOKEN>@github.com/<YOUR_USERNAME>/demo-gae-standard-example.git main --force
```

`YOUR_TOKEN` 來源請參考 [[GitHub Fine-grained Token 設定說明]]

---

## ⚙️ App Engine 設定調整

- 在 `pom.xml` 新增 App Engine Maven 外掛：
```xml
<build>
  <plugins>
    <plugin>
      <groupId>com.google.cloud.tools</groupId>
      <artifactId>appengine-maven-plugin</artifactId>
      <version>2.4.4</version>
      <configuration>
        <version>1</version>
        <projectId>GCLOUD_CONFIG</projectId>
      </configuration>
    </plugin>
  </plugins>
</build>
```

![[Screenshot 2025-04-05 at 1.55.43 PM.jpg]]

- 建立 `app.yaml`：
```bash
mkdir -p src/main/appengine/
touch src/main/appengine/app.yaml
```

- app.yaml 設定範例：
```yaml
runtime: java17
instance_class: F1
```

  ![[Screenshot 2025-04-05 at 2.00.35 PM.jpg | 500]]

> `F1` 為最小執行個體，正式部署請參考：[[Google App Engine 執行個體類型說明]]

---

## 🖥️ 本機執行程式

- 在 `DemoApplication.java` 上點右鍵 → 選擇「Run Java」
- 瀏覽器會自動開啟服務頁面
  ![[Screenshot 2025-04-05 at 4.25.22 PM.jpg]]
  ![[Screenshot 2025-04-05 at 4.25.48 PM.jpg | 500]]

- 或在下方埠口控制台中點選網址
  ![[Screenshot 2025-04-05 at 4.27.01 PM.jpg]]

---

## ☁️ 建立 App Engine 應用

- App Engine 區域一旦建立後 **無法修改**，若需變更只能刪除整個 GCP 專案重新建立。

### 常見可選區域一覽

| 區域代碼           | 地理位置     | 適用情境                     |
|--------------------|--------------|------------------------------|
| `asia-east1`       | 台灣（新北） | 延遲最低，推薦台灣本地部署   |
| `asia-northeast1`  | 日本（東京） | 亞洲區穩定、與東京服務整合佳 |
| `us-central`       | 美國（愛荷華）| 預設教學範例區域、資源穩定    |

### 建立 App Engine 時指定區域

```bash
# 台灣
gcloud app create --region=asia-east1

# 美國（預設）
gcloud app create --region=us-central

# 日本
gcloud app create --region=asia-northeast1
```

---

## 🚢 部署至 App Engine

使用 Maven 執行部署：
```bash
./mvnw -DskipTests package appengine:deploy
```

![[Screenshot 2025-04-05 at 5.19.28 PM.jpg | 500]]

> 首次部署作業可能需等待數分鐘，系統會自動建立執行環境、負載平衡器等基礎架構。

---

## 🌐 以瀏覽器開啟

- 可至 GCP Console > App Engine > 資訊主頁，取得公開網址
- 亦可使用指令快速開啟：
```bash
gcloud app browse
```

網址範例：
```
http://<project-id>.appspot.com
```

![[Screenshot 2025-04-05 at 5.16.49 PM.jpg]]
![[Screenshot 2025-04-05 at 5.14.48 PM.jpg | 300]]

---
## 🚫 停用或刪除 App Engine 

- App Engine 啟用後無法獨立刪除，只能透過刪除整個專案的方式移除
- 可以透過其他方式停用或降低資源消耗，避免產生費用。
### 停用服務

1. 停用應用程式: 系統將停止所有服務要求![[Screenshot 2025-04-05 at 11.33.51 PM.jpg]]
 2. 停止指定服務版本 (default 服務無停止功能)![[Screenshot 2025-04-05 at 11.35.46 PM.jpg]]
### 降低資源消耗

1. 停用服務版本（非 default）** 
   - 到 GCP Console → App Engine → 版本  
   - 選取版本 → 點選「停止」  
   - 僅適用於非 `default` 的服務版本

2. **調整流量配置**  
   - 將所有流量設為 0 或導向其他服務  
   - 使用 `dispatch.yaml` 控制流量路由

3. **部署最小版本至 default 服務**  
   - 建立一個回傳靜態訊息的最小應用程式  
   - 減少記憶體與執行資源使用

4. **停用 App Engine 應用排程任務（Cron Jobs）**  
   - 刪除 `cron.yaml` 中的所有任務或取消部署

5. **關閉帳單（Billing）**  
   - 導致 App Engine 停止提供資源  
   - 注意會影響整個專案的其他資源使用

---

## 🔗 參考資料

- [將 Spring Boot 應用程式部署至 App Engine 標準環境](https://codelabs.developers.google.com/codelabs/cloud-app-engine-springboot?hl=zh-tw)
- [App Engine 地區與資源限制](https://cloud.google.com/appengine/docs/locations)  
- [gcloud app create 官方文件](https://cloud.google.com/sdk/gcloud/reference/app/create)  
- [Spring Boot 應用部署教學 (Codelab)](https://codelabs.developers.google.com/codelabs/cloud-app-engine-springboot?hl=zh-tw)  
- [App Engine app.yaml reference (Java)](https://cloud.google.com/appengine/docs/standard/reference/app-yaml?tab=java#instance_class)
