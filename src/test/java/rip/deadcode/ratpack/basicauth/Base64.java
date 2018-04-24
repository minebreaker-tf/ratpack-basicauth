package rip.deadcode.ratpack.basicauth;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public final class Base64 {

    public static void main( String[] args ) {

        String target = ":";

        System.out.println( BaseEncoding.base64().encode( target.getBytes( StandardCharsets.UTF_8 ) ) );
    }
}
