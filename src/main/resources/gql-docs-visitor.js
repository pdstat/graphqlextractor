({
    VariableDeclarator: (node) => {
        if (node.init && node.init.type ===
            "ObjectExpression") {
            const kindProp = node.init.properties.find(prop => prop.key?.name ===
                "kind" && prop.value?.value === "Document");
            const definitionsProp =
                node.init.properties.find(prop => prop.key?.name === "definitions" && prop.value?.type ===
                    "ArrayExpression");
            if (kindProp && definitionsProp) {
                const extractedObject =
                    extractObjectFromAST(node.init);
                if (extractedObject) matchedObjects.push(extractedObject);
            }
        }
    },
    Literal: (node) => {
        if (node.value && typeof node.value === 'string' &&
            (node.value.indexOf('"kind"') > -1) && (node.value.indexOf('"Document"') > -1) &&
            (node.value.indexOf('"definitions"') > -1)) {
            matchedObjects.push(node.value);
        }
    }
})