package org.jose;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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

    @ConfigProperty(name = "org.jose.ingest.url")
    List<String> ingest_urls;
    
    @Path("/ingest")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String url_ingest() {

        final HtmlTextExtractor transformer = new HtmlTextExtractor("article", Map.of("title", "h1.page-header-title"), true);

        List<Document> transformedDocuments = new ArrayList<>();

        for (String url : ingest_urls) {
            LOG.info("Ingesting " + url);
            transformedDocuments.add(transformer.transform(UrlDocumentLoader.load(url, new TextDocumentParser())));
        }

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(400, 20, new HuggingFaceTokenizer());
        
        LOG.info("Splitting...");
        List<Document> splitDocuments = documentSplitter
                .splitAll(transformedDocuments)
                .stream()
                .map(split -> new Document(split.text(), split.metadata()))
                .toList();
        
        LOG.info("Storing...");
        EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .build()
                .ingest(splitDocuments);

        return"Ingested " + ingest_urls.size() + " URLs as " + splitDocuments.size() + " documents";
    }

    @Path("/local_ingest")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String local_ingest() {

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
