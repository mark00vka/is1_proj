package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/gradovi")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class GradoviRest {

    @Inject
    private JmsSender jms;

    // GET /api/gradovi
    @GET
    public Response getSviGradovi() {
        String result = jms.sendToPodsistem1("<request/>", "GET_ALL_GRADOVI");
        return Response.ok(result).build();
    }

    // POST /api/gradovi
    // Body: <request><naziv>Beograd</naziv></request>
    @POST
    public Response kreirajGrad(String body) {
        String result = jms.sendToPodsistem1(body, "KREIRAJ_GRAD");
        return Response.status(Response.Status.CREATED).entity(result).build();
    }
}