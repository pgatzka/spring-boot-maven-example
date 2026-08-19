
package io.github.pgatzka.example.domain.entity;

import io.github.pgatzka.example.domain.core.AbstractEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "greeting")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Greeting extends AbstractEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_greeting__id_gen") @SequenceGenerator(name = "seq_greeting__id_gen", sequenceName = "seq_greeting__id", allocationSize = 50) @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Setter @Column(name = "author", nullable = false, length = 30)
    private String author;

    @Setter @Column(name = "message", length = 200)
    private String message;

    @Setter @Column(name = "subject", nullable = false, length = 30)
    private String subject;

    public Greeting(String author, String message, String subject) {
        this.author = author;
        this.message = message;
        this.subject = subject;
    }

}
