package org.example.calculatorai.agent;

import com.google.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

@Path("/agent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AgentResource {

    private final AgentService service;

    @Inject
    public AgentResource(AgentService service) {
        this.service = service;
    }

    @POST
    @Path("/message")
    public Response processMessage(AgentRequest request) {
        try {
            AgentResponse result = service.process(
                    request == null ? null : request.getMessage());

            return Response.ok(result).build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(new AgentResponse(
                            "ERROR", false, null,
                            "The request was interrupted. Please retry."))
                    .build();

        } catch (IOException e) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity(new AgentResponse(
                            "ERROR", false, null,
                            "The calculation service is temporarily unavailable."))
                    .build();
        }
    }
}
