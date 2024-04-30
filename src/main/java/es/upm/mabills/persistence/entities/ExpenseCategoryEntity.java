package es.upm.mabills.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
import lombok.NonNull;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="ExpenseCategory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "userId"})
})
public class ExpenseCategoryEntity {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @Column(nullable = false)
    private Timestamp creationDate;

    @UuidGenerator
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "userId")
    @NonNull
    private UserEntity user;

    @PrePersist
    public void prePersist() {
        if(Objects.isNull(this.creationDate)) {
            this.creationDate = new Timestamp(System.currentTimeMillis());
        }
    }

    public ExpenseCategoryEntity(int userId, @NonNull String name) {
        this.user = UserEntity.builder()
                .id(userId)
                .password("")
                .email("")
                .mobile("")
                .username("")
                .build();
        this.name = name;
    }
}
