/**
 * 
 */
package org.acumos.streamercatalog.controller;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestBody;

import org.acumos.streamercatalog.model.CatalogObject;
import org.acumos.streamercatalog.model.ResponseMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author am375y
 *
 */
@Api(value = "Streamer Catalog")
@Path("v1/streamers")
public interface RestCatalogService {


	@POST
	@Path("")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Respond a new streamer URL", notes = "Returns a JSON object with a string providing info about new streamer. ", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), 
			@ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response saveCatalog(@HeaderParam("Authorization") String authorization,
			@HeaderParam("CodeCloudAuthorization") String codeCloudAuthorization,
			@RequestBody CatalogObject objCatalog);

	
	@PUT
	@Path("/{streamerKey}")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Respond a updated streamer", notes = "Returns a JSON object with a string providing info about updated streamer", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Updated"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response updateCatalog(@HeaderParam("Authorization") String authorization,
			@HeaderParam("CodeCloudAuthorization") String codeCloudAuthorization,
			@PathParam("streamerKey") String catalogKey, @RequestBody CatalogObject objCatalog);

	
	@GET
	@Path("/{streamerKey}")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Respond a streamer object", notes = "Returns a JSON object with streamer details", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getCatalog(@HeaderParam("Authorization") String authorization,
			@PathParam("streamerKey") String catalogKey, @QueryParam("mode") String mode);

	
	@GET
	@Path("")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Respond a list of streamer", notes = "Returns a JSON object with a list of streamers according to the query params, if provided.", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response getCatalogs(@HeaderParam("Authorization") String authorization,
			@QueryParam("category") String category, @QueryParam("textSearch") String textSearch);

	
	@DELETE
	@Path("/{streamerKey}")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Respond no content", notes = "Returns No content after deleting a streamer corrsponding to provided key. "
			+ "Uses 'world' if a name is not specified", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 401, message = "Unauthorized"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response deleteCatalog(@HeaderParam("Authorization") String authorization,
			@PathParam("streamerKey") String catalogKey);

}
