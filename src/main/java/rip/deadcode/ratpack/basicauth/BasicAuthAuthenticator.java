package rip.deadcode.ratpack.basicauth;

import ratpack.exec.Promise;

public interface BasicAuthAuthenticator {
    public Promise<Boolean> authenticate( String user, String password );
}
