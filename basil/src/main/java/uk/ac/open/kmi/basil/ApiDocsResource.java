package uk.ac.open.kmi.basil;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import uk.ac.open.kmi.basil.doc.Doc;
import uk.ac.open.kmi.basil.sparql.Specification;
import uk.ac.open.kmi.basil.swagger.SwaggerJsonBuilder;
import uk.ac.open.kmi.basil.swagger.SwaggerUIBuilder;

import com.google.gson.JsonObject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


@Path("{id}/api-docs")
@Api(value = "/basil", description = "BASIL operations")
public class ApiDocsResource extends AbstractResource {
	
	@GET
	@Produces({"application/json", "text/html", "*/*"})
	@ApiOperation(value = "Get API Swagger Description")
	@ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal error") 
    })
	public Response get(@PathParam("id") String id,
						@ApiParam(value = "Accepted Media Type", allowableValues = "application/json,text/html")
						@HeaderParam("Accept") String accept) {
		try {

			if (getApiManager().getSpecification(id) == null) {
				return Response.status(404).build();
			}
			 if (accept.contains("text/html")) {
				String msg = SwaggerUIBuilder.build(requestUri);
				ResponseBuilder builder = Response.ok(msg);
				addHeaders(builder, id);
				return builder.build();
			}else if (accept.contains("application/json") || accept.contains("*/*")) {
				 Specification specification = getApiManager().getSpecification(id);
				 Doc docs = getApiManager().getDoc(id);
				 JsonObject o = SwaggerJsonBuilder.build(id, specification, docs, requestUri.getBaseUri().toString());
				 ResponseBuilder builder = Response.ok(o.toString());
				addHeaders(builder, id);
				return builder.build();
			} 
			return Response.status(406).build();
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
}
