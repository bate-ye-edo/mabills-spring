package es.upm.mabills.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="CreditCard", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"creditCardNumber", "userId"})
})
public class CreditCardEntity {
    @Id
    @UuidGenerator
    private UUID uuid;

    @Column(nullable = false)
    private String creditCardNumber;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Timestamp creationDate;

    @ManyToOne
    @JoinColumn(name = "bankAccountId", referencedColumnName = "uuid")
    private BankAccountEntity bankAccount;

    @PrePersist
    public void prePersist() {
        if(Objects.isNull(this.creationDate)) {
            this.creationDate = new Timestamp(System.currentTimeMillis());
        }
    }
}
