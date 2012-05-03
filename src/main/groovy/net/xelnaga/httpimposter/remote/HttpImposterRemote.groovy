package net.xelnaga.httpimposter.remote

import com.google.gson.Gson

import net.xelnaga.httpimposter.model.ImposterRequest
import net.xelnaga.httpimposter.model.ImposterResponse
import org.apache.commons.codec.binary.Base64
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import com.popcornteam.restclient.RestClient
import com.popcornteam.restclient.factory.HttpClientFactory
import com.popcornteam.restclient.response.RestResponse
import com.popcornteam.restclient.request.StringBody

class HttpImposterRemote {

    private Gson gson = new Gson()

    RestClient restClient
    
    HttpImposterRemote(String baseUrl) {

        HttpClient httpClient = new HttpClientFactory().makeThreadSafeHttpClient(2)
        restClient = new RestClient(baseUrl, [], httpClient)
    }
    
    void configure(ImposterRequest request, ImposterResponse response) {

        Map payload = buildConfigurationPayload(request, response)
        String json = gson.toJson(payload)
        
        RestResponse restResponse = restClient.post("/configure", new StringBody(json))

        if (restResponse.status != HttpStatus.SC_OK) {
            throw new HttpImposterRemoteException('Failed to configure http imposter')
        }
    }

    void reset() {

        RestResponse restResponse = restClient.post("/reset", new StringBody(''))
        if (restResponse.status != HttpStatus.SC_OK) {
            throw new HttpImposterRemoteException('Failed to reset http imposter')
        }
    }

    private Map buildConfigurationPayload(ImposterRequest request, ImposterResponse response) {

        Map payload = [
                request: [
                        uri: request.uri,
                        method: request.method,
                        headers: request.headers,
                        body: new String(Base64.encodeBase64(request.body.bytes))
                ],
                response: [
                        status: response.status,
                        headers: response.headers,
                        body: new String(Base64.encodeBase64(response.body.bytes))
                ]
        ]

        return payload
    }
}
