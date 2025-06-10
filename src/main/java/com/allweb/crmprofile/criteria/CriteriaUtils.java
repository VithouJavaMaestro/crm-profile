package com.allweb.crmprofile.criteria;

import org.springframework.data.relational.core.query.Criteria;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

public final class CriteriaUtils {

    private CriteriaUtils() {
    }

    public static Criteria like(String column, Object value, MatchMode matchMode) {
        Assert.notNull(matchMode, "matchMode cannot be null");
        if (!StringUtils.hasText(column) || value == null) {
            return Criteria.empty();
        }
        return switch (matchMode) {
            case STARTS_WITH -> like(column, "%" + value);
            case ENDS_WITH -> like(column, value + "%");
            case CONTAINS -> like(column, "%" + value + "%");
        };
    }

    public static Criteria like(String column, Object value) {

        return (StringUtils.hasText(column) && value != null)
                ? Criteria.where(column).like(value)
                : Criteria.empty();
    }

    public static Criteria in(String column, Collection<?> values) {
        return (StringUtils.hasText(column) && !CollectionUtils.isEmpty(values))
                ? Criteria.where(column).in(values)
                : Criteria.empty();
    }

    public enum MatchMode {
        STARTS_WITH, ENDS_WITH, CONTAINS
    }
}
