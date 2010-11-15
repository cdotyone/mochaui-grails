package mochaui.grails

import grails.converters.JSON

class PrefixController {
	def index = {
		redirect action: list
	}

	def list = {
		// determine the page size
		if(!params.max) params.max = 10
		def pageSize = params.max = params.max.toString().toInteger()

		// translate the page # to a offset
		if(params.page) params.offset = (params.page.toString().toInteger()-1)*pageSize

		// if there is no offset start at record 1
		if(!params.offset) params.offset = 0

		def offset = params.offset.toString().toInteger()
		def total = Prefix.count()
		def page = (offset / pageSize) + 1
		def pageMax = (total / pageSize).toString().split("[.]")[0].toInteger()
		if (pageMax * pageSize < total) pageMax++

		// build results
		def results = []
		Prefix.list(params)?.each{ prefix ->
			def result = [prefix:prefix.prefix, isActive:prefix.isActive]
			results << result
		}

		// calculate last record, ensure it is not past the last record
		def last = offset+1+pageSize
		if(last>total) last = total

		def json = [
			name:"Prefix",
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
