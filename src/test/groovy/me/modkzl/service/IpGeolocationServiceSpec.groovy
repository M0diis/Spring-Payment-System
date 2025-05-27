package me.modkzl.service

import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject

class IpGeolocationServiceSpec extends Specification {

    def static final API_URL = "http://ip-api.com/json/{ip}"

    def environment = Mock(Environment)
    def restTemplate = Mock(RestTemplate)

    @Subject
    def ipGeolocationService = new IpGeolocationService(environment, restTemplate)

    def setup() {
        environment.getActiveProfiles() >> ["test"]
    }

    def "should return country when geolocation API responds successfully"() {
        given:
        def clientIp = "8.8.8.8"
        def apiUrl = API_URL.replace("{ip}", clientIp)
        def response = new IpGeolocationService.GeolocationResponse("success", "United States")

        when:
        def result = ipGeolocationService.getClientCountry(clientIp)

        then:
        1 * restTemplate.getForObject(apiUrl, IpGeolocationService.GeolocationResponse) >> response
        result == "United States"
    }

    def "should return null when geolocation API responds with failure"() {
        given:
        def clientIp = "8.8.8.8"
        def apiUrl = API_URL.replace("{ip}", clientIp)
        def response = new IpGeolocationService.GeolocationResponse("fail", null)

        when:
        def result = ipGeolocationService.getClientCountry(clientIp)

        then:
        1 * restTemplate.getForObject(apiUrl, IpGeolocationService.GeolocationResponse) >> response
        result == null
    }

    def "should return null when client IP is null or empty"() {
        when:
        def result = ipGeolocationService.getClientCountry(null)

        then:
        result == null
    }

    def "should log error and return null when geolocation API throws exception"() {
        given:
        def clientIp = "8.8.8.8"
        def apiUrl = API_URL.replace("{ip}", clientIp)

        when:
        def result = ipGeolocationService.getClientCountry(clientIp)

        then:
        1 * restTemplate.getForObject(apiUrl, IpGeolocationService.GeolocationResponse) >> {
            throw new RuntimeException("API error")
        }
        result == null
    }
}