import com.polaropposite.mochaui.build.BuildMochaUI

includeTargets << grailsScript("Init")

target(main: "Assemble mochaui.js") {
  def path = (new File("../mochaui/")).getCanonicalPath()
  BuildMochaUI.Rebuild(path,true)
}

setDefaultTarget(main)
