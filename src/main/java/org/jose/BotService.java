package org.jose;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface BotService {
    @SystemMessage("""
        Your name is Wayne.
            """)
    public String chat(@UserMessage String message);
}
