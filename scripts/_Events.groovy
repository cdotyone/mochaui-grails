import org.apache.catalina.loader.WebappLoader
import net.contentobjects.jnotify.*

def watchID = -1

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


  // initialize file change notifications
  def path = new File("d:\\Data\\java\\mochaui\\src\\").getCanonicalPath()
  def mask = JNotify.FILE_CREATED  |
              JNotify.FILE_DELETED  |
              JNotify.FILE_MODIFIED |
              JNotify.FILE_RENAMED;

  watchID = JNotify.addWatch(path, mask, true, new FileChangeWatcher())  
}

eventExiting = {
  if(watchID>-1) JNotify.removeWatch(watchID)
}

eventBuildMocha = { path->
  println 'eventBuildMocha'
  println path
}

