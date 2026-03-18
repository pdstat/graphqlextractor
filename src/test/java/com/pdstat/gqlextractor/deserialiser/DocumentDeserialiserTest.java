package com.pdstat.gqlextractor.deserialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import graphql.language.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class DocumentDeserialiserTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Document.class, new DocumentDeserialiser());
        mapper.registerModule(module);
    }

    @Test
    void testDeserializeSimpleQuery() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"}," +
                "\"arguments\":[]}]}}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        Assertions.assertNotNull(doc);
        Assertions.assertEquals(1, doc.getDefinitions().size());
        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Assertions.assertEquals("GetUser", opDef.getName());
        Assertions.assertEquals(OperationDefinition.Operation.QUERY, opDef.getOperation());
    }

    @Test
    void testDeserializeMutation() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"mutation\",\"name\":{\"kind\":\"Name\",\"value\":\"CreateUser\"}," +
                "\"variableDefinitions\":[{\"kind\":\"VariableDefinition\",\"variable\":{\"kind\":\"Variable\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"input\"}},\"type\":{\"kind\":\"NonNullType\"," +
                "\"type\":{\"kind\":\"NamedType\",\"name\":{\"kind\":\"Name\",\"value\":\"CreateUserInput\"}}}}]," +
                "\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[{\"kind\":\"Field\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"createUser\"},\"arguments\":[{\"kind\":\"Argument\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"input\"},\"value\":{\"kind\":\"Variable\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"input\"}}}],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"}," +
                "\"arguments\":[]}]}}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Assertions.assertEquals("CreateUser", opDef.getName());
        Assertions.assertEquals(OperationDefinition.Operation.MUTATION, opDef.getOperation());
        Assertions.assertEquals(1, opDef.getVariableDefinitions().size());
        Assertions.assertEquals("input", opDef.getVariableDefinitions().get(0).getName());
    }

    @Test
    void testDeserializeWithFragmentDefinition() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"FragmentDefinition\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"UserFields\"}," +
                "\"typeCondition\":{\"kind\":\"NamedType\",\"name\":{\"kind\":\"Name\",\"value\":\"User\"}}," +
                "\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[" +
                "{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"},\"arguments\":[]}," +
                "{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"name\"},\"arguments\":[]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        Assertions.assertEquals(1, doc.getDefinitions().size());
        FragmentDefinition fragDef = (FragmentDefinition) doc.getDefinitions().get(0);
        Assertions.assertEquals("UserFields", fragDef.getName());
        Assertions.assertEquals("User", fragDef.getTypeCondition().getName());
    }

    @Test
    void testDeserializeWithFragmentSpread() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"FragmentSpread\",\"name\":{\"kind\":\"Name\"," +
                "\"value\":\"UserFields\"}}]}}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        SelectionSet userSelections = ((Field) opDef.getSelectionSet().getSelections().get(0)).getSelectionSet();
        FragmentSpread spread = (FragmentSpread) userSelections.getSelections().get(0);
        Assertions.assertEquals("UserFields", spread.getName());
    }

    @Test
    void testDeserializeWithInlineFragment() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetNode\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"node\"}," +
                "\"arguments\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"InlineFragment\"," +
                "\"typeCondition\":{\"kind\":\"NamedType\",\"name\":{\"kind\":\"Name\",\"value\":\"User\"}}," +
                "\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[" +
                "{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"name\"},\"arguments\":[]}]}}]}}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        SelectionSet nodeSelections = ((Field) opDef.getSelectionSet().getSelections().get(0)).getSelectionSet();
        InlineFragment inlineFragment = (InlineFragment) nodeSelections.getSelections().get(0);
        Assertions.assertEquals("User", inlineFragment.getTypeCondition().getName());
    }

    @Test
    void testDeserializeWithStringValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"}," +
                "\"value\":{\"kind\":\"StringValue\",\"value\":\"123\"}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field userField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = userField.getArguments().get(0);
        Assertions.assertEquals("id", arg.getName());
        Assertions.assertInstanceOf(StringValue.class, arg.getValue());
        Assertions.assertEquals("123", ((StringValue) arg.getValue()).getValue());
    }

    @Test
    void testDeserializeWithIntValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"users\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"limit\"}," +
                "\"value\":{\"kind\":\"IntValue\",\"value\":\"10\"}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field usersField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = usersField.getArguments().get(0);
        Assertions.assertInstanceOf(IntValue.class, arg.getValue());
    }

    @Test
    void testDeserializeWithBooleanValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"users\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"active\"}," +
                "\"value\":{\"kind\":\"BooleanValue\",\"value\":true}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field usersField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = usersField.getArguments().get(0);
        Assertions.assertInstanceOf(BooleanValue.class, arg.getValue());
        Assertions.assertTrue(((BooleanValue) arg.getValue()).isValue());
    }

    @Test
    void testDeserializeWithEnumValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"users\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"status\"}," +
                "\"value\":{\"kind\":\"EnumValue\",\"value\":\"ACTIVE\"}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field usersField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = usersField.getArguments().get(0);
        Assertions.assertInstanceOf(EnumValue.class, arg.getValue());
        Assertions.assertEquals("ACTIVE", ((EnumValue) arg.getValue()).getName());
    }

    @Test
    void testDeserializeWithNullValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"users\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"filter\"}," +
                "\"value\":{\"kind\":\"NullValue\"}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field usersField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = usersField.getArguments().get(0);
        Assertions.assertInstanceOf(NullValue.class, arg.getValue());
    }

    @Test
    void testDeserializeWithObjectValueArgument() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"users\"}," +
                "\"arguments\":[{\"kind\":\"Argument\",\"name\":{\"kind\":\"Name\",\"value\":\"filter\"}," +
                "\"value\":{\"kind\":\"ObjectValue\",\"fields\":[{\"name\":{\"kind\":\"Name\"," +
                "\"value\":\"active\"},\"value\":{\"kind\":\"BooleanValue\",\"value\":true}}]}}]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        Field usersField = (Field) opDef.getSelectionSet().getSelections().get(0);
        Argument arg = usersField.getArguments().get(0);
        Assertions.assertInstanceOf(ObjectValue.class, arg.getValue());
        ObjectValue objVal = (ObjectValue) arg.getValue();
        Assertions.assertEquals(1, objVal.getObjectFields().size());
        Assertions.assertEquals("active", objVal.getObjectFields().get(0).getName());
    }

    @Test
    void testDeserializeWithListType() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[{\"kind\":\"OperationDefinition\"," +
                "\"operation\":\"query\",\"name\":{\"kind\":\"Name\",\"value\":\"GetUsers\"}," +
                "\"variableDefinitions\":[{\"kind\":\"VariableDefinition\",\"variable\":{\"kind\":\"Variable\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"ids\"}},\"type\":{\"kind\":\"ListType\"," +
                "\"type\":{\"kind\":\"NonNullType\",\"type\":{\"kind\":\"NamedType\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"ID\"}}}}}]," +
                "\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[{\"kind\":\"Field\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"users\"},\"arguments\":[]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        OperationDefinition opDef = (OperationDefinition) doc.getDefinitions().get(0);
        VariableDefinition varDef = opDef.getVariableDefinitions().get(0);
        Assertions.assertEquals("ids", varDef.getName());
        Assertions.assertInstanceOf(ListType.class, varDef.getType());
    }

    @Test
    void testDeserializeSkipsDefinitionsWithoutName() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[" +
                "{\"kind\":\"OperationDefinition\",\"operation\":\"query\"," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        // Definition without a name is skipped by namedNode check
        Assertions.assertNotNull(doc);
        Assertions.assertTrue(doc.getDefinitions().isEmpty());
    }

    @Test
    void testDeserializeMultipleDefinitions() throws IOException {
        String json = "{\"kind\":\"Document\",\"definitions\":[" +
                "{\"kind\":\"OperationDefinition\",\"operation\":\"query\"," +
                "\"name\":{\"kind\":\"Name\",\"value\":\"GetUser\"}," +
                "\"variableDefinitions\":[],\"selectionSet\":{\"kind\":\"SelectionSet\"," +
                "\"selections\":[{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"user\"}," +
                "\"arguments\":[]}]}}," +
                "{\"kind\":\"FragmentDefinition\",\"name\":{\"kind\":\"Name\",\"value\":\"UserFields\"}," +
                "\"typeCondition\":{\"kind\":\"NamedType\",\"name\":{\"kind\":\"Name\",\"value\":\"User\"}}," +
                "\"selectionSet\":{\"kind\":\"SelectionSet\",\"selections\":[" +
                "{\"kind\":\"Field\",\"name\":{\"kind\":\"Name\",\"value\":\"id\"},\"arguments\":[]}]}}]}";

        Document doc = mapper.readValue(json, Document.class);

        Assertions.assertEquals(2, doc.getDefinitions().size());
        Assertions.assertInstanceOf(OperationDefinition.class, doc.getDefinitions().get(0));
        Assertions.assertInstanceOf(FragmentDefinition.class, doc.getDefinitions().get(1));
    }
}
