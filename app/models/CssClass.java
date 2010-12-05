package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;


@Entity
public class CssClass extends Model {
	
	@Required
	public String name;
	@Required
	public String value;

	public CssClass(String name,String value) {
		this.name=name;
		this.value=value;
	}
	
    public String toString() { 
        return name; 
    } 
    
}