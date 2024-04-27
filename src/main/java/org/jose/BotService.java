package org.jose;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface BotService {
    @SystemMessage("""
        Your name is Wayne.
        You are a bot answering questions about Puente Genil.
        You must only use the information provided in the context.
        Respond concisely to the questions asked by the user.
            """)
    public String chat(@UserMessage String message);
}
