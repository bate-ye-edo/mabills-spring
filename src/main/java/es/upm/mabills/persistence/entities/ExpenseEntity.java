package es.upm.mabills.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="Expense",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"uuid", "userId"})
})
public class ExpenseEntity {
    @Id
    @UuidGenerator
    private UUID uuid;
    @Column(nullable = false)
    private BigDecimal amount;
    private Timestamp creationDate;
    @Column(nullable = false)
    private Timestamp expenseDate;
    private String description;
    private String formOfPayment;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "expenseCategoryId", referencedColumnName = "uuid")
    private ExpenseCategoryEntity category;
}
