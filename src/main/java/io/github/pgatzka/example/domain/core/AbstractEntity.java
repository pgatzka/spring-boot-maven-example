
package io.github.pgatzka.example.domain.core;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Version @Column(name = "version", nullable = false)
    private Long version;

    @CreatedDate @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public abstract Long getId();

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (!Hibernate.getClass(this).equals(Hibernate.getClass(other)))
            return false;
        return uuid.equals(((AbstractEntity) other).uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return Hibernate.getClass(this).getSimpleName() + "{uuid=" + uuid + "}";
    }

}
