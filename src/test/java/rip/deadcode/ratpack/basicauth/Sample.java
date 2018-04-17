package rip.deadcode.ratpack.basicauth;

import com.google.common.collect.ImmutableMap;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;

public final class Sample {

    public static void main( String[] args ) throws Exception {

        Action<Chain> handlers = chain -> {
            chain.all( new BasicAuthHandler( new StaticAuthenticator( ImmutableMap.of( "user", "password" ) ) ) );
            chain.all( ctx -> ctx.render( "OK" ) );
        };

        RatpackServer.of( spec -> {
            spec.serverConfig( ServerConfig.builder()
                                           .port( 8080 )
                                           .development( true )
                                           .build() )
                .handlers( handlers );
        } ).start();
    }
}
