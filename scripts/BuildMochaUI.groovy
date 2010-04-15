import org.apache.tools.ant.taskdefs.Ant

includeTargets << grailsScript("Init")

def mootoolsCore = "mootools-1.2.2-core-yc.js"
def mootoolsCoreMod = null
def mootoolsMore = "mootools-1.2.2-more-yc.js"
def mootoolsMoreMod = null

removeOldMooTools = { from ->
  new File(from).eachFileRecurse() { file->
    jsname = file.getName()
    if(!file.isDirectory() && jsname.indexOf('mootools-')>=0 ) {
      if(jsname!=mootoolsCore && jsname!=mootoolsMore)
        ant.delete(file:"${file.getAbsolutePath()}")
    }
  }
}

copyResources = { from,to,fileType,clear,compress,exclude ->
  // determine source and destination base paths
  def s=File.separatorChar
  to+=s

  // make sure base destination path exists
  ant.mkdir( dir:"${to}" )

  // remove js files that no longer exist
  def f = new File( to )
  if(clear) {
    f.eachFileRecurse() { file->
      if(!file.isDirectory()) {
        def srcfile = (from+s+file.getCanonicalPath()).replace(to,'')
        def file2 = new File(srcfile)

        println srcfile
        println file2.exists()

        // does the file exist
        if(!file2.exists()) {
          ant.delete(file:"${file.getCanonicalPath()}")
        }
      }
    }
  }

  f = new File( from )
  f.eachFileRecurse() { file->
    def fromFile = file.getCanonicalPath()
    def tofile = to+fromFile.replace(from+s,'')

    // if it is a file
    if(!file.isDirectory()) {
      def file2 = new File(tofile)

      // make sure path exists
      toPath = file2.getCanonicalPath().replace(file2.getName(),'')
      ant.mkdir( dir:"${toPath}" )

      // if target does not exist or is older then copy/compress
      if((!file2.exists() || file2.lastModified()<file.lastModified()) && fromFile.indexOf('.')>0) {

        // if it is not already compressed
        if(tofile.indexOf('.min.')<0 && tofile.indexOf('-yc.')<0 && fromFile.endsWith('.'+fileType) && compress) {
          // we need to compress so fire off a command to execute the yuicompressor
          println ' [compress] compressing ' +fromFile+' to '+tofile
          def command = "java -jar "+new File('.').getCanonicalPath()+s+"lib"+s+"yuicompressor-2.4.2.jar -o ${tofile} ${fromFile}"
          def proc = command.execute()                 // Call *execute* on the string
          proc.waitFor()

        } else {
          if(fromFile.indexOf('.html')>0) {
            println '     [copy] Copying 1 file to ' + tofile
            new File(tofile) << new File(fromFile).text.replace('{mootools-core}',mootoolsCore).replace('{mootools-more}',mootoolsMore)
          } else {
            // copy the file
            ant.copy(file:"${fromFile}", tofile:"${tofile}", overwrite: true)
          }
        }
      }
    } else {
      // if it is a directory make it
      ant.mkdir( dir:"${tofile}" )
    }
  }
}

target(main: "Assemble mochaui.js") {
  s = File.separatorChar

  // resource directories
  mooTools = new File(/..\mochaui\\src\\mootools/)
  pluginsDir = new File(/..\mochaui\\src\\mochaui\\plugins/).getCanonicalPath()
  themesDir = new File(/..\mochaui\\src\\mochaui\\themes/).getCanonicalPath()
  demoDir = new File(/..\mochaui\\src\\mochaui\\demo/).getCanonicalPath()

  // detect mootool filenames, so replace in demo section can happen
  mooTools.eachFile { file->
    jsname = file.getName()
    if(jsname.indexOf('mootools-')>=0 && jsname.indexOf('.js')>0) {
      if(jsname.indexOf('-core')>0) {
        mootoolsCoreMod = file.lastModified()
        mootoolsCore = jsname
      }
      if(jsname.indexOf('-more')>0) {
        mootoolsMoreMod = file.lastModified()
        mootoolsMore = jsname
      }
    }
  }

  // destination script folders
  demoJS = new File(/..\mochaui\\demo\\scripts/).getCanonicalPath()
  buildJS = new File(/..\mochaui\\build/).getCanonicalPath()

  //------------------------------------------------------
  // now copy themes and plugins to demo
  copyResources(demoDir,new File(/..\mochaui\\demo/).getCanonicalPath(),'js',true,false,[pluginsDir,themesDir])
  copyResources(pluginsDir,new File(/..\mochaui\\demo\\plugins/).getCanonicalPath(),'js',true,false,[])
  copyResources(themesDir,new File(/..\mochaui\\demo\\themes/).getCanonicalPath(),'js',true,false,[])
  removeOldMooTools(demoJS)
  copyResources(mooTools.getCanonicalPath(),demoJS,'js',false,false,[])

  //------------------------------------------------------
  // now copy themes and plugins to the build folder
  copyResources(pluginsDir,new File(/..\mochaui\\build\\plugins/).getCanonicalPath(),'js',true,true,[])
  copyResources(themesDir,new File(/..\mochaui\\build\\themes/).getCanonicalPath(),'css',true,true,[])
  removeOldMooTools(buildJS)
  copyResources(mooTools.getCanonicalPath(),buildJS,'js',false,true,[])


  //------------------------------------------------------
  // build script libraries
  licenseFile = new File(/..\mochaui\MIT-LICENSE.txt/).getCanonicalPath()
  authorsFile = new File(/..\mochaui\Authors.txt/).getCanonicalPath()

  dest=new File(/..\mochaui\\demo\scripts\mocha.js/)
  dir1=new File(/..\mochaui\src\mochaui\js/).getCanonicalPath()+s
  jsfiles = /Core\Core.js/

  // clear the demo mocha.js
  println ' [clearing]  ' + dest.getCanonicalPath()
  if(dest.exists()) dest.delete()
  dest.createNewFile()

  // append the license file to the mocha.js, add js comments to keep from being removed by compressor
  dest << "/*!\n" + (new File(licenseFile).text) + "\n*/\n"

  // make sure license files are in the same folder as mocha.js in demo
  ant.copy(file:"${licenseFile}", tofile:/..\mochaui\\demo\scripts\MIT-LICENSE.txt/, overwrite: true)
  ant.copy(file:"${authorsFile}", tofile:/..\mochaui\\demo\scripts\AUTHORS.txt/, overwrite: true)

  // make sure license files are in the same folder as mocha.js in build
  ant.copy(file:"${licenseFile}", tofile:/..\mochaui\\build\\MIT-LICENSE.txt/, overwrite: true)
  ant.copy(file:"${authorsFile}", tofile:/..\mochaui\\build\AUTHORS.txt/, overwrite: true)

  // create the demo mocha.js that is not compressed
  [
    'Core/Core.js',
    'Utilities/Themes.js',
    'Window/Window.js',
    'Window/Modal.js',
    'Window/Windows-from-html.js',
    'Window/Windows-from-json.js',
    'Window/Arrange-cascade.js',
    'Window/Arrange-tile.js',
    'Components/Tabs.js',
    'Layout/Layout.js',
    'Layout/Dock.js',
    'Layout/Workspaces.js'
  ].each {
    println '[appending] ' + it
    src=new File(dir1+it)
    dest << src.text
  }

  // we need to compress so fire off a command to execute the yuicompressor
  toJS = new File(/..\mochaui\\build\mocha.js/).getCanonicalPath()
  fromJS = dest.getCanonicalPath()
  println ' [compress] ' +fromJS+' to '+toJS
  command = "java -jar "+new File('.').getCanonicalPath()+s+"lib"+s+"yuicompressor-2.4.2.jar -o ${toJS} ${fromJS}"
  command.execute().waitFor()     // Call *execute* on the string  
}

setDefaultTarget(main)
