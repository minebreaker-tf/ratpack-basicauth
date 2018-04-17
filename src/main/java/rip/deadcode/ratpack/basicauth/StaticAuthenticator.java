package rip.deadcode.ratpack.basicauth;

import ratpack.exec.Promise;

public final class StaticAuthenticator implements BasicAuthAuthenticator {

    private final String user;
    private final String password;

    public StaticAuthenticator( String user, String password ) {
        this.user = user.replaceAll( ":", "" );
        this.password = password;
    }

    @Override
    public Promise<Boolean> authenticate( String user, String password ) {
        return Promise.value( this.user.equals( user ) && this.password.equals( password ) );
    }
}
