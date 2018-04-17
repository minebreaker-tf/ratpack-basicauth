package rip.deadcode.ratpack.basicauth;

import ratpack.exec.Promise;

import java.util.Map;
import java.util.Objects;

/**
 * Authenticates users with given user-password map.
 */
public final class StaticAuthenticator implements BasicAuthAuthenticator {

    private final Map<String, String> userPasswords;

    public StaticAuthenticator( Map<String, String> userPasswords ) {
        this.userPasswords = userPasswords;
    }

    @Override
    public Promise<Boolean> authenticate( String user, String password ) {

        return Promise.value( Objects.equals( password, this.userPasswords.get( user ) ) );
    }
}
