import com.polaropposite.mochauigrails.BuildMochaUI

includeTargets << grailsScript("Init")

target(main: "Assemble mochaui.js") {
  new BuildMochaUI()
}

setDefaultTarget(main)
