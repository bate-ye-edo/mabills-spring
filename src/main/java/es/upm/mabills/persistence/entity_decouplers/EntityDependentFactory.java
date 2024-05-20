package es.upm.mabills.persistence.entity_decouplers;

import lombok.Getter;

import java.util.List;

@Getter
public enum EntityDependentFactory {

    BANK_ACCOUNT(List.of("credit_card", "expense")),
    CREDIT_CARD(List.of("expense"));

    private final List<String> entityDependentList;
    EntityDependentFactory(List<String> entityDependentList) {
        this.entityDependentList = entityDependentList;
    }
}
