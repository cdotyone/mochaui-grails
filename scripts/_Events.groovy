import org.apache.catalina.loader.WebappLoader
import net.contentobjects.jnotify.*
import com.polaropposite.mochauigrails.FileChangeWatcher

def watchID = -1

createVirtualDirectory = { tomcat,name,path ->
  buildroot= "/mochaui-grails/WEB-INF/classes"
  webroot  = new File(path).getCanonicalPath()
  context = tomcat.addWebapp(name, webroot);
  context.reloadable = true
  WebappLoader loader = new WebappLoader(tomcat.class.classLoader)
  loader.addRepository(new File(buildroot).toURI().toURL().toString());
  context.loader = loader
  loader.container = context  
}  

eventConfigureTomcat = {tomcat ->
  createVirtualDirectory(tomcat,"/mochaui",'../mochaui/src/demo')
  createVirtualDirectory(tomcat,"/mochaui/scripts",'../mochaui/src/demo/scripts')
  createVirtualDirectory(tomcat,"/mochaui/scripts/source",'../mochaui/src/scripts')
  createVirtualDirectory(tomcat,"/mochaui/themes",'../mochaui/src/themes')
  createVirtualDirectory(tomcat,"/mochaui/plugins",'../mochaui/src/plugins')

  // initialize file change notifications
  def path = new File(/..\mochaui\src/).getCanonicalPath()
  def mask = JNotify.FILE_CREATED  |
              JNotify.FILE_DELETED  |
              JNotify.FILE_MODIFIED |
              JNotify.FILE_RENAMED;

  watchID = JNotify.addWatch(path, mask, true, new FileChangeWatcher())  
}

eventExiting = {
  if(watchID>-1) JNotify.removeWatch(watchID)
}


