({
    TemplateLiteral: (node) => {
        if (node.quasis &&
            node.quasis.length > 0) {
            for (let i = 0; i < node.quasis.length; i++) {
                const quasisVal = node.quasis[i].value;
                if ((typeof quasisVal.raw === 'string' &&
                        quasisVal.raw.indexOf('{') > -1) && (
                        quasisVal.raw.indexOf('}') > -1) &&
                    (quasisVal.raw.indexOf('query ') > -1 ||
                        quasisVal.raw.indexOf('mutation ') > -
                        1 ||
                        quasisVal.raw.indexOf('fragment ') > -
                        1 || quasisVal.raw.indexOf(
                            'subscription ') > -1)) {
                    const rawQuasisVal = quasisVal.raw.trim()
                        .replaceAll('\n', '').replaceAll(/\$\{[^}]+\}/g, '');
                    matchedObjects.push(rawQuasisVal);
                }
            }
        }
    },
    Literal: (node) => {
        if (node.value && typeof node.value === 'string' && (
                node.value.indexOf('{') > -1) && (node.value
                .indexOf('}') > -1) && (node.value.indexOf(
                'query ') > -1 || node.value.indexOf(
                'mutation ') > -1 || node.value.indexOf(
                'fragment ') > -1 || node.value.indexOf(
                'subscription ') > -1)) {
            matchedObjects.push(node.value.trim()
                .replaceAll('\n', ''));
        }
    }
})