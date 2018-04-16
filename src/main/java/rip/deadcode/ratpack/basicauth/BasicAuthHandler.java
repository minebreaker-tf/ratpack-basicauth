package rip.deadcode.ratpack.basicauth;

import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class BasicAuthHandler implements Handler {

    // TODO read spec to verify logic

    private static final Logger logger = LoggerFactory.getLogger( BasicAuthHandler.class );

    private final String realm;
    private final String token;

    // TODO refactor using map (or other flexible method) for user-password pair
    public BasicAuthHandler( String realm, String user, String password ) {
        this.realm = realm;
        this.token = createToken( user, password );
    }

    @Override
    public void handle( Context ctx ) {

        ctx.getResponse().getHeaders().add( HttpHeaders.WWW_AUTHENTICATE, "Basic realm=" + realm );

        Optional<String> authorizationOptional = ctx.header( HttpHeaders.AUTHORIZATION );
        if ( authorizationOptional.isPresent() ) {
            String authorization = authorizationOptional.get();

            if ( authorization.equals( token ) ) {
                ctx.next();
            } else {
                String decoded = new String( BaseEncoding.base64().decode( token ), StandardCharsets.US_ASCII );
                logger.debug( "Wrong user-ID or password: {}", decoded );
                renderFailure( ctx );
            }

        } else {
            renderFailure( ctx );
        }
    }

    private String createToken( String user, String password ) {
        String userId = user.replaceAll( ":", "" );
        String userPass = userId + ":" + password;
        String token = "Basic " + BaseEncoding.base64().encode( userPass.getBytes( StandardCharsets.US_ASCII ) );
        logger.debug( "Token: {}", token );
        return token;
    }

    private void renderFailure( Context ctx ) {
        ctx.getResponse().status( 401 );
        ctx.render( "401 Authorization Required" );
    }
}
