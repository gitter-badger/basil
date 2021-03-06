package uk.ac.open.kmi.basil.rest.core;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.jena.atlas.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.open.kmi.basil.core.ApiManager;
import uk.ac.open.kmi.basil.core.ApiManagerImpl;
import uk.ac.open.kmi.basil.core.auth.UserManager;
import uk.ac.open.kmi.basil.rest.BasilApplication;
import uk.ac.open.kmi.basil.search.SearchProvider;
import uk.ac.open.kmi.basil.store.Store;

public class AbstractResource {

	private static Logger log = LoggerFactory.getLogger(AbstractResource.class);
	
	@Context
	protected HttpHeaders requestHeaders;

	@Context
	protected UriInfo requestUri;

	@Context
	protected ServletContext context;
	private ApiManager apiManager;
	protected ApiManager getApiManager() {
		if (apiManager == null) {
			apiManager = new ApiManagerImpl(getDataStore(), getUserManager());
		}
		return apiManager;
	}
	
	protected SearchProvider getSearchProvider() {
		return (SearchProvider) context.getAttribute(BasilApplication.Registry.SearchProvider);
	}

	protected final ResponseBuilder packError(ResponseBuilder builder, String message){
		JsonObject j = new JsonObject();
		j.put("error", message);
		return builder.header(Headers.Error, message).entity(j.toString());
	}
	
	protected final ResponseBuilder packError(ResponseBuilder builder, Exception e){
		return packError(builder, e.getMessage());
	}
	
	protected final ResponseBuilder addHeaders(ResponseBuilder builder,
			String id) {
		URI api = requestUri.getBaseUriBuilder().path(id).path("api").build();
		URI direct = requestUri.getBaseUriBuilder().path(id).path("direct").build();
		URI spec = requestUri.getBaseUriBuilder().path(id).path("spec").build();
		URI views = requestUri.getBaseUriBuilder().path(id).path("view")
				.build();
		URI swagger = requestUri.getBaseUriBuilder().path(id).path("api-docs")
				.build();
		URI docs = requestUri.getBaseUriBuilder().path(id).path("docs")
				.build();
		builder.header(Headers.Api, api);
		builder.header(Headers.Spec, spec);
		builder.header(Headers.Direct, direct);
		builder.header(Headers.View, views);
		builder.header(Headers.Docs, docs);
		builder.header(Headers.Swagger, swagger);
		try {
			builder.header(Headers.Creator, getApiManager().getCreatorOfApi(id));
		} catch (IOException e) {
			log.error("",e);
		}
		return builder;
	}

	protected final String getParameterOrHeader(String parameter) {
		String value = requestUri.getQueryParameters().getFirst(parameter);
		if (value == null) {
			if (requestHeaders.getHeaderString(Headers.getHeader(parameter)) == null) {
				throw new WebApplicationException(Response
						.status(HttpURLConnection.HTTP_BAD_REQUEST)
						.header(Headers.Error,
								Headers.getHeader(parameter)
										+ " (or query parameter '" + parameter
										+ "') missing.").build());
			} else {
				value = requestHeaders.getHeaderString(Headers
						.getHeader(parameter));
			}
		}
		return value;
	}

	private Store getDataStore() {
		return (Store) context.getAttribute(BasilApplication.Registry.Store);
	}
	
	protected UserManager getUserManager() {
		return (UserManager) context.getAttribute(BasilApplication.Registry.UserManager);
	}
}
