package com.pdstat.gqlextractor.graal;

import com.pdstat.gqlextractor.service.ResourceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GraalAcornWalker {

    private static final Logger logger = LoggerFactory.getLogger(GraalAcornWalker.class);

    private final ResourceService resourceService;
    private final Resource acornResource;
    private final Resource acornWalkResource;
    private final Resource parseDocsResource;

    private final Engine engine;
    private final ConcurrentHashMap<Long, Context> contextMap = new ConcurrentHashMap<>();
    private String acornScript;
    private String acornWalkScript;
    private String parseScript;

    public GraalAcornWalker(ResourceService resourceService,
                            @org.springframework.beans.factory.annotation.Value("classpath:acorn.js")Resource acornResource,
                            @org.springframework.beans.factory.annotation.Value("classpath:walk.js")Resource acornWalkResource,
                            @org.springframework.beans.factory.annotation.Value("classpath:parseDocs.js")Resource parseDocsResource) {
        this.resourceService = resourceService;
        this.acornResource = acornResource;
        this.acornWalkResource = acornWalkResource;
        this.parseDocsResource = parseDocsResource;
        this.engine = Engine.newBuilder().build();
    }

    @PostConstruct
    void initialise() {
        try {
            // Read script files once and store them as Strings
            this.acornScript = resourceService.readResourceFileContent(acornResource);
            this.acornWalkScript = resourceService.readResourceFileContent(acornWalkResource);
            this.parseScript = resourceService.readResourceFileContent(parseDocsResource);
        } catch (IOException e) {
            logger.error("Error loading JavaScript libraries", e);
            throw new RuntimeException("Failed to initialize GraalAcornWalker", e);
        }
    }

    @PreDestroy
    void cleanup() {
        contextMap.values().forEach(Context::close);
        engine.close();
    }

    public String extractMatchedObjects(String javascript, String visitorScript) {
        try {
            Context context = getContextForThread();

            context.eval("js", "var matchedObjects = [];");

            context.eval("js", parseScript);

            Value parseFunction = context.eval("js", "acorn.parse");
            Value parseJsFunction = context.eval("js", "parseJavascript");

            Value parseAst = parseFunction.execute(javascript,
                    context.eval("js", "({ ecmaVersion: 'latest', sourceType: 'module' })"));

            Value visitorObject = context.eval("js", visitorScript);
            return parseJsFunction.execute(parseAst, visitorObject).asString();
        } catch (PolyglotException e) {
            logger.error("Error during JavaScript execution", e);
            return null;
        }
    }

    private Context getContextForThread() {
        return contextMap.computeIfAbsent(Thread.currentThread().getId(), threadId -> {
            Context ctx = Context.newBuilder("js")
                    .engine(engine)
                    .option("js.shared-array-buffer", "true") // Enables multi-threading support
                    .allowAllAccess(true)
                    .build();

            // Load JavaScript libraries into the context
            ctx.eval("js", acornScript);
            ctx.eval("js", acornWalkScript);
            ctx.eval("js", parseScript);

            return ctx;
        });
    }

}
