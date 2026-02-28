package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/artikli")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class ArtikliRest {

    @Inject private JmsSender jms;

    // GET /api/artikli/moji
    @GET
    @Path("/moji")
    public Response getMojiArtikli(@HeaderParam("X-Korisnik-Id") String id) {
        String xml = "<request><id_korisnik>" + id + "</id_korisnik></request>";
        return Response.ok(jms.sendToPodsistem2(xml, "GET_ARTIKLI_KORISNIKA")).build();
    }

    // POST /api/artikli
    @POST
    public Response kreirajArtikl(@HeaderParam("X-Korisnik-Id") String id,
                                   String body) {
        String xml = body.replace("<request>",
            "<request><id_kreator>" + id + "</id_kreator>");
        return Response.status(201)
                       .entity(jms.sendToPodsistem2(xml, "KREIRAJ_ARTIKL"))
                       .build();
    }

    // PUT /api/artikli/{id}/cena
    @PUT
    @Path("/{id}/cena")
    public Response promeniCenu(@PathParam("id") int idArtikl,
                                 @HeaderParam("X-Korisnik-Id") String idKorisnik,
                                 String body) {
        String xml = body.replace("<request>",
            "<request><id_artikl>" + idArtikl + "</id_artikl>" +
            "<id_korisnik>" + idKorisnik + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "PROMENI_CENU")).build();
    }

    // PUT /api/artikli/{id}/popust
    @PUT
    @Path("/{id}/popust")
    public Response postaviPopust(@PathParam("id") int idArtikl,
                                   @HeaderParam("X-Korisnik-Id") String idKorisnik,
                                   String body) {
        String xml = body.replace("<request>",
            "<request><id_artikl>" + idArtikl + "</id_artikl>" +
            "<id_korisnik>" + idKorisnik + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "POSTAVI_POPUST")).build();
    }
}