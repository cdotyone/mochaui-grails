import mochaui.grails.Person
import org.codehaus.groovy.grails.commons.ApplicationHolder
import grails.converters.JSON
import mochaui.grails.Suffix
import mochaui.grails.Prefix
import mochaui.grails.Country
import mochaui.grails.State

class BootStrap{

	def init = { servletContext ->
		if (!Person.count()){

			def filePaths = ['resources/person1.json', 'resources/person2.json', 'resources/person3.json', 'resources/person4.json', 'resources/person5.json']
			filePaths.each { filePath ->
				String content = ApplicationHolder.application.parentContext.getResource("classpath:$filePath").inputStream.newReader('UTF-8').text
				def json = JSON.parse(content).data

				json.each { p ->
					new Person(
							id: p.id.toInteger(),
							firstName: p.firstName,
							lastName: p.lastName,
							address: p.address,
							city: p.city,
							state: p.state,
							zip: p.zip,
							gender: p.gender,
							name: p.firstName + ' ' + p.lastName
					).save(failOnError: false);
				}
			}
		}

		if (!Suffix.count()){
			def filePath = 'resources/suffix.json'
			String content = ApplicationHolder.application.parentContext.getResource("classpath:$filePath").inputStream.newReader('UTF-8').text
			def json = JSON.parse(content).data

			json.each { p ->
				new Suffix(
						suffix: p.suffix,
						isActive: p.isActive.toString().toBoolean()
				).save(failOnError: false);
			}
		}

		if (!Prefix.count()){
			def filePath = 'resources/prefix.json'
			String content = ApplicationHolder.application.parentContext.getResource("classpath:$filePath").inputStream.newReader('UTF-8').text
			def json = JSON.parse(content).data

			json.each { p ->
				new Prefix(
						prefix: p.prefix,
						isActive: p.isActive.toString().toBoolean()
				).save(failOnError: false);
			}
		}

		if (!Country.count()){
			def filePath = 'resources/country.json'
			String content = ApplicationHolder.application.parentContext.getResource("classpath:$filePath").inputStream.newReader('UTF-8').text
			def json = JSON.parse(content).data

			json.each { p ->
				new Country(
						code: p.code,
						country: p.country,
						isActive: p.isActive.toString().toBoolean()
				).save(failOnError: false);
			}
		}

		if (!State.count()){
			def filePath = 'resources/state.json'
			String content = ApplicationHolder.application.parentContext.getResource("classpath:$filePath").inputStream.newReader('UTF-8').text
			def json = JSON.parse(content).data

			json.each { p ->
				new State(
						code: p.code,
						state: p.state,
						countryCode: p.countryCode,
						isActive: p.isActive.toString().toBoolean()
				).save(failOnError: false);
			}
		}
	}
	def destroy = {
	}
}