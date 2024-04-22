package org.jose;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import static java.util.Objects.requireNonNull;

@Path("/chat")
public class ChatService {

    private final Template rchat;
    private final String r = """
        Quaeris quomodo amicum cito facturus sit? Dicam, si illud mihi tecum convenerit, 
        ut statim tibi solvam quod debeo et quantum ad hanc epistulam paria faciamus. 
        Hecaton ait, 'ego tibi monstrabo amatorium sine medicamento, sine herba, sine ullius 
        veneficae carmine: si vis amari, ama'. Habet autem non tantum usus amicitiae veteris
        et certae magnam voluptatem sed etiam initium et comparatio novae.
            """;

    public ChatService(Template rchat) {
        this.rchat = requireNonNull(rchat, "page is required");
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance get(@FormParam("q") String q) {
        return rchat.data("q", q).data("r", r);
    }

}
