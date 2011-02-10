package mochaui.grails

import grails.converters.JSON

class PersonController{

	def personService

/*
	static Map allowedMethods = [
			  validname: 'POST'
			, save: 'POST'
			, update: 'POST'
			, delete: 'POST'
			, addmember: 'POST'
	]
*/

	def index = {
		redirect action: list
	}

	def list = {

		// determine the page size
		if(!params.max) params.max = 10
		def pageSize = params.max.toString().toInteger()
		if(pageSize<=0) pageSize=10
		params.max=pageSize

		// translate the page # to a offset
		if(params.page) params.offset = (params.page.toString().toInteger()-1)*pageSize

		// if there is no offset start at record 1
		if(!params.offset || params.offset<0) params.offset = 0
		if(!params.sort || params.sort=='') params.sort = 'name'
		if(!params.order || params.order=='') params.order = 'asc'

		def offset = params.offset.toString().toInteger()
		def total = Person.count()
		def page = (offset / pageSize) + 1
		def pageMax = (total / pageSize).toString().split("[.]")[0].toInteger()
		if (pageMax * pageSize < total) pageMax++

		// build results
		def results = []
		Person.list(params)?.each{ person ->
			def result = [id:person.id, name:person.name, firstName:person.firstName, lastName:person.lastName, address: person.address, city: person.city, state: person.state, zip: person.zip, gender:person.gender]
			results << result
		}

		// calculate last record, ensure it is not past the last record
		def last = offset+pageSize
		if(last>total) last = total

		def json = [
			page:page,
			pageSize:pageSize,
			pageMax:pageMax,
			total:total,
			first:offset+1,
			last:last,
			data:results
		]
		render json as JSON
	}
}
