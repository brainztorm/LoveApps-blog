package controllers;
 
import models.CssClass;
import models.User;
import play.*;
import play.mvc.*;

@Check("admin")
@With(Secure.class)
@CRUD.For(CssClass.class)
public class CssClasses extends CRUD {    
}
