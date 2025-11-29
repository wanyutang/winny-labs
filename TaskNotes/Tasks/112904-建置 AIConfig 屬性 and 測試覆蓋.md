---
status: done
priority: normal
scheduled: 2025-11-29
dateCreated: 2025-11-29T16:48:45.667+08:00
dateModified: 2025-11-29T16:52:25.820+08:00
tags:
  - task
completedDate: 2025-11-29
---

## 任務摘要
- 建立 AIConfig 設定屬性，集中管理模型、Provider、timeout 與串流開關
- 使用 @ConfigurationProperties 綁定 YAML / properties
- 撰寫單元測試與整合測試，驗證設定正確載入
- 為多模型 Provider 選擇與串流 API 做準備

## 功能範圍
- 建立 AIConfig POJO 並綁定 application.yml
- 屬性範例：
	- provider: local | openai | huggingface
	- model: text-davinci-003
	- timeout: 30s
	- streamingEnabled: true/false
	- 提供 getter / setter
	- 可在 Service 層注入使用
- 測試：
	- 載入 YAML / properties
	- 驗證不同環境 profile 設定
	- 預設值 fallback 測試

## 程式片段

- application.yml

```yaml
ai:
  provider: openai
  model: text-davinci-003
  timeout: 30
  streamingEnabled: true
```

- AIConfig.java

```java
@Configuration
@ConfigurationProperties(prefix = "ai")
public class AIConfig {
    private String provider = "local";
    private String model = "default-model";
    private int timeout = 30;
    private boolean streamingEnabled = false;

    // getter & setter
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }

    public boolean isStreamingEnabled() { return streamingEnabled; }
    public void setStreamingEnabled(boolean streamingEnabled) { this.streamingEnabled = streamingEnabled; }
}
```

- 單元測試 AIConfigTest.java

```java
@SpringBootTest
public class AIConfigTest {

    @Autowired
    private AIConfig aiConfig;

    @Test
    public void testDefaultValues() {
        assertNotNull(aiConfig);
        assertEquals("openai", aiConfig.getProvider());
        assertEquals("text-davinci-003", aiConfig.getModel());
        assertEquals(30, aiConfig.getTimeout());
        assertTrue(aiConfig.isStreamingEnabled());
    }

    @Test
    public void testOverrideValues() {
        System.setProperty("ai.provider", "local");
        System.setProperty("ai.model", "local-model");
        System.setProperty("ai.timeout", "15");
        System.setProperty("ai.streamingEnabled", "false");

        // 重新載入 context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AIConfig.class);
        context.refresh();
        AIConfig config = context.getBean(AIConfig.class);

        assertEquals("local", config.getProvider());
        assertEquals("local-model", config.getModel());
        assertEquals(15, config.getTimeout());
        assertFalse(config.isStreamingEnabled());

        context.close();
    }
}
```

## 測試重點
- 驗證 AIConfig 屬性可正確載入
- 驗證預設值 fallback
- 驗證不同 profile 或系統屬性 override
- 可在 Service 注入使用，確保後續 SpringAIService 與 MemoryManager 可讀取設定

## 下一步建議
- 將 AIConfig 注入 SpringAIService 或 LocalAIService
- 根據 provider 選擇不同 AI client
- 配合串流 API 與 MemoryManager 的 session 設定使用