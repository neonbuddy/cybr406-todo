package com.cybr406.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Homework 3 - Migration Tests
 *
 * This homework assignment will focus on creating a Liquibase changelog using db/changelog/db.changelog-master.xml
 *
 * Resources:
 * https://ryl.github.io/cybr406-2020/files/database-migrations.pptx
 * https://github.com/ryl/cybr406-books-demo/blob/master/src/main/resources/db/changelog/db.changelog-master.xml
 *
 * https://www.liquibase.org/documentation/index.html
 * https://www.liquibase.org/documentation/changeset.html
 * https://www.liquibase.org/documentation/changes/create_table.html
 * https://www.liquibase.org/documentation/column.html
 * https://www.liquibase.org/documentation/changes/create_index.html
 * https://www.liquibase.org/documentation/changes/create_index.html
 * https://www.liquibase.org/documentation/changes/add_foreign_key_constraint.html
 */
@SpringBootTest
@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
@ActiveProfiles("test")
public class Homework3MigrationTests {

    @Autowired
    Environment environment;

    /**
     * Problem 1: Use XML format
     *
     * Spring uses a YML changeLog format by default, but we can choose between YML, XML, and JSON. Let's use XML for
     * this class.
     *
     * In application.properties add the following:
     *
     *     spring.liquibase.change-log = classpath:/db/changelog/db.changelog-master.xml
     *
     * This will allow us to use XML format.
     */
    @Test
    public void problem01_useXMLChangeLogFormat() {
        assertEquals("classpath:/db/changelog/db.changelog-master.xml",
                environment.getProperty("spring.liquibase.change-log"),
                "You must change the location of spring.liquibase.change-log to " +
                        "classpath:/db/changelog/db.changelog-master.xml " +
                        "in application.properties");
    }

    /**
     * Problem 2: Create a change set for the todo table
     *
     * A change set is a collection of changes to be made in the database. For this problem, create a change set for
     * creating the todo table. For this problem to pass, you can leave the change set empty. The upcoming problems
     * will add content.
     *
     * Add a change set to db/changelog/db.changelog-master.xml
     * Attributes:
     *     id: todo-table
     *     author: your name (any non-null value will work)
     *
     * Read about change sets:
     * https://www.liquibase.org/documentation/changeset.html
     */
    @Test
    public void problem02_createChangeSetForAuthorTable() throws Exception {
        Element changeSet = findElementWithAttributeValue(
                "changeSet",
                "id",
                "todo-table");
        assertAttributeIsNotNull(changeSet, "author");
    }

    /**
     * Problem 3: Create the todo table
     * 
     * Now that you've created a change set, let's add a createTable command. You can leave the createTable command
     * empty for now, we will add columns in the next problem.
     * 
     * Read about the createTable command:
     * https://www.liquibase.org/documentation/changes/create_table.html
     */
    @Test
    public void problem03_createAuthorTable() throws Exception {
        Element changeSet = findElementWithAttributeValue(
                "changeSet",
                "id",
                "todo-table");

        findElementWithAttributeValue(
                changeSet,
                "createTable",
                "tableName",
                "TODO");
    }

    /**
     * Problem 4: Add columns to the todo table
     * 
     * Now let's add some columns to the todo table.
     * 
     * Read about columns:
     * https://www.liquibase.org/documentation/column.html
     * 
     * NOTE: The above article also covers the constraints tag, which will be used in this problem.
     */
    @Test
    public void problem04_createAuthorTableColumns() throws Exception {
        Element changeSet = findElementWithAttributeValue(
                "changeSet",
                "id",
                "todo-table");

        Element createTable = findElementWithAttributeValue(
                changeSet,
                "createTable",
                "tableName",
                "TODO");

        // Verify the "ID" column

        Element idColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "ID");
        assertAttributeEquals(
                idColumn,
                "autoIncrement",
                "true");
        assertAttributeEquals(
                idColumn,
                "type",
                "BIGINT");

        // Verify primary key constraints on ID column

        Element constraint = findElementWithAttributeValue(
                idColumn,
                "constraints",
                "primaryKeyName",
                "PRIMARY_KEY_TODO");
        assertAttributeEquals(
                constraint,
                "nullable",
                "false");
        assertAttributeEquals(
                constraint,
                "primaryKey",
                "true");

        // Verify the "AUTHOR" column

        Element authorColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "AUTHOR");
        assertAttributeEquals(
                authorColumn,
                "type",
                "VARCHAR(255)");

        // Verify the "DETAILS" column

        Element detailsColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "DETAILS");
        assertAttributeEquals(
                detailsColumn,
                "type",
                "CLOB");
    }

    /**
     * Problem 5 - Create a complete changeSet for the task table.
     * 
     * Now that you've seen how changeSet, createTable, and column work together, do the same procedure for the task
     * table.
     */
    @Test
    public void problem05_createTaskTableChangeSet() throws Exception {
        Element changeSet = findElementWithAttributeValue(
                "changeSet",
                "id",
                "task-table");

        Element createTable = findElementWithAttributeValue(
                changeSet,
                "createTable",
                "tableName",
                "TASK");

        // Verify the "ID" column

        Element idColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "ID");
        assertAttributeEquals(
                idColumn,
                "autoIncrement",
                "true");
        assertAttributeEquals(
                idColumn,
                "type",
                "BIGINT");

        // Verify primary key constraints on ID column

        Element constraint = findElementWithAttributeValue(
                idColumn,
                "constraints",
                "primaryKeyName",
                "PRIMARY_KEY_TASK");
        assertAttributeEquals(
                constraint,
                "nullable",
                "false");
        assertAttributeEquals(
                constraint,
                "primaryKey",
                "true");

        // Verify the "COMPLETED" column

        Element authorColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "COMPLETED");
        assertAttributeEquals(
                authorColumn,
                "type",
                "BOOLEAN");

        // Verify the "DETAILS" column

        Element detailsColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "DETAILS");
        assertAttributeEquals(
                detailsColumn,
                "type",
                "CLOB");

        // Verify the "TODO_ID" column

        Element todoIdColumn = findElementWithAttributeValue(
                createTable,
                "column",
                "name",
                "TODO_ID");
        assertAttributeEquals(
                detailsColumn,
                "type",
                "CLOB");
    }

    
    /**
     * Problem 6: Add an index to the task column
     * 
     * Indexes allow your database to find records quickly. Since we often plan on finding all the tasks belonging to
     * a todo, lets add an index to the TODO_ID column on the TASK table.
     * 
     * Read about creating indexes:
     * https://www.liquibase.org/documentation/changes/create_index.html
     */
    @Test
    public void problem06_createIndex() throws Exception {
        Element changeSet = findElementWithAttributeValue(
            "changeSet",
            "id",
            "todo-id-index");
        assertAttributeIsNotNull(changeSet, "author");
        
        Element createIndex = findElementWithAttributeValue(
            "createIndex",
            "indexName",
            "TODO_ID_INDEX");
        assertAttributeEquals(
            createIndex,
            "tableName",
            "TASK");
        
        findElementWithAttributeValue(
            createIndex,
            "column",
            "name",
            "TODO_ID");
    }

    /**
     * Problem 7: Add a foreign key to the task table
     * 
     * Foreign keys allow us to describe the relationships between tables. They can allow useful behavior, such as
     * automatically deleting children when a parent is deleted in an @OneToMany relationship.
     * 
     * Read about adding foreign key constraints:
     * https://www.liquibase.org/documentation/changes/add_foreign_key_constraint.html
     */
    @Test
    public void problem07_addForeignKey() throws Exception {
        Element changeSet = findElementWithAttributeValue(
            "changeSet",
            "id",
            "todo-id-foreign-key");
        assertAttributeIsNotNull(changeSet, "author");

        Element foreignKey = findElementWithAttributeValue(
            changeSet,
            "addForeignKeyConstraint",
            "constraintName",
            "TODO_ID_FOREIGN_KEY");
        
        // Base attributes refer to the table we wish to place a foreign key constraint on
        assertAttributeEquals(foreignKey, "baseColumnNames", "TODO_ID");
        assertAttributeEquals(foreignKey, "baseTableName", "TASK");
        
        // Reference attributes describe the table & column that are pointed to
        assertAttributeEquals(foreignKey, "referencedColumnNames", "ID");
        assertAttributeEquals(foreignKey, "referencedTableName", "TODO");

        // CASCADE will cause changes in the Todo table to cascade to the Task table.
        // For example, if you you delete a Todo, all of its Tasks will automatically be deleted.
        assertAttributeEquals(foreignKey, "onDelete", "CASCADE");
        assertAttributeEquals(foreignKey, "onUpdate", "CASCADE");
    }

    private Element findElementWithAttributeValue(String elementType, String attributeName, String attributeValue) throws Exception {
        Document document = loadChangeLog();
        Element element = document.getDocumentElement();
        return findElementWithAttributeValue(element, elementType, attributeName, attributeValue);
    }

    private Element findElementWithAttributeValue(
            Element parentElement,
            String elementType,
            String attributeName,
            String attributeValue) throws Exception {

        Document document = loadChangeLog();
        XPath xpath = documentXpath(document);

        String parentXPath = "";
        if (!parentElement.getNodeName().equals("databaseChangeLog")) {
            parentXPath = "//db:" + parentElement.getTagName() + "[";
            List<String> attributes = new ArrayList<>();
            for (int i = 0; i < parentElement.getAttributes().getLength(); i++) {
                Attr attr =  (Attr)parentElement.getAttributes().item(i);
                attributes.add(String.format("@%s='%s'", attr.getName(), attr.getValue()));
            }
            parentXPath += attributes.stream().collect(Collectors.joining(" and "));
            parentXPath += "]";
        }

        String expression = String.format("%s//db:%s[@%s='%s']", parentXPath, elementType, attributeName, attributeValue);

        NodeList nodes = (NodeList) xpath.evaluate(
                expression,
                parentElement,
                XPathConstants.NODESET);

        assertEquals(1, nodes.getLength(), String.format(
                "Expected to find exactly 1 %s with attribute %s = \"%s\", but actually found %d",
                elementType, attributeName, attributeValue, nodes.getLength()));
        return (Element) nodes.item(0);
    }

    private void assertAttributeIsNotNull(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        assertTrue(String.format(
                    "The %s attribute on the %s element should have a non-null, non-blank value.",
                    attributeName, element.getNodeName()),
                StringUtils.hasText(value));
    }

    private void assertAttributeEquals(Element element, String attributeName, String expectedAttributeValue) {
        String value = element.getAttribute(attributeName);
        assertEquals(
                expectedAttributeValue,
                value,
                String.format("Expected attribute %s = \"%s\" on %s",
                    attributeName, expectedAttributeValue, element.getNodeName()));
    }

    private Document loadChangeLog() throws IOException, ParserConfigurationException, SAXException {
        String location = environment.getProperty("spring.liquibase.change-log");
        assertNotNull("Change log location must be set.", location);
        assertEquals("classpath:/db/changelog/db.changelog-master.xml", location);

        ClassPathResource resource = new ClassPathResource("db/changelog/db.changelog-master.xml");
        assertTrue("Add db.changelog-master.xml to your project.", resource.exists());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(resource.getInputStream());
    }

    private XPath documentXpath(Document document) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if (prefix == null) throw new NullPointerException("Null prefix");
                else if ("db".equals(prefix)) return "http://www.liquibase.org/xml/ns/dbchangelog";
                else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
                return XMLConstants.NULL_NS_URI;
            }

            // This method isn't necessary for XPath processing.
            public String getPrefix(String uri) {
                throw new UnsupportedOperationException();
            }

            // This method isn't necessary for XPath processing either.
            public Iterator getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }
        });
        return xpath;
    }

}
