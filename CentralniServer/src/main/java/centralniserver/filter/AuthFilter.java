package centralniserver.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Provider
public class AuthFilter implements ContainerRequestFilter {

    // These paths are open to everyone
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
        "korisnici/login"
    ));

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

        // Allow public paths
        for (String pub : PUBLIC_PATHS) {
            if (path.endsWith(pub)) return;
        }

        // Check Authorization header
        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("<greska>Niste prijavljeni</greska>")
                        .type("application/xml")
                        .build()
            );
            return;
        }

        // Extract user id from token and put it in a header
        // so REST resources can read who is calling
        String token = authHeader.substring(7);
        ctx.getHeaders().putSingle("X-Korisnik-Id", token);
    }
}