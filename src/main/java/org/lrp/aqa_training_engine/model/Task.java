package org.lrp.aqa_training_engine.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(exclude = "users")
@ToString(exclude = "users")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @Type(type="uuid-char")
    @Column(name="uuid", columnDefinition = "VARCHAR(255)", insertable = false, updatable = false, nullable = false)
    private UUID uuid;

    @NotBlank(message = "{task.title.not_blank}")
    @Size(max = 200, message = "{task.title.size}")
    private String title;

    @Size(max = 200, message = "{task.description.size}")
    private String description;

    private String state;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @NotNull(message = "{task.deadline.not_null}")
    @Temporal(TemporalType.TIMESTAMP)
    @Future(message = "{task.deadline.future}")
    private Date deadline;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH
                },
                mappedBy = "tasks")
    private Set<User> users =  new HashSet<>();
}
