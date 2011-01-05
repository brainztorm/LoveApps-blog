package controllers;
 
import java.io.File;
import java.util.*;
 
import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;
 
import models.*;
 
public class Application extends Controller {
 
	@Before
	static void addDefaults() {
	    renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
	    renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
	}

    public static void index() {
        Post frontPost = Post.find("order by postedAt desc").first();
        List<Post> olderPosts = Post.find("order by postedAt desc").from(1).fetch(10);
        render(frontPost, olderPosts);
    }

    public static void show(Long id) {
        Post post = Post.findById(id);
        //random id generated to identify captcha
        String randomId = Codec.UUID();
        render(post,randomId);
    }
    
    public static void postComment(
            Long postId, 
            @Required(message="Author is required") String author, 
            @Required(message="A message is required") String content, 
            @Required(message="Please type the code") String code, 
            String randomID) {
        Post post = Post.findById(postId);
        //Catche.get(randomID) retrieve the user captcha code in the cache (via this id) 
        validation.equals(
            code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
            render("Application/show.html", post, randomID);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        show(postId);
    }

    //This method is called directly in the view (show.html) within the "randomId" parameter
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        //A user captcha code is generated with a color font
        String code = captcha.getText("#000000");
        //The captcha is saved in a cache to survive the page context:
        //the id is the key to retrieve and compare (when needed) the code
        //@see stateless architecture
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }
    
    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }
    
    public static void showPostWithThisCssClass(String cssClass){
        List<Post> posts = Post.findClassedWith(cssClass);
        render(cssClass,posts);
    }
    
    public static void allPosts(){
        List<Post> posts = Post.all().fetch();
        render(posts);
    }
    
    public static void getPicture(long id) {
    	Post post = Post.findById(id);
    	//TODO is it possible without create a new File() object ?
        if(post.picturePath!=null){
        	renderBinary(new File(post.picturePath));
        }
    } 
}
