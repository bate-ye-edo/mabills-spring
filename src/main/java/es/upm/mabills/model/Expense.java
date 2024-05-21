package es.upm.mabills.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Expense {
    private String uuid;
    @NotNull
    private BigDecimal amount;
    private Timestamp creationDate;
    @NotNull
    private Timestamp expenseDate;
    private String description;
    private FormOfPayment formOfPayment;
    private ExpenseCategory expenseCategory;
    private CreditCard creditCard;
    private BankAccount bankAccount;
}
