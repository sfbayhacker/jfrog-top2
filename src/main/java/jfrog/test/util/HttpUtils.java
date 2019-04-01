package jfrog.test.util;

import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing HTTP get and post methods using a multi threaded
 * HTTP connection manager
 * 
 * @author arun
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    
    public static final String ACCEPT_HEADER_APPLICATION_JSON = "application/json";
    public static final int CONNECTION_TIMEOUT_MS = 5000;
    public static final int SO_TIMEOUT_MS = 10000;
    public static final Integer MAX_TOTAL_CONNECTIONS = 2000;
    public static final Integer MAX_CONNECTIONS_PER_HOST = 100;
   
    private static final HttpConnectionManager connManager 
        = new MultiThreadedHttpConnectionManager() {
            @Override
            public void releaseConnection(final HttpConnection conn) {
                if (conn == null) {
                    return;
                }
                conn.close();
                super.releaseConnection(conn);
            }
        };
        
    static {
        Protocol.registerProtocol("https",
            new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443));

        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        params.setConnectionTimeout(CONNECTION_TIMEOUT_MS);
        params.setSoTimeout(SO_TIMEOUT_MS);
        params.setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
        params.setDefaultMaxConnectionsPerHost(MAX_CONNECTIONS_PER_HOST);
        connManager.setParams(params);   
    }
            
    private static final HttpClient client = new HttpClient(connManager);

    public static HttpUtils.Response get(String url, String acceptHeader, 
            String apiKey) throws Exception {
        if (logger.isDebugEnabled()) logger.debug("URL :: " + url);

        GetMethod get = new GetMethod(url);
        //get.setDoAuthentication( true );
        get.addRequestHeader(new Header("Accept", ACCEPT_HEADER_APPLICATION_JSON));
        get.addRequestHeader(new Header("X-JFrog-Art-Api", apiKey));
        
        HttpUtils.Response resp = null;
        String content = null;
        int status = HttpStatus.SC_NO_CONTENT;
        try {
            // execute the GET
            status = client.executeMethod( get );
            if (logger.isDebugEnabled()) logger.debug("status :: " + status);
            if (status == 200) {
                logger.debug("status is OK");
                // print the status and response
                InputStream is = get.getResponseBodyAsStream();
                content = IOUtils.toString(is, "UTF-8");
                IOUtils.closeQuietly(is);
            }
        } catch(SocketTimeoutException se) {
        	if (logger.isDebugEnabled()) {
        		logger.error(String.format("Connection timed out for url [%s]: ", url), se);
        	} else {
        		logger.error(String.format("Connection timed out for url [%s]: ", url));
        	}
        	
        	status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        } catch(Exception e) {
        	if (logger.isDebugEnabled()) {
        		logger.error(String.format("Error accessing url [%s]: ", url), e);
        	} else {
        		logger.error(String.format("Error accessing url [%s]: ", url) + e.getMessage());
        	}
        	
        	status = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        } finally {
            // release any connection resources used by the method
            get.releaseConnection();
        }

    	return new HttpUtils.Response(status, content);
    }
    
    public static String postRequest(String url, String apiKey, 
            String payload) throws Exception {
        if (logger.isDebugEnabled()) logger.debug("URL :: " + url);

        PostMethod post = new PostMethod(url);
        //post.setDoAuthentication( true );
        post.addRequestHeader(new Header("Accept", ACCEPT_HEADER_APPLICATION_JSON));
        post.addRequestHeader(new Header("X-JFrog-Art-Api", apiKey));
        
        StringRequestEntity requestEntity = new StringRequestEntity(
            payload, "text/plain", "UTF-8");
        post.setRequestEntity(requestEntity);
        
        String content = null;
        
        try {
            // execute the GET
            int status = client.executeMethod( post );
            if (logger.isDebugEnabled()) logger.debug("status :: " + status);
            if (status >= 200 && status <= 299) {
                logger.debug("status is OK");
                // print the status and response
                InputStream is = post.getResponseBodyAsStream();
                content = IOUtils.toString(is, "UTF-8");
                IOUtils.closeQuietly(is);
            }
        } catch(Exception e) {
            logger.error("Error sending post", e); // Need this to see stack trace in log
        } finally {
            // release any connection resources used by the method
            post.releaseConnection();
        }
       
        return content;
    }

    public static class Response {
    	public int status;
    	public String output;
    	
    	public Response(int status, String output) {
    		this.status = status;
    		this.output = output;
    	}
    }
}
