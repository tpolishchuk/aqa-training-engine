package org.lrp.aqa_training_engine.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(exclude = {"user", "createdAt", "updatedAt"})
@ToString(exclude = {"user"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notes")
public class Note {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "uuid", columnDefinition = "VARCHAR(255)", insertable = false, updatable = false, nullable = false)
    private UUID uuid;

    @NotBlank(message = "{note.body.not_blank}")
    @Size(max = 500, message = "{note.body.size}")
    private String body;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
