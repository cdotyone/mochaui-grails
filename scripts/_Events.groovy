import org.apache.catalina.loader.WebappLoader

eventConfigureTomcat = {tomcat ->
  println "### Starting load of custom application"
  def contextRoot = "/mochaui"
  def buildroot= "/mochaui-grails/WEB-INF/classes"
  def webroot  = new File('../mochaui/demo').getCanonicalPath()

  println buildroot
  println webroot

  context = tomcat.addWebapp(contextRoot, webroot);
  context.reloadable = true

  WebappLoader loader = new WebappLoader(tomcat.class.classLoader)

  loader.addRepository(new File(buildroot).toURI().toURL().toString());
  context.loader = loader
  loader.container = context

  println "### Ending load of custom application"
}

