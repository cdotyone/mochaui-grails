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
          Thread.sleep(2000)

          def task = new GrailsTask()
          task.setScript("BuildMochaUI")
          task.setEnvironment(GrailsUtil.environment)
          def ref = new Reference()
          ref.set(new File('./WEB-INF/classes').getCanonicalPath())
          task.setClasspathRef(ref)
          //task.classpath =
          task.execute()
          
          //ApplicationHolder.application.parentContext.
          //BuildMochaUI()
/*          def build = new File(/scripts\BuildMochaUI.groovy/).getCanonicalFile()
          // Execute the script
          def cmd = ['grails', 'run-script', build]
          Process executingProcess = cmd.execute()

          // Read process output and print on console
          def errorStreamPrinter = new StreamPrinter(executingProcess.err)
          def outputStreamPrinter = new StreamPrinter(executingProcess.in)
          [errorStreamPrinter, outputStreamPrinter]*.start()*/


      //    if(change=="renamed") println "renamed " + rootPath + " : " + oldName + " -> " + newName
      //    if(change=="modified") println "modified " + rootPath + " : " + oldName
      //    if(change=="deleted") println "deleted " + rootPath + " : " + oldName
      //    if(change=="created")  println "created " + rootPath + " : " + oldName
          //event("BuildMocha", [rootPath])

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
