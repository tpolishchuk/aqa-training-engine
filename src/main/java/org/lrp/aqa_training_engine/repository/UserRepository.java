package org.lrp.aqa_training_engine.repository;

import org.lrp.aqa_training_engine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);

    @Query(value = "SELECT u.* FROM users u " +
                   "JOIN users_tasks ut ON ut.user_uuid=u.uuid " +
                   "WHERE ut.task_uuid = :taskUuid", nativeQuery = true)
    Set<User> getAssociatedUsers(@Param("taskUuid") String taskUuid);
}
