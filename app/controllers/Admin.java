package controllers;
 
import play.*;
import play.data.validation.Required;
import play.libs.Files;
import play.libs.Images;
import play.mvc.*;
 
import java.io.File;
import java.io.IOException;
import java.util.*;
 
import models.*;
 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
 
    public static void index() {
        List<Post> posts = Post.find("author.email", Security.connected()).fetch();
        render(posts);
    }
    
    public static void form(Long id) {
        if(id != null) {
            Post post = Post.findById(id);
            render(post);
        }
        render();
    }

    public static void save(Long id, String title, String content, String tags, 
    		String cssClassName, File picture) throws IOException {
    	
    	String imagePath = null;
    	
    	if(picture!=null){
    		imagePath = saveImage(picture,title);
    	}
    	
    	Post post = null;
    	
        CssClass cssClass = CssClass.find("name = ?",cssClassName).first();
        
        if(id == null) {
            // Create post
            User author = User.find("byEmail", Security.connected()).first();
            post = new Post(author, title, content, cssClass, imagePath);
        } else {
            // Retrieve post
            post = Post.findById(id);
            // Edit
            post.title = title;
            post.content = content;
            post.tags.clear();
            post.cssClass = cssClass;
            post.picturePath = imagePath;
        }
        // Set tags list
        for(String tag : tags.split("\\s+")) {
            if(tag.trim().length() > 0) {
                post.tags.add(Tag.findOrCreateByName(tag));
            }
        }
        // Validate
        validation.valid(post);
        if(validation.hasErrors()) {
            render("@form", post);
        }
        // Save
        post.save();
        
        index();
    }
    
    private static String saveImage(File picture,String title){
    	//--- saving image ---
        int dotPos = picture.getName().lastIndexOf(".");
        String extension = picture.getName().substring(dotPos);
        
    	//TODO javscript coté navigateur pour x1,y1,x2,Y2 crop
    	// see http://groups.google.com/group/play-framework/browse_thread/thread/fb81d19afdfd22e7/9d376535f2a8e431?lnk=gst&q=crop#9d376535f2a8e431
    	//Images.crop(picture, to, 0, 0, 200, 200);
        
    	//create new File
		File to = Play.getFile("/public/images/" + "picture_"+title.replace(" ","")+extension);
        Files.copy(picture, to);
        picture.delete();
        String imagePath=null;
        
        try {
			imagePath = to.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return imagePath;
    }
    
}
