package models;

import java.io.File;

import javax.persistence.Entity;

import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Picture extends Model {

	public String imagePath;
	
}
