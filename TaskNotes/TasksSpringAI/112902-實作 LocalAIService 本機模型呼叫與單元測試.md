---
status: done
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T16:12:42.649+08:00
dateModified: 2025-11-29T16:19:01.067+08:00
tags:
  - task
completedDate: 2025-11-29
---

## 任務摘要
- 實作 LocalAIService 介面與本機模型呼叫邏輯
- 提供基本的文字輸入 → AI 回應功能
- 撰寫單元測試，驗證輸入輸出與錯誤處理
- 為後續串流 Chat API 與 Spring AI client 打基礎

## 功能範圍
- LocalAIService 介面
- LocalAIServiceImpl 實作（可替換 Provider）
- 方法範例：
- String chat(String prompt)
- String chat(String prompt, String conversationId)
- 回傳標準 ApiResponse 結構
- 錯誤處理與日誌紀錄

## 程式片段

LocalAIService 介面

```java
public interface LocalAIService {
    String chat(String prompt);
    String chat(String prompt, String conversationId);
}
```

- LocalAIServiceImpl

```java
@Service
public class LocalAIServiceImpl implements LocalAIService {

    @Override
    public String chat(String prompt) {
        // 模擬本地模型回應
        return "LocalAI reply: " + prompt;
    }

    @Override
    public String chat(String prompt, String conversationId) {
        // 模擬 session-based 回應
        return "[" + conversationId + "] LocalAI reply: " + prompt;
    }
}
```

- 單元測試範例

```java
@SpringBootTest
public class LocalAIServiceTest {

    @Autowired
    private LocalAIService localAIService;

    @Test
    public void testChatBasic() {
        String input = "Hello AI";
        String output = localAIService.chat(input);
        assertTrue(output.contains(input));
    }

    @Test
    public void testChatWithConversationId() {
        String input = "How are you?";
        String conversationId = "session1";
        String output = localAIService.chat(input, conversationId);
        assertTrue(output.contains(conversationId));
        assertTrue(output.contains(input));
    }
}
```

## 測試重點
- 回應內容包含原始輸入
- session-based 方法可返回帶 conversationId 的回應
- 單元測試可在 CI pipeline 執行
- 模擬異常狀況，驗證 Service 不崩潰

## 下一步建議
- 接入非串流 Chat API endpoint
- 確認 Swagger 顯示 LocalAIService 相關 API
- 後續可替換為 Spring AI client 或遠端 Provider