package ro.sapientia2015.story.service;

import java.util.List;

import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.ToDo;

/**
 * @author Kovacs Szabolcs
 */
public interface ToDoService {

    /**
     * Adds a new to-do entry.
     * @param added The information of the added to-do entry.
     * @return  The added to-do entry.
     */
    public ToDo add(ToDoDTO added);

    /**
     * Deletes a to-do entry.
     * @param id    The id of the deleted to-do entry.
     * @return  The deleted to-do entry.
     * @throws NotFoundException    if no to-do entry is found with the given id.
     */
    public ToDo deleteById(Long id) throws NotFoundException;

    /**
     * Returns a list of to-do entries.
     * @return
     */
    public List<ToDo> findAll();

    /**
     * Finds a to-do entry.
     * @param id    The id of the wanted to-do entry.
     * @return  The found to-entry.
     * @throws NotFoundException    if no to-do entry is found with the given id.
     */
    public ToDo findById(Long id) throws NotFoundException;

    /**
     * Updates the information of a to-do entry.
     * @param updated   The information of the updated to-do entry.
     * @return  The updated to-do entry.
     * @throws NotFoundException    If no to-do entry is found with the given id.
     */
    public ToDo update(ToDoDTO updated) throws NotFoundException;
}
