package org.lrp.aqa_training_engine.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(exclude = {"tasks", "notes"})
@ToString(exclude = {"tasks", "notes"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @Type(type="uuid-char")
    @Column(name="uuid", columnDefinition = "VARCHAR(255)", insertable = false, updatable = false, nullable = false)
    private UUID uuid;

    @NotBlank(message = "{user.username.not_blank}")
    @Size(max = 30, min = 2, message = "{user.username.size}")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "{user.email.not_blank}")
    @Email(message = "{user.email.valid}")
    private String email;

    @NotBlank(message = "{user.password.not_blank}")
    @Size(min = 8, message = "{user.password.size}")
    private String password;

    @Transient
    private String passwordConfirm;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY,
                cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE,
                        CascadeType.DETACH
                })
    @JoinTable(name = "users_tasks",
               joinColumns = @JoinColumn(name = "user_uuid"),
               inverseJoinColumns = @JoinColumn(name = "task_uuid"))
    private Set<Task> tasks = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy="user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Note> notes = new HashSet<>();
}
