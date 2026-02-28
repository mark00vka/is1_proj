/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package centralniserver;

import centralniserver.jms.JmsHelper;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/korisnici")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class KorisniciResource {

    @Inject
    private JmsHelper jms;

    @POST
    @Path("/login")
    public Response login(String body) {
        String result = jms.sendToPodsistem1(body, "PROVERI_KORISNIKA");
        return Response.ok(result).build();
    }

    @GET
    public Response getSviKorisnici(
            @HeaderParam("Authorization") String token) {
        String result = jms.sendToPodsistem1("{}", "GET_ALL_KORISNICI");
        return Response.ok(result).build();
    }

    @POST
    public Response kreirajKorisnika(String body) {
        String result = jms.sendToPodsistem1(body, "KREIRAJ_KORISNIKA");
        return Response.status(Response.Status.CREATED)
                       .entity(result).build();
    }

    @PUT
    @Path("/{id}/novac")
    public Response dodajNovac(@PathParam("id") int id, String body) {
        // Merge id into body before sending
        String json = "{\"id_korisnik\":" + id + "," +
                      body.substring(1); // append rest of body JSON
        String result = jms.sendToPodsistem1(json, "DODAJ_NOVAC");
        return Response.ok(result).build();
    }

    @PUT
    @Path("/{id}/adresa")
    public Response promeniAdresu(@PathParam("id") int id, String body) {
        String json = "{\"id_korisnik\":" + id + "," +
                      body.substring(1);
        String result = jms.sendToPodsistem1(json, "PROMENI_ADRESU");
        return Response.ok(result).build();
    }
}