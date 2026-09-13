package org.beyondvector.assignments.common;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class DBHelper {

    public static enum DBType {
        IN_MEMORY,
        CHROMA_DB
    }

    public static EmbeddingStore<TextSegment> getEmbeddingStore(DBType dbType) {
        switch (dbType) {
            case IN_MEMORY:
                return getInMemoryDB();
            case CHROMA_DB:
                return getChromaDB();
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
    }

    public static EmbeddingStore<TextSegment> getInMemoryDB() {
        return new InMemoryEmbeddingStore<>();
    }

    public static EmbeddingStore<TextSegment> getChromaDB() {
        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .apiVersion(ChromaApiVersion.V2)
                .databaseName("pricing")
                .build();
        return embeddingStore;
    }
}
