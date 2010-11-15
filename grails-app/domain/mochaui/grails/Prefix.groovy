package mochaui.grails

class Prefix {

	String prefix
	boolean isActive

	static mapping = {
		id generator:'assigned', name:'prefix', type:'string'
	}

    static constraints = {
    }
}