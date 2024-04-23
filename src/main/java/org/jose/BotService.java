package org.jose;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface BotService {
    public String chat(@UserMessage String message);
}
