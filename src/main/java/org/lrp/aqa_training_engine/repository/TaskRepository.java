package org.lrp.aqa_training_engine.repository;

import org.lrp.aqa_training_engine.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query(value = "SELECT t.* FROM tasks t " +
                   "JOIN users_tasks ut ON ut.task_uuid=t.uuid " +
                   "JOIN users u ON u.uuid = ut.user_uuid " +
                   "WHERE u.username = :username " +
                   "AND t.state = :state " +
                   "ORDER BY t.deadline", nativeQuery = true)
    Set<Task> findByUsernameAndState(@Param("username") String username,
                                     @Param("state") String state);

    @Modifying
    @Transactional
    @Query(value = "DELETE ut FROM users_tasks ut " +
                   "JOIN users u ON u.uuid = ut.user_uuid " +
                   "WHERE u.username = :username " +
                   "AND ut.task_uuid = :taskUuid", nativeQuery = true)
    void deleteTaskOfUser(@Param("username") String username,
                          @Param("taskUuid") String taskUuid);
}
