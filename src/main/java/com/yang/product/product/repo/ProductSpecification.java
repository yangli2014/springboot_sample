package com.yang.product.product.repo;

import com.yang.product.product.dto.SearchCriteria;
import com.yang.product.product.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification implements Specification <Product> {

    private final SearchCriteria criteria;

    public ProductSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasLength(criteria.getProductName())) {
            predicates.add(builder.like(builder.lower(root.get("name")), "%"+criteria.getProductName().toLowerCase()+"%"));
            //predicates.add(builder.like(root.get("name"), "%"+criteria.getProductName()+"%"));
        }
        if(criteria.getMinPrice()!=null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
        }
        if(criteria.getMaxPrice()!=null) {
            predicates.add(builder.lessThan(root.get("price"), criteria.getMaxPrice()));
        }

        if(criteria.getMinPostedDate()!=null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("postDate"), criteria.getMinPostedDate()));
        }
        if(criteria.getMaxPostedDate() != null) {
            predicates.add(builder.lessThan(root.get("postDate"), criteria.getMaxPostedDate()));
        }

        return builder.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
