package es.upm.mabills.model.filters;

import es.upm.mabills.exceptions.InvalidRequestException;
import lombok.Getter;

@Getter
public enum FilterField {
    CREDIT_CARD(EntityAndFieldName.builder().entityName("creditCard").fieldName("creditCardNumber").build()),
    AMOUNT(EntityAndFieldName.builder().fieldName("amount").build()),
    BANK_ACCOUNT(EntityAndFieldName.builder().entityName("bankAccount").fieldName("iban").build()),
    EXPENSE_DATE(EntityAndFieldName.builder().fieldName("expenseDate").build()),
    FORM_OF_PAYMENT(EntityAndFieldName.builder().fieldName("formOfPayment").build()),
    EXPENSE_CATEGORY(EntityAndFieldName.builder().entityName("expenseCategory").fieldName("name").build()),
    DESCRIPTION(EntityAndFieldName.builder().fieldName("description").build()),
    INCOME_DATE(EntityAndFieldName.builder().fieldName("incomeDate").build());

    private final EntityAndFieldName field;

    FilterField(EntityAndFieldName field) {
        this.field = field;
    }

    public String getFieldName() {
        return field.getFieldName();
    }

    public String getEntityName() {
        return field.getEntityName();
    }

    public static FilterField fromString(String value) {
        for (FilterField type : FilterField.values()) {
            if (type.name().equals(value)) {
                return type;
            }
        }
        throw new InvalidRequestException("Invalid filter field type");
    }
}
