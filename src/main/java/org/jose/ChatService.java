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

import java.util.List;

import org.jboss.logging.Logger;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;

@Path("/chat")
public class ChatService {

    private static final Logger LOG = Logger.getLogger(ChatService.class);

    @Inject
    BotService bot;

    @SuppressWarnings("rawtypes")
    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel model;

    private final Template rchat;

    public ChatService(Template rchat) {
        this.rchat = requireNonNull(rchat, "page is required");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Blocking
    public TemplateInstance get(@FormParam("q") String q) {
        @SuppressWarnings("unchecked")
        List<EmbeddingMatch<TextSegment>> relevant = store.findRelevant(model.embed(q).content(), 3, .7);
        LOG.info(relevant.size());

        String r = bot.chat(q);
        return rchat
                .data("relevant", relevant)
                .data("q", q)
                .data("r", r);
    }

}
