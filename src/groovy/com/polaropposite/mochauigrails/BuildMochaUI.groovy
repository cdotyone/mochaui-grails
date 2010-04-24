package com.polaropposite.mochauigrails

class BuildMochaUI {
def ant = new AntBuilder()
def mootoolsCore = "mootools-1.2.2-core-yc.js"
def mootoolsCoreMod = null
def mootoolsMore = "mootools-1.2.2-more-yc.js"
def mootoolsMoreMod = null
def mootoolsScriptsMod = null
def mootoolsScripts =
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
  ]

def removeOldMooTools = { from ->
  new File(from).eachFileRecurse() { file->
    def jsname = file.getName()
    if(!file.isDirectory() && jsname.indexOf('mootools-')>=0 ) {
      if(jsname!=mootoolsCore && jsname!=mootoolsMore) {
        ant.delete(file:"${file.getAbsolutePath()}")
      }
    }
  }
}

def copyHTML = { from,to ->
  println '     [copy] Copying 1 file to ' + to
  def src = new File(from).text
  def tag = '<!--MOCHAUI-->'
  if(src.indexOf(tag)>-1) {
    def firstPos = src.indexOf(tag)
    def lastPos = src.indexOf(tag,firstPos+1)+tag.length()

    if(firstPos<lastPos) {
      def part1 = src.substring(0,firstPos)
      def part2 = src.substring(lastPos)
      src = part1
      def indent = part1.substring(part1.length()-part1.reverse().indexOf('\n'))
      src+='<script type="text/javascript" src="scripts/'+mootoolsCore+'"></script>\n'
      src+=indent+'<script type="text/javascript" src="scripts/'+mootoolsMore+'"></script>\n'
      src+=indent+'<script type="text/javascript" src="scripts/mocha.js"></script>'
      src+=part2
    }
  }
  new File(to) << src
  new File(to).setLastModified(new File(from).lastModified())
}

def copyFile = { from,to ->
  def src=new File(from)
  def dest=new File(to)
  if(!dest.exists() || src.lastModified()>dest.lastModified()) {
    ant.copy(file:"${src.getCanonicalFile()}", tofile:"${dest.getCanonicalFile()}", overwrite: true, preservelastmodified:true)
  }
}

def copyMooTools = { from,to ->
  def s=File.separatorChar
  copyFile(from+s+mootoolsCore,to+s+mootoolsCore)
  copyFile(from+s+mootoolsMore,to+s+mootoolsMore)
}

def copyResources = { from,to,fileType,clear,compress,exclude ->
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

        // does the file exist, and is the directory not on the exclude list
        if(!file2.exists() && !exclude.find {it -> srcfile.indexOf(from+s+it)>=0 }.any()) {
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
      def toPath = file2.getCanonicalPath().replace(file2.getName(),'')
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
            copyHTML(fromFile,tofile)
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

BuildMochaUI() {
  def s = File.separatorChar

  // resource directories
  def mooTools = new File(/..\mochaui\\src\\scripts/)
  def pluginsDir = new File(/..\mochaui\\src\\plugins/).getCanonicalPath()
  def themesDir = new File(/..\mochaui\\src\\themes/).getCanonicalPath()
  def demoDir = new File(/..\mochaui\\src\\demo/).getCanonicalPath()

  // detect mootool filenames, so replace in demo section can happen
  mooTools.eachFile { file->
    def jsname = file.getName()
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
  def demoJS = new File(/..\mochaui\\demo\\scripts/).getCanonicalPath()
  def buildJS = new File(/..\mochaui\\build/).getCanonicalPath()

  //------------------------------------------------------
  // now copy themes and plugins to demo
  copyResources(demoDir,new File(/..\mochaui\\demo/).getCanonicalPath(),'js',true,false,[/plugins/,/themes/,/scripts/])
  copyResources(pluginsDir,new File(/..\mochaui\\demo\\plugins/).getCanonicalPath(),'js',true,false,[])
  copyResources(themesDir,new File(/..\mochaui\\demo\\themes/).getCanonicalPath(),'js',true,false,[])
  removeOldMooTools(demoJS)
  copyMooTools(mooTools.getCanonicalPath(),new File(/..\mochaui\\demo\\scripts/).getCanonicalPath())

  //------------------------------------------------------
  // now copy themes and plugins to the build folder
  copyResources(pluginsDir,new File(/..\mochaui\\build\\plugins/).getCanonicalPath(),'js',true,true,[])
  copyResources(themesDir,new File(/..\mochaui\\build\\themes/).getCanonicalPath(),'css',true,true,[])
  removeOldMooTools(buildJS)
  copyMooTools(mooTools.getCanonicalPath(),new File(/..\mochaui\\build/).getCanonicalPath())


  //------------------------------------------------------
  // build script libraries
  def licenseFile = new File(/..\mochaui\MIT-LICENSE.txt/).getCanonicalPath()
  def authorsFile = new File(/..\mochaui\Authors.txt/).getCanonicalPath()

  // make sure license files are in the same folder as mocha.js in demo
  copyFile(licenseFile,/..\mochaui\\demo\scripts\MIT-LICENSE.txt/)
  copyFile(authorsFile,/..\mochaui\\demo\scripts\AUTHORS.txt/)

  // make sure license files are in the same folder as mocha.js in build
  copyFile(licenseFile,/..\mochaui\\build\MIT-LICENSE.txt/)
  copyFile(authorsFile,/..\mochaui\\build\AUTHORS.txt/)


  //----------------------------------------------------------
  // create the demo mocha.js that is not compressed
  def dest=new File(/..\mochaui\demo\scripts\mocha.js/)
  def dir1=new File(/..\mochaui\src\scripts/).getCanonicalPath()+s

  // first see if any of the files have changed
  mootoolsScripts.each {
    def src=new File(dir1+it)
    if(mootoolsScriptsMod==null || src.lastModified()>mootoolsScriptsMod)
      mootoolsScriptsMod = src.lastModified()
  }

  if(!dest.exists() || dest.lastModified()<mootoolsScriptsMod ) {
    // clear the demo mocha.js
    println ' [clearing]  ' + dest.getCanonicalPath()
    if(dest.exists()) dest.delete()
    dest.createNewFile()

    // append the license file to the mocha.js, add js comments to keep from being removed by compressor
    dest << "/*!\n" + (new File(licenseFile).text) + "\n*/\n"

    // create the demo mocha.js that is not compressed
    mootoolsScripts.each {
      println '[appending] ' + it
      def src=new File(dir1+it)
      dest << src.text
    }

    dest.setLastModified(mootoolsScriptsMod)
  }

  // we need to compress so fire off a command to execute the yuicompressor
  def toJSFile = new File(/..\mochaui\\build\mocha.js/)
  if(toJSFile.lastModified() < dest.lastModified()) {
    def toJS = toJSFile.getCanonicalPath()
    def fromJS = dest.getCanonicalPath()
    println ' [compress] ' +fromJS+' to '+toJS
    def command = "java -jar "+new File('.').getCanonicalPath()+s+"lib"+s+"yuicompressor-2.4.2.jar -o ${toJS} ${fromJS}"
    command.execute().waitFor()     // Call *execute* on the string

    toJSFile.setLastModified(dest.lastModified())
  }
}
  
}
