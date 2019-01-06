package ro.sapientia2015.story.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.repository.StoryRepository;
import ro.sapientia2015.story.repository.ToDoRepository;

import javax.annotation.Resource;

import java.util.Iterator;
import java.util.List;

/**
 * @author Kiss Tibor
 */
@Service
public class RepositoryStoryService implements StoryService {

    @Resource
    private StoryRepository repository;
    
    @Resource
    private ToDoRepository toDoRepository;
    
    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
	public Story deleteToDo(Long storyID,Long todoID)throws NotFoundException{
    	Story model = findById(storyID);
    	
    	ToDo to=toDoRepository.findOne(todoID);
    	model.deleteToDo(to);
    	//model.deleteToDo();
    	return model;
    }
    
    
    
    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
    public Story addToDo(StoryDTO story,ToDoDTO todo) throws NotFoundException {
        Story model = findById(story.getId());
        
        ToDo temp=ToDo.getBuilder(todo.getTitle())
        		.description(todo.getDescription())
        		.story(model)
        		.build();
        		
        model.addToDo(temp);

        return model;
    	
    }
    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
	public
    Story addToDo(Long storyID,ToDoDTO todo)throws NotFoundException{
        Story model = findById(storyID);
        
        ToDo temp=ToDo.getBuilder(todo.getTitle())
        		.description(todo.getDescription())
        		.story(model)
        		.build();
        		
        model.addToDo(temp);

        return model;
    }
    @Transactional
    @Override
    public Story add(StoryDTO added) {

        Story model = Story.getBuilder(added.getTitle())
                .description(added.getDescription())
                .build();
        
        return repository.save(model);
    }

    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
    public Story deleteById(Long id) throws NotFoundException {
        Story deleted = findById(id);
        
        /*
        List<ToDo> result=toDoRepository.findAll();
        
        if(result!=null&&deleted!=null) {
			for (ToDo temp : result) {
				if(temp!=null) {
					
					if(temp.getStory()!=null&&temp.getStory().getId()==deleted.getId()) {
						
						toDoRepository.delete(temp);
					}
				}
			}
        }
        */
        repository.delete(deleted);
        return deleted;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Story> findAll() {
       return repository.findAll();
    }

    @Transactional(readOnly = true, rollbackFor = {NotFoundException.class})
    @Override
    public Story findById(Long id) throws NotFoundException {
        Story found = repository.findOne(id);
       // repository.f
        if (found == null) {
            throw new NotFoundException("No entry found with id: " + id);
        }

        return found;
    }

    @Transactional(rollbackFor = {NotFoundException.class})
    @Override
    public Story update(StoryDTO updated) throws NotFoundException {
        Story model = findById(updated.getId());
        model.update(updated.getDescription(), updated.getTitle());

        return model;
    }
}
