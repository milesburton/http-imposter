package com.mb.httpimposter

import net.xelnaga.httpimposter.HttpImposter
import net.xelnaga.httpimposter.filter.HeaderNameExclusionFilter
import net.xelnaga.httpimposter.filter.HttpHeaderFilter
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.SlingHttpServletResponse
import org.apache.sling.api.servlets.SlingSafeMethodsServlet

/**
 * @scr.component immediate="true" enabled="true"
 * @scr.service interface="javax.servlet.Servlet"
 * @scr.property name="service.description" value="HTTP Imposter for Apache Felix"
 * @scr.property name="service.vendor" value="milesburton.com"
 * @scr.property name="sling.servlet.paths" value="/bin/mb/imposter"
 */
class ImposterServlet extends SlingSafeMethodsServlet {

    private static final HttpHeaderFilter FILTER = new HeaderNameExclusionFilter(['Host', 'User-Agent', 'Connection', 'Content-Length'])
    private static final HttpImposter IMPOSTER = new HttpImposter(filter: FILTER)

    void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        process(request,response)
    }

    void handleMethodNotImplemented(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        process(request,response)
    }

    void process(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        if (request.queryString.endsWith("/configure")) {
            IMPOSTER.configure(request)
        } else if (request.queryString.endsWith("/reset")) {
            IMPOSTER.reset()
        }else if (request.queryString.endsWith("/test")){
            response.writer.println("Ready")
        } else {
            IMPOSTER.respond("${request.requestURI}?${request.queryString}", request, response)
        }
    }
    =

}