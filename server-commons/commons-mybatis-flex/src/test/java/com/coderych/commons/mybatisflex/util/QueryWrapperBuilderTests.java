package com.coderych.commons.mybatisflex.util;

import com.coderych.commons.mybatisflex.enums.Operator;
import com.coderych.commons.mybatisflex.enums.Relation;
import com.coderych.commons.mybatisflex.model.Condition;
import com.mybatisflex.core.query.QueryWrapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class QueryWrapperBuilderTests {

    @Nested
    class BuildConditionTests {

        @Test
        void nullConditionShouldReturnEmptyWrapper() {
            QueryWrapper result = QueryWrapperBuilder.build((Condition) null);
            assertNotNull(result);
        }

        @Test
        void singleEqLeafCondition() {
            Condition condition = new Condition();
            condition.setKey("name");
            condition.setValue("test");
            condition.setOperator(Operator.EQ);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();
            System.out.println(sql);

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("="));
            assertTrue(sql.contains("test"));
        }

        @Test
        void neOperator() {
            Condition condition = new Condition();
            condition.setKey("status");
            condition.setValue(0);
            condition.setOperator(Operator.NE);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("status"));
            assertTrue(sql.contains("<>") || sql.contains("!="));
        }

        @Test
        void gtOperator() {
            Condition condition = new Condition();
            condition.setKey("age");
            condition.setValue(18);
            condition.setOperator(Operator.GT);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("age"));
            assertTrue(sql.contains(">"));
        }

        @Test
        void geOperator() {
            Condition condition = new Condition();
            condition.setKey("score");
            condition.setValue(60);
            condition.setOperator(Operator.GE);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("score"));
            assertTrue(sql.contains(">="));
        }

        @Test
        void ltOperator() {
            Condition condition = new Condition();
            condition.setKey("price");
            condition.setValue(100);
            condition.setOperator(Operator.LT);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("price"));
            assertTrue(sql.contains("<"));
        }

        @Test
        void leOperator() {
            Condition condition = new Condition();
            condition.setKey("quantity");
            condition.setValue(50);
            condition.setOperator(Operator.LE);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("quantity"));
            assertTrue(sql.contains("<="));
        }

        @Test
        void likeOperator() {
            Condition condition = new Condition();
            condition.setKey("name");
            condition.setValue("test");
            condition.setOperator(Operator.LIKE);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("LIKE"));
        }

        @Test
        void notLikeOperator() {
            Condition condition = new Condition();
            condition.setKey("name");
            condition.setValue("test");
            condition.setOperator(Operator.NOT_LIKE);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("LIKE"));
        }

        @Test
        void isNullOperator() {
            Condition condition = new Condition();
            condition.setKey("deleted_at");
            condition.setValue(null);
            condition.setOperator(Operator.IS_NULL);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("deleted_at"));
            assertTrue(sql.contains("IS NULL"));
        }

        @Test
        void isNotNullOperator() {
            Condition condition = new Condition();
            condition.setKey("email");
            condition.setValue(null);
            condition.setOperator(Operator.IS_NOT_NULL);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("email"));
            assertTrue(sql.contains("IS NOT NULL"));
        }

        @Test
        void inOperatorWithList() {
            Condition condition = new Condition();
            condition.setKey("id");
            condition.setValue(List.of(1, 2, 3));
            condition.setOperator(Operator.IN);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("id"));
            assertTrue(sql.contains("IN"));
        }

        @Test
        void notInOperatorWithList() {
            Condition condition = new Condition();
            condition.setKey("id");
            condition.setValue(List.of(4, 5));
            condition.setOperator(Operator.NOT_IN);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("id"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("IN"));
        }

        @Test
        void betweenOperator() {
            Condition condition = new Condition();
            condition.setKey("age");
            condition.setValue(List.of(18, 30));
            condition.setOperator(Operator.BETWEEN);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("age"));
            assertTrue(sql.contains("BETWEEN"));
        }

        @Test
        void notBetweenOperator() {
            Condition condition = new Condition();
            condition.setKey("price");
            condition.setValue(List.of(10, 100));
            condition.setOperator(Operator.NOT_BETWEEN);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            String sql = result.toSQL();

            assertTrue(sql.contains("price"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("BETWEEN"));
        }

        @Test
        void nestedAndCondition() {
            Condition child1 = new Condition();
            child1.setKey("name");
            child1.setValue("test");
            child1.setOperator(Operator.EQ);

            Condition child2 = new Condition();
            child2.setKey("age");
            child2.setValue(18);
            child2.setOperator(Operator.GT);

            Condition parent = new Condition();
            parent.setRelation(Relation.AND);
            parent.setChildren(List.of(child1, child2));

            QueryWrapper result = QueryWrapperBuilder.build(parent);
            String sql = result.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("age"));
        }

        @Test
        void nestedOrCondition() {
            Condition child1 = new Condition();
            child1.setKey("name");
            child1.setValue("test");
            child1.setOperator(Operator.EQ);

            Condition child2 = new Condition();
            child2.setKey("name");
            child2.setValue("demo");
            child2.setOperator(Operator.EQ);

            Condition parent = new Condition();
            parent.setRelation(Relation.OR);
            parent.setChildren(List.of(child1, child2));

            QueryWrapper result = QueryWrapperBuilder.build(parent);
            String sql = result.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("OR"));
        }

        @Test
        void invalidLeafNodeMissingKeyShouldBeIgnored() {
            Condition condition = new Condition();
            condition.setKey(null);
            condition.setValue("test");
            condition.setOperator(Operator.EQ);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            assertNotNull(result);
        }

        @Test
        void invalidLeafNodeMissingOperatorShouldBeIgnored() {
            Condition condition = new Condition();
            condition.setKey("name");
            condition.setValue("test");
            condition.setOperator(null);

            QueryWrapper result = QueryWrapperBuilder.build(condition);
            assertNotNull(result);
        }

        @Test
        void branchWithNoValidChildrenShouldBeIgnored() {
            Condition invalidChild = new Condition();
            invalidChild.setKey(null);
            invalidChild.setValue("test");
            invalidChild.setOperator(Operator.EQ);

            Condition parent = new Condition();
            parent.setRelation(Relation.AND);
            parent.setChildren(List.of(invalidChild));

            QueryWrapper result = QueryWrapperBuilder.build(parent);
            assertNotNull(result);
        }

        @Test
        void deeplyNestedCondition() {
            Condition leaf1 = new Condition();
            leaf1.setKey("a");
            leaf1.setValue(1);
            leaf1.setOperator(Operator.EQ);

            Condition leaf2 = new Condition();
            leaf2.setKey("b");
            leaf2.setValue(2);
            leaf2.setOperator(Operator.EQ);

            Condition innerBranch = new Condition();
            innerBranch.setRelation(Relation.OR);
            innerBranch.setChildren(List.of(leaf1, leaf2));

            Condition leaf3 = new Condition();
            leaf3.setKey("c");
            leaf3.setValue(3);
            leaf3.setOperator(Operator.EQ);

            Condition root = new Condition();
            root.setRelation(Relation.AND);
            root.setChildren(List.of(innerBranch, leaf3));

            QueryWrapper result = QueryWrapperBuilder.build(root);
            String sql = result.toSQL();

            assertTrue(sql.contains("a"));
            assertTrue(sql.contains("b"));
            assertTrue(sql.contains("c"));
        }
    }

    @Nested
    class ParseConditionTests {

        @Test
        void eqCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("name", Operator.EQ, "test");
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("="));
            assertTrue(sql.contains("test"));
        }

        @Test
        void neCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("status", Operator.NE, 0);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("status"));
            assertTrue(sql.contains("<>") || sql.contains("!="));
        }

        @Test
        void gtCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("age", Operator.GT, 18);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("age"));
            assertTrue(sql.contains(">"));
        }

        @Test
        void geCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("score", Operator.GE, 60);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("score"));
            assertTrue(sql.contains(">="));
        }

        @Test
        void ltCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("price", Operator.LT, 100);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("price"));
            assertTrue(sql.contains("<"));
        }

        @Test
        void leCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("quantity", Operator.LE, 50);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("quantity"));
            assertTrue(sql.contains("<="));
        }

        @Test
        void likeCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("name", Operator.LIKE, "test");
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("LIKE"));
        }

        @Test
        void notLikeCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("name", Operator.NOT_LIKE, "test");
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("name"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("LIKE"));
        }

        @Test
        void inCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("id", Operator.IN, List.of(1, 2, 3));
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("id"));
            assertTrue(sql.contains("IN"));
        }

        @Test
        void notInCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("id", Operator.NOT_IN, List.of(4, 5));
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("id"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("IN"));
        }

        @Test
        void isNullCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("deleted_at", Operator.IS_NULL, null);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("deleted_at"));
            assertTrue(sql.contains("IS NULL"));
        }

        @Test
        void isNotNullCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("email", Operator.IS_NOT_NULL, null);
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("email"));
            assertTrue(sql.contains("IS NOT NULL"));
        }

        @Test
        void betweenCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("age", Operator.BETWEEN, List.of(18, 30));
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("age"));
            assertTrue(sql.contains("BETWEEN"));
        }

        @Test
        void notBetweenCondition() {
            Consumer<QueryWrapper> consumer = QueryWrapperBuilder.parseCondition("price", Operator.NOT_BETWEEN, List.of(10, 100));
            QueryWrapper wrapper = new QueryWrapper();
            consumer.accept(wrapper);
            String sql = wrapper.toSQL();

            assertTrue(sql.contains("price"));
            assertTrue(sql.contains("NOT"));
            assertTrue(sql.contains("BETWEEN"));
        }
    }

    @Nested
    class GetMultipleValuesTests {

        @Test
        void listInputShouldReturnSameList() {
            List<Object> input = List.of(1, 2, 3);
            List<Object> result = QueryWrapperBuilder.getMultipleValues(input);
            assertEquals(input, result);
        }

        @Test
        void stringInputShouldSplitByComma() {
            List<Object> result = QueryWrapperBuilder.getMultipleValues("a,b,c");
            assertEquals(3, result.size());
            assertEquals("a", result.get(0));
            assertEquals("b", result.get(1));
            assertEquals("c", result.get(2));
        }

        @Test
        void singleStringShouldReturnSingleElementList() {
            List<Object> result = QueryWrapperBuilder.getMultipleValues("single");
            assertEquals(1, result.size());
            assertEquals("single", result.get(0));
        }

        @Test
        void invalidTypeShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    QueryWrapperBuilder.getMultipleValues(123));
        }

        @Test
        void nullValueShouldThrowException() {
            assertThrows(IllegalArgumentException.class, () ->
                    QueryWrapperBuilder.getMultipleValues(null));
        }

        @Test
        void emptyListShouldReturnEmptyList() {
            List<Object> result = QueryWrapperBuilder.getMultipleValues(List.of());
            assertTrue(result.isEmpty());
        }

        @Test
        void emptyStringShouldReturnSingleEmptyElement() {
            List<Object> result = QueryWrapperBuilder.getMultipleValues("");
            assertEquals(1, result.size());
            assertEquals("", result.get(0));
        }
    }

    @Nested
    class BuildQueryObjectTests {

        @Test
        void nullQueryShouldReturnEmptyWrapper() {
            QueryWrapper result = QueryWrapperBuilder.build((Object) null);
            assertNotNull(result);
        }
    }
}
