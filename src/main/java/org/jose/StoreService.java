package org.jose;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/store")
public class StoreService {

    private static final Logger LOG = Logger.getLogger(StoreService.class);

    @SuppressWarnings("rawtypes")
    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel model;
    
    @Path("/ingest")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {

        final String url = "https://visitpuentegenil.es/roman-villa-of-fuente-alamo/";
        LOG.info(url);

        final Document document = UrlDocumentLoader.load(url, new TextDocumentParser());
        document.metadata().add("URL", url);

        final HtmlTextExtractor transformer = new HtmlTextExtractor(".wpb_wrapper", Map.of("title", "h1.entry-title",
                "author", ".td-post-author-name", "date", ".td-post-date", "visits", ".td-post-views"), true);

        Document transformedDocument = transformer.transform(document);

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(400, 20, new HuggingFaceTokenizer());
        
        List<Document> splitDocuments = documentSplitter
                .split(transformedDocument)
                .stream()
                .map(split -> new Document(split.text()))
                .toList();
        
        EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .build()
                .ingest(splitDocuments);

        return"Ingested " + url + " as " + splitDocuments.size() + " documents";
    }

    @Path("/local_ingest")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String locall_ingest() {

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**");
        LOG.info("Ingesting documents... ");
        List<Document> documents = null;
        documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/docs", pathMatcher);
        
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(500,
                40, new HuggingFaceTokenizer());
        List<Document> splitDocuments = documentSplitter
                .splitAll(documents)
                .stream()
                .map(split -> new Document(split.text()))
                .toList();
        EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .build()
                .ingest(splitDocuments);

        return "Ingested " + documents.size() + " files as " + splitDocuments.size() + " documents";
    }
}
