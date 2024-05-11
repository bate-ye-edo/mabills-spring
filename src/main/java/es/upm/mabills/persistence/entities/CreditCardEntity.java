package es.upm.mabills.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="CreditCard", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"creditCardNumber", "username"})
})
public class CreditCardEntity {
    @Id
    @GeneratedValue
    private int id;

    @UuidGenerator
    private UUID uuid;

    @Column(nullable = false)
    private String creditCardNumber;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "bankAccountId")
    private BankAccountEntity bankAccount;
}
