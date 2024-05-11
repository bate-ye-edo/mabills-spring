package es.upm.mabills.persistence.entities;

import es.upm.mabills.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="mabillsUser")
public class UserEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;

    @OneToMany(mappedBy = "user")
    private List<ExpenseCategoryEntity> expenseCategories;

    @OneToMany(mappedBy = "user")
    private List<CreditCardEntity> creditCards;

    @OneToMany(mappedBy = "user")
    private List<BankAccountEntity> bankAccounts;

    public UserEntity(User user, @NonNull String encodedPassword) {
        BeanUtils.copyProperties(user, this);
        this.password = encodedPassword;
    }
}
