package uk.ac.open.kmi.basil.rest.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wordnik.swagger.annotations.*;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.secnod.shiro.jaxrs.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.open.kmi.basil.doc.Doc.Field;
import uk.ac.open.kmi.basil.view.Engine;
import uk.ac.open.kmi.basil.view.View;
import uk.ac.open.kmi.basil.view.Views;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

@Path("{id}/view")
@Api(value = "/basil", description = "BASIL operations")
public class ViewResource extends AbstractResource {
	private Logger log = LoggerFactory.getLogger(ViewResource.class);

	@PUT
	@Path("{name}")
	@Produces("application/json")
	@ApiOperation(value = "Create a new API view",
            notes = "The operation returns the resource URI of the API view")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Body cannot be empty"),
            @ApiResponse(code = 201, message = "View created"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal error") })
	public Response put(
            @ApiParam(value = "ID of the API specification", required = true)
            @PathParam("id") String id,
            @ApiParam(value = "Media type of the view output (e.g., text/html)", required = true)
			@QueryParam("type") @DefaultValue("text/html") String type,
            @ApiParam(value = "Name of the view", required = true)
            @PathParam("name") String name,
            @ApiParam(value = "Media type of the view", required = true)
            @HeaderParam("Content-Type") String contentType,
            @ApiParam(value = "Template of the view", required = true)
			String body,
			@Auth Subject subject) {
		log.trace("Calling PUT view with id: {} name: ", id, name);
		try {
			subject.checkRole(id);
			Engine engine;
			// Content-type
			if (requestHeaders.getMediaType() == null) {
				engine = Engine.MUSTACHE;
			} else {
				String mediaType = requestHeaders.getMediaType().getType() + "/" + requestHeaders.getMediaType().getSubtype();
				log.debug("Content-Type is {}", mediaType);
				engine = Engine.byContentType(mediaType);
				if (engine == null) {
					JsonObject m = new JsonObject();
					m.add("message", new JsonPrimitive("Unsupported media type: " + mediaType));
					m.add("unsupportedMediaType", new JsonPrimitive(mediaType));
					return Response.serverError()
							.entity(m.toString())
							.build();
				}
			}

			Views views = getApiManager().listViews(id);
			boolean created = true;
			if (views.exists(name)) {
				created = false;
			}
			getApiManager().createView(id, type, name, body, engine);
			ResponseBuilder r;
			if (created) {
				log.debug("View created.");
				 r = Response.created(
						requestUri.getBaseUriBuilder().path(id).path(name).build());
			} else {
				r = Response.ok();
			}
			addHeaders(r, id);
			return r
					.build();
		} catch (AuthorizationException e) {
			log.trace("Not authorized");
			return packError(Response.status(Status.FORBIDDEN), e).build();
		} catch (IOException e) {
			log.error("",e);
			return packError(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
					, e).build();
		} 
	}

	@GET
	@Produces("application/json")
	@ApiOperation(value = "Get the list of available views of an API",response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal error") }
    )
    public Response listViews(
            @ApiParam(value = "ID of the API specification", required = true)
            @PathParam("id") String id) {
		try {
			Views views = getApiManager().listViews(id);
			ResponseBuilder r;
			if (views.numberOf() == 0) {
				r = Response.noContent();
			}else{
				JsonArray arr = new JsonArray();
				for(String n : views.getNames()){
					View v = views.byName(n);
					JsonObject o = new JsonObject();
					o.add("id", new JsonPrimitive(n));
					o.add("extension", new JsonPrimitive(n));
					o.add("content-type", new JsonPrimitive(v.getEngine().getContentType()));
					o.add("type", new JsonPrimitive(v.getMimeType()));
					o.add("template", new JsonPrimitive(v.getTemplate()));
					arr.add(o);
				}
				r = Response.ok(arr.toString());
			}
			addHeaders(r, id);
			r.header(Headers.Name, getApiManager().getDoc(id).get(Field.NAME));
			return r.build();
		} catch (Exception e) {
			log.error("", e);
			return packError(Response.serverError(), e).build();
		}
	}

	@GET
	@Path("{name}")
	@ApiOperation(value = "See an API view")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "API view not found"),
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal error") })
    public Response spec(
            @ApiParam(value = "ID of the API specification", required = true)
            @PathParam("id") String id,
            @ApiParam(value = "Name of the view", required = true)
			@PathParam("name") String name) {
		log.trace("Calling GET view with id: {} and name: {}", id, name);
		try {
			View view = getApiManager().getView(id, name);
			if (view == null) {
				return packError(Response.status(404),"Not found").build();
			}
			ResponseBuilder builder = Response.ok(view.getTemplate());
			addHeaders(builder, id);
			builder.header("Content-type", view.getEngine().getContentType());
			builder.header(Headers.Type, view.getMimeType());
			return builder.build();
		} catch (Exception e) {
			return packError(Response.serverError(),e).build();
		}
	}

	@DELETE
	@Path("{name}")
	@Produces("application/json")
	@ApiOperation(value = "Delete API view")
    @ApiResponses(value = { @ApiResponse(code = 404, message = "API view not found"),
            @ApiResponse(code = 200, message = "API view deleted"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal error") })
	public Response delete(
            @ApiParam(value = "ID of the API specification", required = true)
            @PathParam("id") String id,
            @ApiParam(value = "Name of the view", required = true)
			@PathParam("name") String name,
			@Auth Subject subject) {
		try {
			log.trace("Deleting view id: {} name: {}", id, name);
			subject.checkRole(id);
			getApiManager().deleteView(id, name);
			log.debug("View deleted: {}:{} ", id, name);
			URI view = requestUri.getBaseUriBuilder().path(id).path("view").path("name").build();
			JsonObject m = new JsonObject();
			m.add("message", new JsonPrimitive("View deleted: " + view.toString()));
			m.add("location", new JsonPrimitive(view.toString()));
			ResponseBuilder r = Response.ok().entity(m.toString());
			addHeaders(r, id);
			return r
					.build();
		} catch (AuthorizationException e) {
			log.trace("Not authorized");
			return packError(Response.status(Status.FORBIDDEN),e).build();
		} catch (IOException e) {
			log.error("", e);
			return packError(Response.serverError(), e).build();
		}
	}
}
