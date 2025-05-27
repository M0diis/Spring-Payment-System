package me.modkzl.utils;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Collection;

public class ConditionBuilder {
    private Condition condition = DSL.noCondition();

    public <T> ConditionBuilder andEquals(Field<T> field, T value) {
        if (value != null) {
            condition = condition.and(field.eq(value));
        }
        return this;
    }

    public <T> ConditionBuilder andIn(Field<T> field, Collection<T> values) {
        if (values != null && !values.isEmpty()) {
            condition = condition.and(field.in(values));
        }
        return this;
    }

    public <T extends Comparable<? super T>> ConditionBuilder andGreaterOrEqual(Field<T> field, T value) {
        if (value != null) {
            condition = condition.and(field.ge(value));
        }
        return this;
    }

    public <T extends Comparable<? super T>> ConditionBuilder andLessOrEqual(Field<T> field, T value) {
        if (value != null) {
            condition = condition.and(field.le(value));
        }
        return this;
    }

    public Condition build() {
        return condition;
    }
}