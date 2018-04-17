package rip.deadcode.ratpack.basicauth;

import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

/**
 * Basic authentication handler.
 * If the Authorization header value is accepted by {@link BasicAuthAuthenticator},
 * the context is delegated to the next handlers calling {@link Context#next()}.
 * If rejected, client error with 401 status code is rendered.
 */
public final class BasicAuthHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger( BasicAuthHandler.class );

    private static final Splitter authorizationSplitter = Splitter.on( " " ).omitEmptyStrings().limit( 2 );
    private static final Splitter userPasswordSplitter = Splitter.on( ":" ).limit( 2 );

    private final String realm;
    private final BasicAuthAuthenticator authenticator;

    public BasicAuthHandler( BasicAuthAuthenticator authenticator ) {
        this( "WallyWorld", authenticator );
    }

    public BasicAuthHandler( String realm, BasicAuthAuthenticator authenticator ) {
        this.realm = realm;
        this.authenticator = authenticator;
    }

    @Override
    public void handle( Context ctx ) {

        ctx.getResponse().getHeaders().add( HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"" );

        Optional<String> authorizationOptional = ctx.header( HttpHeaders.AUTHORIZATION );
        if ( authorizationOptional.isPresent() ) {

            String authorization = authorizationOptional.get();
            List<String> parts = authorizationSplitter.splitToList( authorization );
            checkState( parts.size() == 2, "Invalid Authorization header value: %s", authorization );
            checkState( parts.get( 0 ).equals( "Basic" ), "Not Basic authentication." );

            String decoded = new String( BaseEncoding.base64().decode( parts.get( 1 ) ), StandardCharsets.US_ASCII );
            List<String> userPassword = userPasswordSplitter.splitToList( decoded );
            checkState( userPassword.size() == 2, "Invalid token: %s", userPassword );
            String user = userPassword.get( 0 );
            String password = userPassword.get( 1 );

            authenticator.authenticate( user, password ).then( success -> {
                if ( success ) {
                    ctx.next();
                } else {
                    logger.debug( "Wrong user or password. User: {}", user );
                    ctx.clientError( 401 );
                }
            } );

        } else {
            ctx.clientError( 401 );
        }
    }
}
