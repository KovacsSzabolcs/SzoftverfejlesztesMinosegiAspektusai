package ro.sapientia2015.story.model;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;

import javax.persistence.*;

/**
 * @author Kovacs Szabolcs
 */
@Entity
@Table(name="todo")
public class ToDo implements Serializable{

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

    
    @ManyToOne
    @JoinColumn(name ="story")
    private Story story;

    

    public ToDo() {

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
    
    public Story getStory() {
    	return this.story;
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

    public static class Builder {

        private ToDo built;

        public Builder(String title) {
            built = new ToDo();
            built.title = title;
        }

        public ToDo build() {
            return built;
        }

        public Builder description(String description) {
            built.description = description;
            return this;
        }
        public Builder story(Story story) {
            built.story = story;
            return this;
        }
        public Builder id(Long id) {
            built.id = id;
            return this;
        }
        
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
