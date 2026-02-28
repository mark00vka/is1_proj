package centralniserver.rest;

import centralniserver.jms.JmsSender;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/korisnici")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class KorisniciRest {

    @Inject
    private JmsSender jms;

    // POST /api/korisnici/login
    // Body: <request><korisnicko_ime>x</korisnicko_ime><sifra>y</sifra></request>
    @POST
    @Path("/login")
    public Response login(String body) {
        String result = jms.sendToPodsistem1(body, "PROVERI_KORISNIKA");
        return Response.ok(result).build();
    }

    // GET /api/korisnici
    @GET
    public Response getSviKorisnici() {
        String result = jms.sendToPodsistem1("<request/>", "GET_ALL_KORISNICI");
        return Response.ok(result).build();
    }

    // POST /api/korisnici
    // Body: <request><korisnicko_ime>..</korisnicko_ime><sifra>..</sifra>...</request>
    @POST
    public Response kreirajKorisnika(String body) {
        String result = jms.sendToPodsistem1(body, "KREIRAJ_KORISNIKA");
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    // PUT /api/korisnici/{id}/novac
    // Body: <request><iznos>1000</iznos></request>
    @PUT
    @Path("/{id}/novac")
    public Response dodajNovac(@PathParam("id") int id, String body) {
        // Inject the id into the XML body
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        String result = jms.sendToPodsistem1(xml, "DODAJ_NOVAC");
        return Response.ok(result).build();
    }

    // PUT /api/korisnici/{id}/adresa
    // Body: <request><adresa>Nova adresa</adresa><id_grad>1</id_grad></request>
    @PUT
    @Path("/{id}/adresa")
    public Response promeniAdresu(@PathParam("id") int id, String body) {
        String xml = body.replace("<request>",
            "<request><id_korisnik>" + id + "</id_korisnik>");
        String result = jms.sendToPodsistem1(xml, "PROMENI_ADRESU");
        return Response.ok(result).build();
    }
}