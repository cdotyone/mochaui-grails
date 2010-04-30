import org.apache.catalina.loader.WebappLoader
import com.polaropposite.mochaui.build.BuildMochaUI

def watchID = -1

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
  createVirtualDirectory(tomcat,"/mochaui",'../mochaui/src/demo')
  createVirtualDirectory(tomcat,"/mochaui/scripts",'../mochaui/src/demo/scripts')
  createVirtualDirectory(tomcat,"/mochaui/scripts/source",'../mochaui/src/core')
  createVirtualDirectory(tomcat,"/mochaui/themes",'../mochaui/src/themes')
  createVirtualDirectory(tomcat,"/mochaui/plugins",'../mochaui/src/plugins')


  def path = (new File("../mochaui/")).getCanonicalPath()
  watchID = BuildMochaUI.Watch(path,false)
}

eventExiting = {
  if(watchID>-1) BuildMochaUI.StopWatch(watchID)
}


