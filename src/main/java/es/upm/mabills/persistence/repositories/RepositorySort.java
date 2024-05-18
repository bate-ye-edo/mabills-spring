package es.upm.mabills.persistence.repositories;

import org.springframework.data.domain.Sort;

public enum RepositorySort {
    BY_CREATION_DATE(Sort.by(Sort.Order.asc("creationDate")));

    private final Sort sort;
    RepositorySort(Sort sort) {
        this.sort = sort;
    }

    public Sort value() {
        return sort;
    }
}
