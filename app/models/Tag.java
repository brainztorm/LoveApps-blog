package models; 
 
import java.util.*; 
import javax.persistence.*; 
 
import play.data.validation.Required;
import play.db.jpa.*; 
 
@Entity 
public class Tag extends Model implements Comparable<Tag> { 
 
	@Required
    public String name; 
    
    public String toString() { 
        return name; 
    } 
    
    public int compareTo(Tag otherTag) { 
        return name.compareTo(otherTag.name); 
    } 
 
    public static Tag findOrCreateByName(String name) { 
        Tag tag = Tag.find("byName", name).first(); 
        if(tag == null) { 
            tag = new Tag(name); 
        } 
        return tag; 
    } 

    public static List<Map> getCloud() {
    	
    	//Create a new List of Map in witch each Map contains 1 occurence of each tag with his pound (value)
    	//In general : "group by" lets you use aggregate functions, like 
    	//avg, max, min, sum, and count. "distinct" just removes duplicates ... so :
    	//"join p.tags as t group by t.name" = take the post tags (p.tags) and group them by name distinctively
    	//"order by count(p.id) desc, t.name asc" = order by the bigger pound and in alphabetical order if the pound part is equal
        List<Map> result = Tag.find(
            "select new map(t.name as tag, count(p.id) as pound) from Post p join p.tags as t group by t.name order by count(p.id) desc, t.name asc"
        ).fetch();
        return result;
    }
    
    private Tag(String name) { 
        this.name = name; 
    } 

}