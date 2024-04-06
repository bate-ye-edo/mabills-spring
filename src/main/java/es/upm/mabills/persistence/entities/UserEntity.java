package es.upm.mabills.persistence.entities;

import es.upm.mabills.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;

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
    @NonNull
    private String password;
    @NonNull
    private String email;
    @NonNull
    private String mobile;
    public UserEntity(User user) {
        BeanUtils.copyProperties(user, this);
    }
}
