package org.acumos.streamer.controller;

import java.io.InputStream;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acumos.streamer.model.ResponseMessage;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "CMLP Streamer")
// @Path("/")
@Produces({ MediaType.APPLICATION_JSON })
public interface RestConsumerService {

	
	@PUT
	@Path("{catalogKey}/{fileName}")
	@Produces({ MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Responds back sciring result", notes = "Returns a No content successful message after publishing the data recieved and scoring perfromed after using the same data.", response = ResponseMessage.class)
	@ApiResponses(value = { @ApiResponse(code = 204, message = ""), @ApiResponse(code = 400, message = "Bad Payload"),
			@ApiResponse(code = 404, message = "Service not available"),
			@ApiResponse(code = 500, message = "Unexpected Runtime error") })
	public Response operateData(@HeaderParam("Authorization") String authorization,
			@HeaderParam("feedAuth") String feedAuthorization, @PathParam("catalogKey") String catalogKey,
			@PathParam("fileName") String fileName,
			@Multipart(value = "file", type = "application/octet-stream") InputStream attachedFiles);

}
