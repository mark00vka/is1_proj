package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/kategorije")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class KategorijeRest {

    @Inject private JmsSender jms;

    // GET /api/kategorije
    @GET
    public Response getSveKategorije() {
        return Response.ok(
            jms.sendToPodsistem2("<request/>", "GET_ALL_KATEGORIJE")
        ).build();
    }

    // POST /api/kategorije
    // Body: <request><naziv>Elektronika</naziv></request>
    // Body with parent: <request><naziv>Telefoni</naziv><id_nadkategorija>1</id_nadkategorija></request>
    @POST
    public Response kreirajKategoriju(String body) {
        return Response.status(201)
                       .entity(jms.sendToPodsistem2(body, "KREIRAJ_KATEGORIJU"))
                       .build();
    }
}