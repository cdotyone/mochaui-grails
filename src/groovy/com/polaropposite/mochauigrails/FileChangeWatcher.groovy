package com.polaropposite.mochauigrails

import net.contentobjects.jnotify.JNotifyListener
import java.util.concurrent.Semaphore
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.ant.GrailsTask
import grails.util.GrailsUtil

class FileChangeWatcher implements JNotifyListener {
    def onlyone = new Semaphore(1)

    // fires events, and suppresses double messages from OS
    def doChangeNotify = { change,rootPath,oldName,newName ->
      def prevChange

      if(onlyone.tryAcquire()) {
        Thread.start {
          Thread.sleep(3000)

          new BuildMochaUI()
          
          onlyone.release()
        }
      }
    }

    def void fileRenamed(int wd, String rootPath, String oldName, String newName) {
      doChangeNotify("renamed",rootPath,oldName,newName)
    }
    def void fileModified(int wd, String rootPath, String name) {
      doChangeNotify("modified",rootPath,name,name)
    }
    def void fileDeleted(int wd, String rootPath, String name) {
      doChangeNotify("deleted",rootPath,name,name)
    }
    def void fileCreated(int wd, String rootPath, String name) {
      doChangeNotify("created",rootPath,name,name)
    }
}
