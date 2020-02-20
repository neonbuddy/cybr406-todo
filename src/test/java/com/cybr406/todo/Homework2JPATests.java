package com.cybr406.todo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Homework2JPATests {

    /**
     * Part 1 Setup: Annotate the classes you plan to persist into the database.
     */
    @Nested
    class Part_1_Setup {

        /**
         * Problem 1: Add JPA Annotations to classes
         *
         * @Entity tells JPA which classes you wish to store into a database. Add it to the Todo and Task classes.
         *
         * This is a good first step to refactoring the project to use a database. JPA will automatically generate database
         * tables for classes marked with @Entity.
         *
         * You can check your progress by running TodoApplication and visiting http://localhost:8080/h2-console
         */
        @Test
        public void problem_01_addEntityAnnotations() {
            verifyClassAnnotation(
                    "com.cybr406.todo.Todo",
                    "javax.persistence.Entity",
                    "You need to add the @Entity annotation to the Todo class.");
            verifyClassAnnotation(
                    "com.cybr406.todo.Task",
                    "javax.persistence.Entity",
                    "You need to add the @Entity annotation to the Task class.");
        }

        /**
         * Problem 2: Add ID annotations
         *
         * When your store information into a database its important to indicate which fields uniquely identify a record.
         * An easy way to accomplish this is with a number that automatically grows as records are added. In database terms
         * this is often called an "identity" or "auto increment" column that acts as the primary key of the table.
         *
         * In Java terms, this is accomplished with the @Id and @GeneratedValue annotations.
         *
         * @Id: Tell JPA which field uniquely identifies object in the database.
         * @GeneratedValue: Tell JPA how you would like the database to generate a value for this field.
         *      strategy: Use GenerationType.IDENTITY for an automatically incrementing number.
         */
        @Test
        public void problem_02_addIdAnnotations() {
            verifyFieldAnnotation(
                    "id",
                    Long.class,
                    "com.cybr406.todo.Todo",
                    javax.persistence.Id.class,
                    "Add the @Id annotation to the 'id' field on the Todo object.");
            verifyFieldAnnotation(
                    "id",
                    Long.class,
                    "com.cybr406.todo.Task",
                    javax.persistence.Id.class,
                    "Add the @Id annotation to the 'id' field on the Task object.");

            GeneratedValue generatedValue = verifyFieldAnnotation(
                    "id",
                    Long.class,
                    "com.cybr406.todo.Todo",
                    javax.persistence.GeneratedValue.class,
                    "Add the @GeneratedValue annotation the 'id' field on the Todo object.");
            assertEquals(GenerationType.IDENTITY, generatedValue.strategy(),
                    "Use the GenerationType.IDENTITY strategy to automatically generate unique ids for Todos");

            generatedValue = verifyFieldAnnotation(
                    "id",
                    Long.class,
                    "com.cybr406.todo.Task",
                    javax.persistence.GeneratedValue.class,
                    "Add the @GeneratedValue annotation the 'id' field on the Task object.");
            assertEquals(GenerationType.IDENTITY, generatedValue.strategy(),
                    "Use the GenerationType.IDENTITY strategy to automatically generate unique ids for Tasks");
        }

        /**
         * Problem 3: Add Lob Annotations
         *
         * When JPA detects a String field on one of your Java objects, it usually creates a VARCHAR(255) column. This
         * column type can only hold 255 characters, which may not be enough storage for long messages.
         *
         * Adding the @Lob to the String field will store it as a "large object" that can handle large strings of text.
         *
         * Make the Todo.details and Task.details fields large objects using the @Lob annotation.
         */
        @Test
        public void problem_03_addLobAnnotations() {
            verifyFieldAnnotation(
                    "details",
                    String.class,
                    "com.cybr406.todo.Todo",
                    javax.persistence.Lob.class,
                    "Add the @Lob annotation to the 'details' field on the Todo object.");
            verifyFieldAnnotation(
                    "details",
                    String.class,
                    "com.cybr406.todo.Task",
                    javax.persistence.Lob.class,
                    "Add the @Lob annotation to the 'details' field on the Task object.");
        }

        /**
         * Problem 4: Describe Relationships
         *
         * JPA needs to know about the relationships between classes that work together. In the case of Todo and Task,
         * Todo plays the role of parent, and Task the role of child. Said another way, a one to many relationship
         * exists between Todo and Task.
         *
         * Add @OneToMany to Todo.tasks
         *      * On the parent side of the relationship, you should set the mappedBy parameter. The value should be the
         *        name of field on the child that references the parent.
         *
         * Add @ManyToOne to Task.todo
         */
        @Test
        public void problem_04_addRelationshipAnnotations() {
            OneToMany oneToMany = verifyFieldAnnotation(
                    "tasks",
                    List.class,
                    "com.cybr406.todo.Todo",
                    javax.persistence.OneToMany.class,
                    "Add the @OneToMany annotation to the 'tasks' field on the Todo object.");
            assertEquals("todo", oneToMany.mappedBy(), "Set the mappedBy parameter of @OneToMany to 'todo'.");

            verifyFieldAnnotation(
                    "todo",
                    Todo.class,
                    "com.cybr406.todo.Task",
                    javax.persistence.ManyToOne.class,
                    "Add the @ManyToOne annotation to the 'todo' field on the Task object.");
        }

        /**
         * Problem 5: Create repositories
         *
         * A Repository is an interface with methods that provided access to the database. They come with simple
         * CRUD (Create, Retrieve, Update, Delete) operations out of the box. More complex functions can be added as
         * methods provided you use the right naming convention.
         *
         * Create 2 new interfaces com.cybr406.todo.TodoJpaRepository and com.cybr406.todo.TaskJpaRepository
         *
         * Remember, these must be Java interfaces, not classes. Interfaces describe the requirements of a class without
         * providing any method implementations.
         *
         * Each interface should extend JpaRepository<T, ID>, where T is the type of class being managed, and ID is
         * Java type of the class's @Id annotated field.
         *
         * Finally, annotate your new classes with @Repository.
         */
        @Test
        public void problem_05_createRepositories() {
            verifyRepository(
                    "com.cybr406.todo.TodoJpaRepository",
                    JpaRepository.class);
            verifyRepository(
                    "com.cybr406.todo.TaskJpaRepository",
                    JpaRepository.class);
        }
    }

    /**
     * Part 2 Repositories
     */
    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
    class Part_2_Refactoring {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;
        
        /**
         * Problem 6: Switch to Page & Pageable
         * 
         * Refactor the "/todos" method to use Spring's Page and Pageable classes.
         * 
         *     * Refactor the method arguments from (int page, int size) to (Pageable page).
         *     * Refactor the return type to Page<Todo>
         */
        @Test
        public void problem_06_usePaginatedOnTodosMethod() throws Exception {
            Class<?> clazz = Class.forName("com.cybr406.todo.TodoRestController");
            Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> {
                    GetMapping annotation = m.getAnnotation(GetMapping.class);
                    if (Objects.isNull(annotation))
                        return false;
                    return Arrays.asList(annotation.value()).contains("/todos");
                })
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Could not a method with @GetMapping(\"/todos\")!"));

            assertEquals(1, method.getParameterCount(), "The /todos method should only take 1 argument, and it should be a Pageable.");
            assertEquals(Pageable.class, method.getParameterTypes()[0], "The first method argument should be a Pageable.");
            ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();
            assertEquals("org.springframework.data.domain.Page<com.cybr406.todo.Todo>", returnType.getTypeName(), "The /todos endpoint should return a Page<Todo>");
        }

        /**
         * Problem 7: Refactor GET /todos
         *     * Update the method to use TodoJpaRepository instead of InMemoryTodoRepository
         */
        @Test
        public void problem_07_testFindAll() throws Exception {
            generateRandomizedTodos(25);

            // When no params are present, reasonable defaults should be applied.

            String response = mockMvc.perform(get("/todos"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            List<Todo> todos = new ArrayList<>();
            ObjectNode objectNode = objectMapper.readValue(response, ObjectNode.class);
            for (JsonNode jsonNode : objectNode.get("content")) {
                todos.add(objectMapper.readValue(jsonNode.toString(), Todo.class));
            }

            assertEquals(20, todos.size());
            assertEquals(1L, todos.get(0).getId());

            // Size should be adjustable

            response = mockMvc.perform(get("/todos?size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            todos = new ArrayList<>();
            objectNode = objectMapper.readValue(response, ObjectNode.class);
            for (JsonNode jsonNode : objectNode.get("content")) {
                todos.add(objectMapper.readValue(jsonNode.toString(), Todo.class));
            }
            assertEquals(5, todos.size());
            assertEquals(1L, todos.get(0).getId());

            // Page should be adjustable

            response = mockMvc.perform(get("/todos?page=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            todos = new ArrayList<>();
            objectNode = objectMapper.readValue(response, ObjectNode.class);
            for (JsonNode jsonNode : objectNode.get("content")) {
                todos.add(objectMapper.readValue(jsonNode.toString(), Todo.class));
            }
            assertEquals(5, todos.size()); // Second page with size 10 should have 5 items.
            assertEquals(21L, todos.get(0).getId());
        }
        
        /**
         * Problem 8: Reactor POST /todos
         */
        @Test
        public void problem_08_testCreateTodoMethod() throws Exception {
            Todo todo = new Todo();
            todo.setAuthor("Mr. Test");
            todo.setDetails("Remember to test.");

            for (long i = 1; i <= 3; i++) {
                String response = mockMvc.perform(post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(todo)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

                Todo result = objectMapper.readValue(response, Todo.class);

                // Verify id increments upwards as new todo's are sent in.
                assertEquals(i, result.getId());
            }
        }

        /**
         * Problem 9: Refactor GET /todos/{id}
         */
        @Test
        public void problem_09_testFindTodo() throws Exception {
            Todo todo = new Todo();
            todo.setAuthor("testFindTodo");
            todo.setDetails("testFindTodo");

            mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
                .andDo(print())
                .andExpect(status().isCreated());

            mockMvc.perform(get("/todos/1"))
                .andDo(print())
                .andExpect(status().isOk());

            mockMvc.perform(get("/todos/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
        }

        /**
         * Problem 10: Refactor POST /todos/{id}/tasks
         */
        @Test
        public void problem_10_testAddTask() throws Exception {
            generateRandomizedTodos(1);

            Map<String, Object> postData = new HashMap<>();
            postData.put("completed", false);
            postData.put("details", "Task 1");

            String response = mockMvc.perform(post("/todos/1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

            Todo todo = objectMapper.readValue(response, Todo.class);
            assertEquals(1, todo.getTasks().size());

            postData = new HashMap<>();
            postData.put("completed", false);
            postData.put("details", "Task 2");

            response = mockMvc.perform(post("/todos/1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postData)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

            todo = objectMapper.readValue(response, Todo.class);
            assertEquals(2, todo.getTasks().size());

            Task first = todo.getTasks().get(0);
            Task second = todo.getTasks().get(1);

            assertEquals("Task 1", first.getDetails());
            assertEquals("Task 2", second.getDetails());
        }

        /**
         * Problem 11: Refactor DELETE /todos/{id}
         */
        @Test
        public void problem_11_deleteTodo() throws Exception {
            generateRandomizedTodos(1);

            String response = mockMvc.perform(delete("/todos/1"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse()
                .getContentAsString();

            assertEquals("", response);

            mockMvc.perform(delete("/todos/1"))
                .andDo(print())
                .andExpect(status().isNotFound());
        }

        /**
         * Problem 12: DELETE /tasks/{id}
         */
        @Test
        public void problem_12_deleteTask() throws Exception {
            problem_10_testAddTask();

            String response = mockMvc.perform(get("/todos/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            Todo todo = objectMapper.readValue(response, Todo.class);
            assertEquals(2, todo.getTasks().size());

            mockMvc.perform(delete("/tasks/1"))
                .andDo(print())
                .andExpect(status().isNoContent());

            response = mockMvc.perform(get("/todos/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

            todo = objectMapper.readValue(response, Todo.class);
            assertEquals(1, todo.getTasks().size());

            mockMvc.perform(delete("/tasks/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

            mockMvc.perform(delete("/tasks/2"))
                .andDo(print())
                .andExpect(status().isNoContent());

            mockMvc.perform(delete("/tasks/2"))
                .andDo(print())
                .andExpect(status().isNotFound());
        }

        private void generateRandomizedTodos(int total) throws Exception {
            for (int i = 0; i < total; i++) {
                Todo todo = new Todo();
                todo.setAuthor("Test Author " + UUID.randomUUID());
                todo.setDetails("Test details " + UUID.randomUUID());
                mockMvc.perform(post("/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(todo)))
                    .andDo(print())
                    .andExpect(status().isCreated());
            }
        }
    }

    private static Class<?> verifyClassExists(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("You need to create a class called " + className, e);
        }
    }

    private static void verifyClassAnnotation(String className, String annotationName, String message) {
        Class<?> clazz = verifyClassExists(className);
        List<Annotation> annotations = Arrays.asList(clazz.getAnnotations());

        Annotation annotation = annotations.stream()
                .filter(a -> a.annotationType().getName().equals(annotationName))
                .findFirst()
                .orElse(null);

        assertNotNull(annotation, message);
    }

    private static <T> void verifyRepository(String className, Class<T> expectedParentClass) {
        Class<?> clazz = verifyClassExists(className);
        assertTrue(clazz.isInterface(), clazz.getSimpleName() + " must be an interface, not a class.");
        assertTrue(expectedParentClass.isAssignableFrom(clazz), String.format(
                "%s should extend %s",
                clazz.getSimpleName(),
                expectedParentClass.getSimpleName()));

        Repository annotation = clazz.getDeclaredAnnotation(Repository.class);
        assertNotNull(annotation, clazz.getSimpleName() + " should be annotated with @Repository");
    }

    private static <T extends Annotation> T verifyFieldAnnotation(
            String fieldName,
            Class<?> expectedFieldType,
            String className,
            Class<T> annotationClass,
            String message) {

        Class<?> clazz = verifyClassExists(className);

        try {
            Field field = clazz.getDeclaredField(fieldName);

            assertEquals(field.getType(), expectedFieldType, String.format(
                    "Field %s should be a %s", fieldName, expectedFieldType.getCanonicalName()));

            T annotation = field.getAnnotation(annotationClass);
            assertNotNull(annotation, message);

            return annotation;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(String.format("You need to add a field '%s %s' to %s",
                    expectedFieldType.getSimpleName(),
                    fieldName,
                    clazz.getSimpleName()),
                    e);
        }
    }

}
