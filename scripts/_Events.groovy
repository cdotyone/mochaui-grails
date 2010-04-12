import org.apache.catalina.loader.WebappLoader

eventConfigureTomcat = {tomcat ->
  def contextRoot = "/mochaui"
  def buildroot= "/mochaui-grails/WEB-INF/classes"
  def webroot  = new File('../mochaui/demo').getCanonicalPath()

  context = tomcat.addWebapp(contextRoot, webroot);
  context.reloadable = true

  WebappLoader loader = new WebappLoader(tomcat.class.classLoader)

  loader.addRepository(new File(buildroot).toURI().toURL().toString());
  context.loader = loader
  loader.container = context
}

