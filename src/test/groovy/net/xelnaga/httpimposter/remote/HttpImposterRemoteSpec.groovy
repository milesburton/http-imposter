package net.xelnaga.httpimposter.remote

import net.xelnaga.httpimposter.model.ImposterRequest
import net.xelnaga.httpimposter.model.ImposterResponse
import org.apache.http.HttpStatus
import spock.lang.Specification
import com.popcornteam.restclient.RestClient
import com.popcornteam.restclient.request.StringBody
import com.popcornteam.restclient.response.StubRestResponse

class HttpImposterRemoteSpec extends Specification {

    HttpImposterRemote remote
    
    RestClient mockRestClient

    void setup() {

        remote = new HttpImposterRemote('http://www.example.com/jacksparrow')

        mockRestClient = Mock(RestClient)
        remote.restClient = mockRestClient
    }

    def "configure"() {

        given:
            ImposterRequest request = new ImposterRequest(uri: 'jack', method: 'sparrow', headers: [], body: 'requestBody')
            ImposterResponse response = new ImposterResponse(status: 123, headers: [], body: 'responseBody')

            String json = '{"request":{"uri":"jack","method":"sparrow","headers":[],"body":"cmVxdWVzdEJvZHk\\u003d"},"response":{"status":123,"headers":[],"body":"cmVzcG9uc2VCb2R5"}}'
        
        when:
            remote.configure(request, response)

        then:
            1 * mockRestClient.post('/configure', new StringBody(json)) >> new StubRestResponse(HttpStatus.SC_OK, 'OK')
            0 * _._
    }

    def "configure with non ok"() {

        given:
            ImposterRequest request = new ImposterRequest(uri: 'jack', method: 'sparrow', headers: [], body: 'requestBody')
            ImposterResponse response = new ImposterResponse(status: 123, headers: [], body: 'responseBody')

            String json = '{"request":{"uri":"jack","method":"sparrow","headers":[],"body":"cmVxdWVzdEJvZHk\\u003d"},"response":{"status":123,"headers":[],"body":"cmVzcG9uc2VCb2R5"}}'

        when:
            remote.configure(request, response)

        then:
            1 * mockRestClient.post('/configure', new StringBody(json)) >> new StubRestResponse(HttpStatus.SC_NOT_FOUND, 'OK')
            0 * _._
        
        and:
            Exception ex = thrown(HttpImposterRemoteException)
            ex.message == 'Failed to configure http imposter'
    }

    def 'reset'() {

        when:
            remote.reset()
        
        then:
            1 * mockRestClient.post('/reset', new StringBody('')) >> new StubRestResponse(HttpStatus.SC_OK, 'OK')
            0 * _._
    }

    def 'reset with non ok'() {

        when:
            remote.reset()

        then:
            1 * mockRestClient.post('/reset', new StringBody('')) >> new StubRestResponse(HttpStatus.SC_NOT_FOUND, 'OK')
            0 * _._

        and:
            Exception ex = thrown(HttpImposterRemoteException)
            ex.message == 'Failed to reset http imposter'
    }
}
