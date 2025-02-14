const matchedObjects = [];
extractObjectFromAST = (node) => {
  if (node.type !== "ObjectExpression") return null;

  const obj = {};
  node.properties.forEach(prop => {
    if (!prop.key || !prop.value) return;

    const key = prop.key.name || prop.key.value;
    let value;

    switch (prop.value.type) {
      case "Literal":
        value = prop.value.value;
        break;
      case "ArrayExpression":
        value = prop.value.elements.map(el => extractObjectFromAST(el) || (el.value ?? null));
        break;
      case "ObjectExpression":
        value = extractObjectFromAST(prop.value);
        break;
    }

    obj[key] = value;
  });

  return obj;
}

parseJavascript = (ast, visitors) => {
  try {
    acorn.walk.simple(ast, visitors);
    return JSON.stringify(matchedObjects);
  } catch (error) {
    console.error(error);
    return JSON.stringify([]);
  }
}