package ro.sapientia2015.story.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

/**
 * @author Kiss Tibor
 */
@Entity
@Table(name="story")

public class Story implements Serializable{
    public static final int MAX_LENGTH_DESCRIPTION = 500;
    public static final int MAX_LENGTH_TITLE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "creation_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime creationTime;

    @Column(name = "description", nullable = true, length = MAX_LENGTH_DESCRIPTION)
    private String description;

    @Column(name = "modification_time", nullable = false)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime modificationTime;

    @Column(name = "title", nullable = false, length = MAX_LENGTH_TITLE)
    private String title;

    @Version
    private long version;

    

	@OneToMany(fetch = FetchType.EAGER,mappedBy="story",orphanRemoval=true, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	Set<ToDo> todos = new HashSet<ToDo>();
	
    public Story() {

    }

    public static Builder getBuilder(String title) {
        return new Builder(title);
    }

    public Long getId() {
        return id;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getModificationTime() {
        return modificationTime;
    }

    public String getTitle() {
        return title;
    }

    public long getVersion() {
        return version;
    }
    public Set<ToDo> getTodos() {
    	return this.todos;
    }
    @PrePersist
    public void prePersist() {
        DateTime now = DateTime.now();
        creationTime = now;
        modificationTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        modificationTime = DateTime.now();
    }

    public void update(String description, String title) {
        this.description = description;
        this.title = title;
    }
    
    public void addToDo(ToDo todo) {
    	this.todos.add(todo);
    }

    public void deleteToDo(ToDo todo) {
    	this.todos.remove(todo);
    }
 
    public static class Builder {

        private Story built;

        public Builder(String title) {
            built = new Story();
            built.title = title;
        }

        public Story build() {
            return built;
        }

        public Builder description(String description) {
            built.description = description;
            return this;
        }
        public Builder todos(Set<ToDo> todos) {
            built.todos = todos;
            return this;
        }
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
