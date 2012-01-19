package net.xelnaga.httpimposter

import spock.lang.Specification

class ImposterStoreSpec extends Specification {

    private ImposterStore store
    
    void setup() {
        store = new ImposterStore()
    }

    def 'get with stored response'() {

        given:
            ImposterRequest request = new ImposterRequest(body: 'hello')
            ImposterResponse response = new ImposterResponse(body: 'world')

        when:
            store.put(request, response)

        then:
            store.get(request) == response
    }

    def 'get without stored response'() {
    
        given:
            ImposterRequest request = new ImposterRequest(body: 'hello')
        
        expect:
            store.get(request) == null
    }

    def 'clear'() {

        given:
            ImposterRequest request = new ImposterRequest(body: 'hello')
            ImposterResponse response = new ImposterResponse(body: 'world')
            store.put(request, response)

        when:
            store.clear()

        then:
            store.get(request) == null
    }
}
