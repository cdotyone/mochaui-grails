package mochaui.grails

import grails.converters.JSON

class MultipleController {

	def index = {
		redirect action: list
	}

	def list = {
		def all = []

		// build results
		def suffixes = []
		Suffix.list()?.each{ suffix ->
			def result = [suffix:suffix.suffix, isActive:suffix.isActive]
			suffixes << result
		}
		all << [
			name:"Suffix",
			page:1,
			pageSize:Suffix.count(),
			pageMax:1,
			total:Suffix.count(),
			first:1,
			last:Suffix.count(),
			data:suffixes
		]

		// build results
		def prefixes = []
		Prefix.list()?.each{ prefix ->
			def result = [prefix:prefix.prefix, isActive:prefix.isActive]
			prefixes << result
		}
		all << [
			name:"Prefix",
			page:1,
			pageSize:Prefix.count(),
			pageMax:1,
			total:Prefix.count(),
			first:1,
			last:Prefix.count(),
			data:prefixes
		]

		def countries = []
		Country.list()?.each{ country ->
			def result = [code:country.code, state:country.country, isActive:country.isActive]
			countries << result
		}
		all << [
			name:"Country",
			page:1,
			pageSize:Country.count(),
			pageMax:1,
			total:Country.count(),
			first:1,
			last:Country.count(),
			data:countries
		]

		def states = []
		State.list()?.each{ state ->
			def result = [countryCode:state.countryCode, code:state.code, state:state.state, isActive:state.isActive]
			states << result
		}
		all << [
			name:"State",
			page:1,
			pageSize:State.count(),
			pageMax:1,
			total:State.count(),
			first:1,
			last:State.count(),
			data:states
		]

		render all as JSON
	}

}
