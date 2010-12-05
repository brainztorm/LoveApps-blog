package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.data.validation.*;
import play.db.jpa.*;
 
@Entity
public class Post extends Model {
 
	public Picture picture;
	
	@Required
    public String title;

	@Required
	public Date postedAt;
    
    @Lob
    @Required
    @MaxSize(10000)
    public String content;
    
    @ManyToOne
    @Required
    public CssClass cssClass;
    
    @Required
    @ManyToOne
    public User author;
    
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<Comment> comments;

    @ManyToMany(cascade=CascadeType.PERSIST) 
    public Set<Tag> tags; 
    
    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content).save();
        this.comments.add(newComment);
        return this;
    }
    
    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }
     
    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }
    
    public Post tagItWith(String name) { 
        tags.add(Tag.findOrCreateByName(name)); 
        return this; 
    } 
    
    public static List<Post> findTaggedWith(String tag) { 
    	//"p.tags as t" t is considered as p.tags
        return Post.find( 
            "select distinct p from Post p join p.tags as t where t.name = ?", tag 
        ).fetch(); 
    } 
    
    public static List<Post> findClassedWith(String cssClass) { 
    	//"p.tags as t" t is considered as p.tags
        return Post.find("select distinct p from Post p where p.cssClass.name = ?", cssClass).fetch();
    } 
    
    public static List<Post> findTaggedWith(String... tags) {
    	// idem que la méthode précédente sauf que :
    	// "having count(t.id) = :size" -> compter les t.id et ne sortir que ceux égaux a size. Autrement dit sortir 
    	//les posts dont le nombres de tags est = a ceux passé en parametres (via le String...) 
        return Post.find(
            "select distinct p.id from Post p join p.tags as t where t.name in (:tags) group by p.id having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
    
    public String toString() {
        return title;
    }
    
    public Post(User author, String title, String content, CssClass cssClass, Picture picture) { 
        this.comments = new ArrayList<Comment>(); 
        this.tags = new TreeSet<Tag>(); 
        this.author = author; 
        this.title = title; 
        this.content = content; 
        this.postedAt = new Date(); 
        this.cssClass = cssClass;
        this.picture = picture;
    } 
    
}
