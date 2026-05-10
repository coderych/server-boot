package com.coderych.commons.mybatisflex.model;

import com.coderych.commons.mybatisflex.enums.Operator;
import com.coderych.commons.mybatisflex.enums.Relation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConditionTests {

    @Test
    void defaultOperatorShouldBeEq() {
        Condition condition = new Condition();
        assertEquals(Operator.EQ, condition.getOperator());
    }

    @Test
    void defaultRelationShouldBeAnd() {
        Condition condition = new Condition();
        assertEquals(Relation.AND, condition.getRelation());
    }

    @Test
    void defaultChildrenShouldBeNull() {
        Condition condition = new Condition();
        assertNull(condition.getChildren());
    }

    @Test
    void defaultKeyValueShouldBeNull() {
        Condition condition = new Condition();
        assertNull(condition.getKey());
        assertNull(condition.getValue());
    }

    @Test
    void shouldSetAndGetKey() {
        Condition condition = new Condition();
        condition.setKey("name");
        assertEquals("name", condition.getKey());
    }

    @Test
    void shouldSetAndGetValue() {
        Condition condition = new Condition();
        condition.setValue("test");
        assertEquals("test", condition.getValue());
    }

    @Test
    void shouldSetAndGetOperator() {
        Condition condition = new Condition();
        condition.setOperator(Operator.LIKE);
        assertEquals(Operator.LIKE, condition.getOperator());
    }

    @Test
    void shouldSetAndGetRelation() {
        Condition condition = new Condition();
        condition.setRelation(Relation.OR);
        assertEquals(Relation.OR, condition.getRelation());
    }

    @Test
    void shouldSetAndGetChildren() {
        Condition child1 = new Condition();
        child1.setKey("age");
        child1.setValue(18);
        child1.setOperator(Operator.GT);

        Condition child2 = new Condition();
        child2.setKey("name");
        child2.setValue("test");
        child2.setOperator(Operator.LIKE);

        Condition parent = new Condition();
        parent.setChildren(List.of(child1, child2));

        assertNotNull(parent.getChildren());
        assertEquals(2, parent.getChildren().size());
        assertEquals("age", parent.getChildren().get(0).getKey());
        assertEquals("name", parent.getChildren().get(1).getKey());
    }

    @Test
    void shouldSupportEqualsAndHashCode() {
        Condition c1 = new Condition();
        c1.setKey("name");
        c1.setValue("test");
        c1.setOperator(Operator.EQ);

        Condition c2 = new Condition();
        c2.setKey("name");
        c2.setValue("test");
        c2.setOperator(Operator.EQ);

        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void shouldSupportToString() {
        Condition condition = new Condition();
        condition.setKey("name");
        condition.setValue("test");

        String str = condition.toString();
        assertNotNull(str);
        assertTrue(str.contains("name"));
        assertTrue(str.contains("test"));
    }

    @Test
    void shouldBuildNestedConditionTree() {
        Condition leaf1 = new Condition();
        leaf1.setKey("status");
        leaf1.setValue(1);
        leaf1.setOperator(Operator.EQ);

        Condition leaf2 = new Condition();
        leaf2.setKey("age");
        leaf2.setValue(18);
        leaf2.setOperator(Operator.GE);

        Condition branch = new Condition();
        branch.setRelation(Relation.OR);
        branch.setChildren(List.of(leaf1, leaf2));

        Condition root = new Condition();
        root.setRelation(Relation.AND);
        root.setChildren(List.of(branch));

        assertNotNull(root.getChildren());
        assertEquals(1, root.getChildren().size());
        assertEquals(Relation.OR, root.getChildren().get(0).getRelation());
        assertEquals(2, root.getChildren().get(0).getChildren().size());
    }
}
