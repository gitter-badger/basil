package uk.ac.open.kmi.basil.rest.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hp.hpl.jena.query.Query;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import uk.ac.open.kmi.basil.sparql.QueryParameter;
import uk.ac.open.kmi.basil.sparql.Specification;
import uk.ac.open.kmi.basil.sparql.VariablesBinder;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("{id}/explain")
@Api(value = "/basil", description = "BASIL operations")
public class ExplainResource extends AbstractResource {

	@GET
	@Produces("application/json")
	@ApiOperation(value = "Explain API invocation")
	@ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal error") 
    })
	public Response get(@PathParam("id") String id) {
		try {

			if (!getApiManager().existsSpec(id)) {
				return Response.status(404).build();
			}

			Specification specification = getApiManager().getSpecification(id);
			VariablesBinder binder = new VariablesBinder(specification);

			List<String> missing = new ArrayList<String>();
			for (QueryParameter qp : specification.getParameters()) {
				if (requestUri.getQueryParameters().containsKey(qp.getName())) {
					List<String> values = requestUri.getQueryParameters().get(qp.getName());
					binder.bind(qp.getName(), values.get(0));
				} else if (!qp.isOptional()) {
					missing.add(qp.getName());
				}
			}

			Query q = binder.toQuery();
			JsonObject m = new JsonObject();
			m.add("query", new JsonPrimitive(q.toString()));

			ResponseBuilder builder = Response.ok(m.toString());
			addHeaders(builder, id);
			builder.header(Headers.Endpoint, specification.getEndpoint());
			return builder.build();
		} catch (IOException e) {
			return packError(Response.serverError(), e).build();
		}
	}
}
