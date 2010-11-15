package mochaui.grails

class Country {

	String code
	String country
	boolean isActive

	static mapping = {
		id generator:'assigned', name:'code', type:'string'
	}

    static constraints = {
    }
}

