package mochaui.grails

class Suffix {

	String suffix
	boolean isActive

	static mapping = {
		id generator:'assigned', name:'suffix', type:'string'
	}

    static constraints = {
    }
}