package es.upm.mabills.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BankAccount", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"iban", "username"})
})
public class BankAccountEntity {
    @Id
    @GeneratedValue
    private int id;

    @UuidGenerator
    private UUID uuid;

    @Column(nullable = false)
    private String iban;

    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.EAGER)
    private List<CreditCardEntity> creditCards;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private UserEntity user;
}
