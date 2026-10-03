package org.beyondvector.assignments.common;

import java.util.Collection;
import java.util.List;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;

public class HydeQueryTransformer implements QueryTransformer {

    private final ChatModel model;

    public HydeQueryTransformer(ChatModel model) {
        this.model = model;
    }


    @Override
    public Collection<Query> transform(Query query) {
        String hydePrompt = """
            Please write a hypothetical passage or pricing plan description 
            that answers the following user query:
            Query: %s
            """.formatted(query.text());

        String hypotheticalDocument = model.chat(hydePrompt);

        return List.of(Query.from(hypotheticalDocument));
    }

}
