package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/narudzbine")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class NarudzbineRest {

    @Inject private JmsSender jms;

    // GET /api/narudzbine/moje
    @GET
    @Path("/moje")
    public Response getMojeNarudzbine(@HeaderParam("X-Korisnik-Id") String id) {
        String xml = "<request><id_korisnik>" + id + "</id_korisnik></request>";
        return Response.ok(
            jms.sendToPodsistem3(xml, "GET_NARUDZBINE")
        ).build();
    }

    // GET /api/narudzbine (admin only)
    @GET
    public Response getAllNarudzbine() {
        return Response.ok(
            jms.sendToPodsistem3("<request/>", "GET_ALL_NARUDZBINE")
        ).build();
    }

    // GET /api/narudzbine/transakcije (admin only)
    @GET
    @Path("/transakcije")
    public Response getAllTransakcije() {
        return Response.ok(
            jms.sendToPodsistem3("<request/>", "GET_ALL_TRANSAKCIJE")
        ).build();
    }

    // POST /api/narudzbine/plati
    // Body: <request>
    //         <adresa>Moja adresa 5</adresa>
    //         <id_grad>1</id_grad>
    //         <stavke>
    //           <stavka>
    //             <id_artikl>1</id_artikl>
    //             <kolicina>1</kolicina>
    //             <jedinicna_cena>114000</jedinicna_cena>
    //           </stavka>
    //         </stavke>
    //       </request>
    @POST
    @Path("/plati")
    public Response plati(@HeaderParam("X-Korisnik-Id") String id,
                           String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        return Response.ok(
            jms.sendToPodsistem3(xml, "PLATI")
        ).build();
    }
}