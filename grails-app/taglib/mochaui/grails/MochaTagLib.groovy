package mochaui.grails

class MochaTagLib {
  def redirectDemoPage = {
    response.sendRedirect("/index.html")
  }
}
