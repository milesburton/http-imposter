package net.xelnaga.httpimposter



import com.google.gson.Gson
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import net.xelnaga.httpimposter.factory.ImposterRequestFactory
import net.xelnaga.httpimposter.factory.ImposterResponseFactory
import net.xelnaga.httpimposter.filter.HttpHeaderFilter
import net.xelnaga.httpimposter.model.HttpHeader
import net.xelnaga.httpimposter.model.ImposterRequest
import net.xelnaga.httpimposter.model.ImposterResponse
import org.apache.log4j.Logger

class HttpImposter {

    Logger log = Logger.getLogger(HttpImposter)

    static final NO_MATCH = new ImposterResponse(
            status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            headers: [
                    new HttpHeader('Content-Type', 'text/plain')
            ],
            body: 'No match found for http request'
    )

    ImposterRequestFactory requestReader = new ImposterRequestFactory()
    ResponseWriter responseWriter = new ResponseWriter()

    private Map<ImposterRequest, ImposterResponse> map

    HttpImposter() {
        map = [:]
    }

    void setFilter(HttpHeaderFilter filter) {
        requestReader = new ImposterRequestFactory(filter: filter)
    }

    void respond(String uri, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

        ImposterRequest imposterRequest = requestReader.fromHttpRequest(httpRequest, uri)
        ImposterResponse imposterResponse = map.get(imposterRequest)

        if (imposterResponse) {
            logMatchedRequest()
            logInteraction(imposterRequest, imposterResponse)
            responseWriter.write(imposterResponse, httpResponse)
        } else {
            logUnmatchedRequest()
            logInteraction(imposterRequest, NO_MATCH)
            responseWriter.write(NO_MATCH, httpResponse)
        }
    }

    void configure(HttpServletRequest httpRequest) {

        ImposterRequestFactory requestFactory = new ImposterRequestFactory()
        ImposterResponseFactory responseFactory = new ImposterResponseFactory()

        Gson gson = new Gson()
        Map json = gson.fromJson(httpRequest.inputStream.text, HashMap)
        
        ImposterRequest imposterRequest = requestFactory.fromJson(json.request)
        ImposterResponse imposterResponse = responseFactory.fromJson(json.response)

        logConfigRequest()
        logInteraction(imposterRequest,imposterResponse)

        map.put(imposterRequest, imposterResponse)
    }

    void put(ImposterRequest imposterRequest, ImposterResponse imposterResponse) {
        map[imposterRequest] = imposterResponse
    }

    ImposterResponse get(ImposterRequest imposterRequest) {
        return map[imposterRequest]
    }

    void reset() {
        map.clear()
    }


    private void logMatchedRequest(){
        log.info'\n>> [Http Imposter]: Matched Request'
    }

    private void logUnmatchedRequest(){
        log.info '\n>> [Http Imposter]: Unmatched Request'
    }

    private void logConfigRequest(){
        log.info '\n>> [Http Imposter]: Configuration Request'
    }


    private void logInteraction(ImposterRequest imposterRequest, ImposterResponse imposterResponse) {

        log.info '>> ==================================='
        log.info imposterRequest.toString()
        log.info '>>'

        log.info '\n>> [Http Imposter]: Sending Response'
        log.info '>> ==================================='
        log.info imposterResponse.toString()
        log.info '>>'
    }
}
