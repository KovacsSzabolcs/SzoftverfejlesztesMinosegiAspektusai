package ro.sapientia2015.story.dto;

import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.ToDo;

/**
 * @author Kiss Tibor
 */
public class StoryDTO {

    private Long id;

    @Length(max = Story.MAX_LENGTH_DESCRIPTION)
    private String description;

    @NotEmpty
    @Length(max = Story.MAX_LENGTH_TITLE)
    private String title;
    
    private Set<ToDo> todos;

    public StoryDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<ToDo> getTodos() {
    	return this.todos;
    }
    
    public void setTodos(Set<ToDo> todos) {
    	this.todos=todos;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
