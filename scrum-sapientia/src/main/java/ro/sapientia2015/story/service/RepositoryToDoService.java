package ro.sapientia2015.story.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.repository.ToDoRepository;

import javax.annotation.Resource;

import java.util.List;

/**
 * @author Kovacs Szabolcs
 */
@Service
public class RepositoryToDoService implements ToDoService {

    @Resource
    private ToDoRepository repository;

    @Transactional
    @Override
    public ToDo add(ToDoDTO added) {

    	ToDo model = ToDo.getBuilder(added.getTitle())
                .description(added.getDescription())
                .build();

        return repository.save(model);
    }

    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
    public ToDo deleteById(Long id) throws NotFoundException {
    	ToDo deleted = findById(id);
        repository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ToDo> findAll() {
       return repository.findAll();
    }

    @Transactional(readOnly = true, rollbackFor = {NotFoundException.class})
    @Override
    public ToDo findById(Long id) throws NotFoundException {
    	ToDo found = repository.findOne(id);
    	
        if (found == null) {
            throw new NotFoundException("No entry found with id: " + id);
        }

        return found;
    }

    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
    public ToDo update(ToDoDTO updated) throws NotFoundException {
    	ToDo model = findById(updated.getId());
        model.update(updated.getDescription(), updated.getTitle());

        return model;
    }
}
