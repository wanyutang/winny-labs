---
status: done
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T16:40:01.932+08:00
dateModified: 2025-11-29T16:44:24.921+08:00
tags:
  - task
completedDate: 2025-11-29
---

## 任務摘要
- 引入 Spring AI 套件至專案
- 建立基本呼叫樣板（Service 層封裝）
- 撰寫單元測試，驗證本地與遠端 AI 呼叫
- 為後續串流 Chat API 與多 Provider 支援打基礎

## 功能範圍
- Spring AI 套件整合
- 建立 SpringAIService 封裝調用邏輯
- 方法範例：
- String chatWithAI(String prompt)
- String chatWithAI(String prompt, String conversationId)
- 支援 LocalAIService 與遠端 Provider 呼叫切換
- 標準 ApiResponse 回傳
- 基礎錯誤處理與日誌

## 程式片段

pom.xml

```xml
<dependency>
    <groupId>com.theokanning.openai</groupId>
    <artifactId>openai-spring-boot-starter</artifactId>
    <version>0.10.0</version>
</dependency>
```

- SpringAIService 介面

```java
public interface SpringAIService {
    String chatWithAI(String prompt);
    String chatWithAI(String prompt, String conversationId);
}
```

- SpringAIServiceImpl

```java
@Service
public class SpringAIServiceImpl implements SpringAIService {

    private final OpenAiService openAiService;

    @Autowired
    public SpringAIServiceImpl(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String chatWithAI(String prompt) {
        // 呼叫 Spring AI client
        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-003")
                .build();
        return openAiService.createCompletion(request)
                .getChoices()
                .get(0)
                .getText();
    }

    @Override
    public String chatWithAI(String prompt, String conversationId) {
        // 可依 conversationId 做 session 管理
        return "[" + conversationId + "] " + chatWithAI(prompt);
    }
}
```

- 單元測試

```java
@SpringBootTest
public class SpringAIServiceTest {

    @Autowired
    private SpringAIService springAIService;

    @Test
    public void testChatWithAI() {
        String input = "Hello AI";
        String output = springAIService.chatWithAI(input);
        assertNotNull(output);
        assertFalse(output.isEmpty());
    }

    @Test
    public void testChatWithAIWithConversationId() {
        String input = "How are you?";
        String conversationId = "session123";
        String output = springAIService.chatWithAI(input, conversationId);
        assertTrue(output.contains(conversationId));
        assertTrue(output.contains("How are you?"));
    }
}
```
## 測試重點
- 驗證 Spring AI client 可正確回傳文字
- 驗證 session-based 呼叫能包含 conversationId
- 單元測試可在 CI pipeline 執行
- 可模擬遠端 Provider 或 LocalAIService 切換

## 下一步建議
- 將 SpringAIService 接入非串流 Chat API endpoint
- 準備串流 Chat API 與 MemoryManager 的整合
- 配合 AIConfig 屬性測試載入與切換 Provider