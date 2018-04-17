package rip.deadcode.ratpack.basicauth;

import ratpack.exec.Promise;

/**
 * Interface for authentication logic.
 */
public interface BasicAuthAuthenticator {

    /**
     * Authenticates requested user and password.
     * If the user and the password are valid, {@link Promise} of {@link Boolean#TRUE} is returned.
     * Otherwise false is returned.
     *
     * @param user User ID to authenticate.
     * @param password Password of the user.
     * @return {@link Promise} of the boolean value. True if the challenge was successful.
     */
    public Promise<Boolean> authenticate( String user, String password );
}
