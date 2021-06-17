package org.example.http;
import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import org.example.CRUDstatements;
import org.example.entities.User;

public  class MyAuthenticator extends Authenticator {
    private static final String AUTHORIZATION_HEADER = "Authorization";

     @Override
    public Result authenticate(final HttpExchange httpExchange) {
        final String token = httpExchange.getRequestHeaders().getFirst(AUTHORIZATION_HEADER);
            try {
                final String username = ServerHttp.getUserLoginFromJWT(token);
                final User user = CRUDstatements.getUserByLogin(username);
                if (user != null) {
                    return new Success(new HttpPrincipal(username, user.getRole()));
                }else
                    {
                        return new Failure(403);
                    }
            } catch (Exception e) {
                return new Failure(403);
            }
    }
}