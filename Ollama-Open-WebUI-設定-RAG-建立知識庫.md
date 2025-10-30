## RAG 文件格式與系統資源限制

- 支援檔案格式：**txt、Markdown、PDF、Parquet**
- 單檔大小限制：**不超過 1024 個中文字元**
- 上下文長度限制：預設 **2048 token**，如需處理更長文本，可在 Open WebUI 管理員控制台調整至 **8000 token 以上**
- 系統資源需求：上下文長度增加會提升 **VRAM 與 RAM** 使用量，需確保系統可承載
## 嵌入模型與文件搜尋設定

- docker-compose.yml
```yaml
services:
  tika: # 內容擷取引擎 Apache Tika
    image: apache/tika:latest-full
    restart: on-failure
    ports:
      - "9998:9998"
```
- 在 Open WebUI，進入「管理員設定」→「Ollama 模型」，下載嵌入模型 mxbai-embed-large
- 文件頁面設定：嵌入模型引擎選 **Ollama**，嵌入模型選 **mxbai-embed-large**，內容擷取引擎連線至 **Apache Tika** 伺服器。
- 設定查詢參數 **Top K**：一次最多搜尋檔案數，預設 3。若希望模型檢視全部檔案，可將值設定為檔案總數。
## 自訂語言模型與知識庫設定流程

- 打開 Open WebUI，左上角進入「工作區」→「知識」，點「新增知識庫」。
- 上傳所需的 txt 檔案，系統會自動處理並儲存到向量資料庫。
- 切換到「模型」頁面，點「新增自訂語言模型」，選擇現有模型作為基礎（例如 LLaMA 3）。
- 在模型設定中，選擇剛建立的知識庫作為資料來源。
- 嘗試對自訂模型提問，模型會優先從知識庫中尋找答案。
- 若需更長上下文，開啟側邊「Ollama 設定」，調整上下文長度。

---
## 參考來源
- [Ollama + Open WebUI 設定 RAG，建立知識庫強化語言模型能力](https://ivonblog.com/posts/open-webui-rag-knowledge-base/)
- [Retrieval Augmented Generation (RAG) - OpenWebUI Documentation](https://docs.openwebui.com/features/rag/)
- [Tutorial: Configuring RAG with OpenWebUI Documentation](https://docs.openwebui.com/tutorials/tips/rag-tutorial/)