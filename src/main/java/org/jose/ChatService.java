package org.jose;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import static java.util.Objects.requireNonNull;

@Path("/chat")
public class ChatService {

    @Inject
    BotService bot;

    private final Template rchat;

    public ChatService(Template rchat) {
        this.rchat = requireNonNull(rchat, "page is required");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance get(@FormParam("q") String q) {
        String r = bot.chat(q);
        return rchat.data("q", q).data("r", r);
    }

}
