package org.beyondvector.assignments.common;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.beyondvector.assignments.a2.PricingAgentWithRAG;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

public class DocumentHelper {

     public List<TextSegment> doFixedLengthChunking(Document document, int chunkSize) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(chunkSize,10);
        return documentSplitter.split(document);
    }

    public Document loadDocumentFromDisk(String fileName) {
        Path documentPath = toPath(fileName);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
        return document;
    }

    public static Path toPath(String fileName) {
        try {
            URL fileUrl = PricingAgentWithRAG.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
