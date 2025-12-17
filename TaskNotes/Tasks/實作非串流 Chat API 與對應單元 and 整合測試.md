---
status: none
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T16:53:35.251+08:00
dateModified: 2025-12-15T13:19:43.647+08:00
tags:
  - task
---

## 任務摘要
- 實作非串流 Chat API endpoint，支援單次輸入與回應
- 將 SpringAIService 或 LocalAIService 注入 Controller
- 撰寫單元與整合測試，驗證端到端回傳
- 為串流 Chat API 與多 Provider 支援打基礎

## 功能範圍
- Controller：
	- POST /api/chat
	- Request: { "prompt": "string", "conversationId": "optional string" }
	- Response: { "message": "AI 回覆字串", "conversationId": "echo id if provided" }
- Service：
	- SpringAIService 或 LocalAIService 呼叫
	- 封裝非串流邏輯
	- 錯誤處理：
	- 回傳標準 ApiResponse
	- 例外捕捉與 log
- 測試：
	- 單元測試 Controller 與 Service
	- 整合測試 API 端到端呼叫與回覆驗證

## 程式片段

- ChatRequest.java

```java
public class ChatRequest {
    private String prompt;
    private String conversationId;

    // getters & setters
}
```

- ChatResponse.java

```java
public class ChatResponse {
    private String message;
    private String conversationId;

    // getters & setters
}
```

- ChatController.java

```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final SpringAIService aiService;

    @Autowired
    public ChatController(SpringAIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String reply;
        if (request.getConversationId() != null) {
            reply = aiService.chatWithAI(request.getPrompt(), request.getConversationId());
        } else {
            reply = aiService.chatWithAI(request.getPrompt());
        }
        ChatResponse response = new ChatResponse();
        response.setMessage(reply);
        response.setConversationId(request.getConversationId());
        return ResponseEntity.ok(response);
    }
}
```

- 單元測試 ChatControllerTest.java

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testChatEndpoint() throws Exception {
        String json = "{ \"prompt\": \"Hello AI\", \"conversationId\": \"session1\" }";

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.conversationId").value("session1"));
    }
}
```

## 測試重點
- 驗證 API 端點正確回應
- 驗證 conversationId 可 echo 並支援 session-based 呼叫
- Controller 與 Service 的整合測試通過
- 單元測試可在 CI pipeline 執行

## 下一步建議
- 建立串流 Chat API，支援逐段回覆
- 配合 MemoryManager，維護 session-based 記憶
- 整合 AIConfig 支援多 Provider 切換