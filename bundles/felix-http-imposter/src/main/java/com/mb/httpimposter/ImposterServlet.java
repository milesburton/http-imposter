package com.mb.httpimposter;

import net.xelnaga.httpimposter.HttpImposter;
import net.xelnaga.httpimposter.filter.HeaderNameExclusionFilter;
import net.xelnaga.httpimposter.filter.HttpHeaderFilter;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;


@Component(immediate = true)
@Service(value = javax.servlet.Servlet.class)
@Properties({
        @Property(name = "sling.servlet.methods", value = "GET"),
        @Property(name = "sling.servlet.resourceTypes", value = {"sling/servlet/default"}),
        @Property(name = "mapping", value = "/imposter/")
}
)
public class ImposterServlet extends SlingAllMethodsServlet implements OptingServlet {

    private static final Logger log = LoggerFactory.getLogger(ImposterServlet.class);
    private static final HttpHeaderFilter FILTER;
    private static final HttpImposter IMPOSTER;

    static {

        String[] exl = {"Host", "User-Agent", "Connection", "Content-Length"};

        FILTER = new HeaderNameExclusionFilter(Arrays.asList(exl));
        IMPOSTER = new HttpImposter();
        IMPOSTER.setFilter(FILTER);
    }


    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response);
    }

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        process(request, response);
    }

    void process(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        String qs = request.getQueryString() != null ? request.getQueryString() : "";
        String uri = request.getRequestURI() + "?" + qs;

        log.info("Http Imposter responding to: " + uri);

        if (uri.endsWith("/configure")) {
            IMPOSTER.configure(request);
        } else if (uri.endsWith("/reset")) {
            IMPOSTER.reset();
        } else if (uri.endsWith("/test")) {
            response.getWriter().println("Ready");
        } else {
            IMPOSTER.respond(uri, request, response);
        }
    }

    public boolean accepts(SlingHttpServletRequest request) {
        return request.getPathInfo().startsWith("/imposter/");
    }
}