package mochaui.grails

class State {

	String countryCode
	String code
	String state
	boolean isActive

	static mapping = {
		id generator:'assigned', name:'code', type:'string'
	}

    static constraints = {
    }
}
