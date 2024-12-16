package com.expensetrackerapp.repository;

import com.expensetrackerapp.constants.Operation;
import com.expensetrackerapp.modal.Expense;
import com.expensetrackerapp.modal.Filter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ExpenseFilterRepository {

    @Autowired
    private EntityManager entityManager;

    private final List<String> ALLOWED_COLUMN_NAMES = List.of("expenseId", "salary", "department", "designation","name", "age", "gender","adharNumber");

    @Transactional
    public Page<Expense> getExpenses(List<Filter> filters, Pageable pageable) {

        HibernateCriteriaBuilder criteriaBuilder = entityManager.unwrap(Session.class).getCriteriaBuilder();

        JpaCriteriaQuery<Expense> criteriaQuery = criteriaBuilder.createQuery(Expense.class);
        Root<Expense> root = criteriaQuery.from(Expense.class);

        List<Predicate> predicates = new ArrayList<>();
        filters.forEach(filter -> {
            Predicate predicate = buildPredicate(criteriaBuilder, root, filter);
            if (predicate != null) {
                predicates.add(predicate);
            }
        });

//        Predicate[] predicates = filters.stream()
//                .map(filter -> {
//                    String field = filter.getFieldName();
//                    Object value = filter.getValue();
//                    Path<Object> path = root.get(field);
//
//                    return buildPredicate(criteriaBuilder, root, filter);

//                    if (value instanceof String) {
//                        return criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + ((String) value).toLowerCase() + "%");
//                    } else if (value instanceof Integer) {
//                        return criteriaBuilder.equal(path, value);
//                    } else if (value instanceof Double) {
//                        return criteriaBuilder.equal(path, value);
//                    } else if (value instanceof LocalDate) {
//                        return criteriaBuilder.equal(path, value);
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toArray(Predicate[]::new);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        List<Expense> expenses = entityManager.createQuery(criteriaQuery).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        // Total count of results
        Long totalCount = entityManager.createQuery(criteriaQuery.createCountQuery()).getSingleResult();

        return new PageImpl<>(expenses, pageable, totalCount);
    }


    private void validateEntityFields(List<Filter> filters) {
        List<String> filterEntityFields = filters.stream().map(Filter::getFieldName).toList();

        boolean isInvalid = filterEntityFields.stream().anyMatch(field -> !ALLOWED_COLUMN_NAMES.contains(field));
        if (!isInvalid) {
            throw new RuntimeException("Invalid filter parameters");
        }

    }

    public Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<Expense> root, Filter filter) {
        Object value = filter.getValue();
        String fieldName = filter.getFieldName();

        Operation operation = Operation.valueOf(filter.getOperation());
        switch (operation) {
            case EQ -> {
                return criteriaBuilder.equal(root.get(fieldName), value);
            }
            case NOT_EQ -> {
                return criteriaBuilder.notEqual(root.get(fieldName), value);
            }
            case GT -> {
                if (value instanceof Integer | value instanceof Long | value instanceof Double) {
                    assert value instanceof Integer;
                    return criteriaBuilder.greaterThan(root.get(fieldName), (Integer) value);
                }
            }
            case LT -> {
                if (value instanceof Integer | value instanceof Long | value instanceof Double) {
                    assert value instanceof Integer;
                    return criteriaBuilder.lessThan(root.get(fieldName), (Integer) value);
                }
            }
            case GE -> {
                if (value instanceof Integer | value instanceof Long | value instanceof Double) {
                    assert value instanceof Integer;
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), (Integer) value);
                }
            }
            case LE -> {
                if (value instanceof Integer | value instanceof Long | value instanceof Double) {
                    assert value instanceof Integer;
                    return criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), (Integer) value);
                }
            }
            case BETWEEN -> {
                if (value instanceof Object[] && ((Object[]) value).length == 2) {
                    Object[] range = (Object[]) value;
                    return criteriaBuilder.between(root.get(fieldName), (Comparable) range[0], (Comparable) range[1]);
                } else {
                    throw new IllegalArgumentException("Value for BETWEEN must be an array of size 2.");
                }
            }
            case NOT_BETWEEN -> {
                // Not Between
                if (value instanceof Object[] && ((Object[]) value).length == 2) {
                    Object[] range = (Object[]) value;
                    return criteriaBuilder.not(criteriaBuilder.between(root.get(fieldName), (Comparable) range[0], (Comparable) range[1]));
                } else {
                    throw new IllegalArgumentException("Value for NOT_BETWEEN must be an array of size 2.");
                }
            }
            case LIKE -> {
                if (value instanceof Integer | value instanceof Long | value instanceof Double) {

                }
                assert value instanceof String;
                return criteriaBuilder.like(criteriaBuilder.lower(root.get(fieldName)), "%" + ((String) value).toLowerCase() + "%");
            }
            case NOT_LIKE -> {
                assert value instanceof String;
                return criteriaBuilder.notLike(criteriaBuilder.lower(root.get(fieldName)), "%" + ((String) value).toLowerCase() + "%");
            }
            default -> throw new UnsupportedOperationException("Unsupported operation: " + filter.getOperation());
        }
        return null;
    }

}
