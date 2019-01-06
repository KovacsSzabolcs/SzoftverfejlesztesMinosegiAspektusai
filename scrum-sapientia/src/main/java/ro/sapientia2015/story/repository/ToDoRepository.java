package ro.sapientia2015.story.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ro.sapientia2015.story.model.ToDo;

/**
 * @author Kovacs Szabolcs
 */
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
}
