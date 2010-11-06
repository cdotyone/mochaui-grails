import org.apache.catalina.loader.WebappLoader

createVirtualDirectory = { tomcat,name,path ->
  buildroot= "/mochaui-grails/WEB-INF/classes"
  webroot  = new File(path).getCanonicalPath()
  println "Creating virtual directory of " + name + " pointed to " + webroot
  context = tomcat.addWebapp(name, webroot);
  context.reloadable = true
  WebappLoader loader = new WebappLoader(tomcat.class.classLoader)
  loader.addRepository(new File(buildroot).toURI().toURL().toString());
  context.loader = loader
  loader.container = context  
}  

eventConfigureTomcat = {tomcat ->
  createVirtualDirectory(tomcat,"/",'../mochaui/')
}


