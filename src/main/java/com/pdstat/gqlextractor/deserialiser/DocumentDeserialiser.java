package com.pdstat.gqlextractor.deserialiser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentDeserialiser extends JsonDeserializer<Document> {

    @Override
    public Document deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        Map<String, Object> node = objectMapper.readValue(jsonParser, new TypeReference<HashMap<String, Object>>() {
        });

        List<Definition> definitions = new ArrayList<>();
        List<Map<String, Object>> definitionsJson = (List<Map<String, Object>>) node.get("definitions");

        for (Map<String, Object> def : definitionsJson) {
            if (namedNode(def)) {
                String kind = (String) def.get("kind");

                if ("OperationDefinition".equals(kind)) {
                    definitions.add(parseOperationDefinition(def));
                } else if ("FragmentDefinition".equals(kind)) {
                    definitions.add(parseFragmentDefinition(def));
                }
            }
        }

        return Document.newDocument()
                .definitions(definitions)
                .build();
    }

    private boolean namedNode(Map<String, Object> node) {
        if (node == null) {
            return false;
        }
        return node.containsKey("name") && node.get("name") != null;
    }

    private OperationDefinition parseOperationDefinition(Map<String, Object> json) {
        OperationDefinition.Operation operation = OperationDefinition.Operation.valueOf(((String) json.get("operation")).toUpperCase());
        String name = json.containsKey("name") ? (String) ((Map<String, Object>) json.get("name")).get("value") : null;

        List<VariableDefinition> variableDefinitions = new ArrayList<>();
        List<Map<String, Object>> varDefsJson = (List<Map<String, Object>>) json.get("variableDefinitions");
        if (varDefsJson != null) {
            for (Map<String, Object> varDef : varDefsJson) {
                variableDefinitions.add(parseVariableDefinition(varDef));
            }
        }

        SelectionSet selectionSet = parseSelectionSet((Map<String, Object>) json.get("selectionSet"));

        return OperationDefinition.newOperationDefinition()
                .name(name)
                .operation(operation)
                .variableDefinitions(variableDefinitions)
                .selectionSet(selectionSet)
                .build();
    }

    private FragmentDefinition parseFragmentDefinition(Map<String, Object> json) {
        String name = (String) ((Map<String, Object>) json.get("name")).get("value");
        TypeName typeCondition = TypeName.newTypeName(
                (String) ((Map<String, Object>) ((Map<String, Object>) json.get("typeCondition")).get("name"))
                        .get("value")).build();
        SelectionSet selectionSet = parseSelectionSet((Map<String, Object>) json.get("selectionSet"));

        return FragmentDefinition.newFragmentDefinition()
                .name(name)
                .typeCondition(typeCondition)
                .selectionSet(selectionSet)
                .build();
    }

    private VariableDefinition parseVariableDefinition(Map<String, Object> json) {
        String name = (String) ((Map<String, Object>) ((Map<String, Object>) json.get("variable")).get("name"))
                .get("value");
        Type<?> type = parseType((Map<String, Object>) json.get("type"));
        return VariableDefinition.newVariableDefinition()
                .name(name)
                .type(type)
                .build();
    }

    private Type<?> parseType(Map<String, Object> json) {
        if ("NonNullType".equals(json.get("kind"))) {
            return NonNullType.newNonNullType(parseType((Map<String, Object>) json.get("type"))).build();
        } else if ("ListType".equals(json.get("kind"))) {
            return ListType.newListType(parseType((Map<String, Object>) json.get("type"))).build();
        } else {
            return TypeName.newTypeName((String) ((Map<String, Object>) json.get("name")).get("value")).build();
        }
    }

    private SelectionSet parseSelectionSet(Map<String, Object> json) {
        List<Selection> selections = new ArrayList<>();
        if (json != null) {
            List<Map<String, Object>> selectionsJson = (List<Map<String, Object>>) json.get("selections");
            for (Map<String, Object> selectionJson : selectionsJson) {
                String kind = (String) selectionJson.get("kind");

                if ("Field".equals(kind)) {
                    selections.add(parseField(selectionJson));
                } else if ("FragmentSpread".equals(kind)) {
                    selections.add(parseFragmentSpread(selectionJson));
                } else if ("InlineFragment".equals(kind)) {  // Handling InlineFragment
                    selections.add(parseInlineFragment(selectionJson));
                }
            }
        }
        return SelectionSet.newSelectionSet().selections(selections).build();
    }

    private Field parseField(Map<String, Object> json) {
        String name = (String) ((Map<String, Object>) json.get("name")).get("value");

        List<Argument> arguments = new ArrayList<>();
        List<Map<String, Object>> argsJson = (List<Map<String, Object>>) json.get("arguments");
        if (argsJson != null) {
            for (Map<String, Object> argJson : argsJson) {
                arguments.add(parseArgument(argJson));
            }
        }

        SelectionSet selectionSet = parseSelectionSet((Map<String, Object>) json.get("selectionSet"));

        return Field.newField()
                .name(name)
                .arguments(arguments)
                .selectionSet(selectionSet)
                .build();
    }

    private Argument parseArgument(Map<String, Object> json) {
        String name = (String) ((Map<String, Object>) json.get("name")).get("value");
        Value<?> value = parseValue((Map<String, Object>) json.get("value"));
        return Argument.newArgument().name(name).value(value).build();
    }

    private FragmentSpread parseFragmentSpread(Map<String, Object> json) {
        String name = (String) ((Map<String, Object>) json.get("name")).get("value");
        return FragmentSpread.newFragmentSpread().name(name).build();
    }

    private InlineFragment parseInlineFragment(Map<String, Object> json) {
        TypeName typeCondition = null;
        if (json.containsKey("typeCondition")) {
            Map<String, Object> namedType = (Map<String, Object>) json.get("typeCondition");
            typeCondition = TypeName.newTypeName((String) ((Map<String, Object>) namedType.get("name"))
                    .get("value")).build();
        }
        SelectionSet selectionSet = parseSelectionSet((Map<String, Object>) json.get("selectionSet"));

        return InlineFragment.newInlineFragment()
                .typeCondition(typeCondition)
                .selectionSet(selectionSet)
                .build();
    }

    private Value<?> parseValue(Map<String, Object> json) {
        String kind = (String) json.get("kind");

        if ("Variable".equals(kind)) {
            return VariableReference.newVariableReference()
                    .name((String) ((Map<String, Object>) json.get("name")).get("value"))
                    .build();
        } else if ("StringValue".equals(kind)) {
            return StringValue.newStringValue((String) json.get("value")).build();
        } else if ("IntValue".equals(kind)) {
            return IntValue.newIntValue(new java.math.BigInteger(json.get("value").toString())).build();
        } else if ("BooleanValue".equals(kind)) {
            return BooleanValue.newBooleanValue(json.get("value") != null && (Boolean) json.get("value")).build();
        } else if ("NullValue".equals(kind)) {
            return NullValue.newNullValue().build();
        } else if ("FloatValue".equals(kind)) {
            return FloatValue.newFloatValue(new BigDecimal((String) json.get("value"))).build();
        } else if ("EnumValue".equals(kind)) {
            return EnumValue.newEnumValue((String) json.get("value")).build();
        } else if ("ObjectValue".equals(kind)) {
            List<ObjectField> fields = new ArrayList<>();
            for (Map<String, Object> fieldJson : (List<Map<String, Object>>) json.get("fields")) {
                String fieldName = (String) ((Map<String, Object>) fieldJson.get("name")).get("value");
                Value<?> fieldValue = parseValue((Map<String, Object>) fieldJson.get("value"));
                fields.add(ObjectField.newObjectField().name(fieldName).value(fieldValue).build());
            }
            return ObjectValue.newObjectValue().objectFields(fields).build();
        }

        return NullValue.newNullValue().build();
    }

}
