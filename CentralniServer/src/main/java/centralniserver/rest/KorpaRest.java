package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/korpa")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class KorpaRest {

    @Inject private JmsSender jms;

    // GET /api/korpa
    @GET
    public Response getKorpa(@HeaderParam("X-Korisnik-Id") String id) {
        String xml = "<request><id_korisnik>" + id + "</id_korisnik></request>";
        return Response.ok(jms.sendToPodsistem2(xml, "GET_KORPA")).build();
    }

    // POST /api/korpa
    // Body: <request><id_artikl>1</id_artikl><kolicina>2</kolicina></request>
    @POST
    public Response dodajUKorpu(@HeaderParam("X-Korisnik-Id") String id,
                                 String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "DODAJ_U_KORPU")).build();
    }

    // POST /api/korpa/obrisi
    // Body: <request><id_artikl>1</id_artikl><kolicina>1</kolicina></request>
    @POST
    @Path("/obrisi")
    public Response obrisiIzKorpe(@HeaderParam("X-Korisnik-Id") String id,
                                   String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "OBRISI_IZ_KORPE")).build();
    }

    // GET /api/korpa/wishlist
    @GET
    @Path("/wishlist")
    public Response getWishlist(@HeaderParam("X-Korisnik-Id") String id) {
        String xml = "<request><id_korisnik>" + id + "</id_korisnik></request>";
        return Response.ok(jms.sendToPodsistem2(xml, "GET_WISHLIST")).build();
    }

    // POST /api/korpa/wishlist
    @POST
    @Path("/wishlist")
    public Response dodajUWishlist(@HeaderParam("X-Korisnik-Id") String id,
                                    String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "DODAJ_U_WISHLIST")).build();
    }

    // POST /api/korpa/wishlist/obrisi
    @POST
    @Path("/wishlist/obrisi")
    public Response obrisiIzWishlist(@HeaderParam("X-Korisnik-Id") String id,
                                      String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        return Response.ok(jms.sendToPodsistem2(xml, "OBRISI_IZ_WISHLIST")).build();
    }
}