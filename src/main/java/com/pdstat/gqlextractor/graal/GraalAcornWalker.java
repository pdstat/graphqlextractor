package com.pdstat.gqlextractor.graal;

import com.pdstat.gqlextractor.service.ResourceService;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GraalAcornWalker {

    private static final Logger logger = LoggerFactory.getLogger(GraalAcornWalker.class);

    private final ResourceService resourceService;
    private final Resource acornResource;
    private final Resource acornWalkResource;
    private final Resource parseDocsResource;

    public GraalAcornWalker(ResourceService resourceService,
                            @org.springframework.beans.factory.annotation.Value("classpath:acorn.js")Resource acornResource,
                            @org.springframework.beans.factory.annotation.Value("classpath:walk.js")Resource acornWalkResource,
                            @org.springframework.beans.factory.annotation.Value("classpath:parseDocs.js")Resource parseDocsResource) {
        this.resourceService = resourceService;
        this.acornResource = acornResource;
        this.acornWalkResource = acornWalkResource;
        this.parseDocsResource = parseDocsResource;
    }

    public String extractMatchedObjects(String javascript, String visitorScript) {
        try (Engine engine = Engine.newBuilder().build(); // Create a GraalVM engine
             Context context = Context.newBuilder("js")
                     .engine(engine).allowAllAccess(true).build()) {

            String acornScript = resourceService.readResourceFileContent(acornResource);
            context.eval("js", acornScript); // Load Acorn
            String acornWalkScript = resourceService.readResourceFileContent(acornWalkResource);
            context.eval("js", acornWalkScript);
            String parseScript = resourceService.readResourceFileContent(parseDocsResource);
            context.eval("js", parseScript);

            Value parseFunction = context.eval("js", "acorn.parse");
            Value parseJsFunction = context.eval("js", "parseJavascript");

            Value parseAst = parseFunction.execute(javascript,
                    context.eval("js", "({ ecmaVersion: 'latest', sourceType: 'module' })"));

            Value visitorObject = context.eval("js", visitorScript);
            return parseJsFunction.execute(parseAst, visitorObject).asString();
        } catch (IOException e) {
            logger.error("Error reading resource", e);
            System.exit(1);
            return null;
        }
    }

}
