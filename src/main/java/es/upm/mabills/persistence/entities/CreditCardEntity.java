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
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String creditCardNumber;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity user;
}
