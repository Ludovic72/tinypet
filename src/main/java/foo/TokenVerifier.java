package foo;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

public class TokenVerifier {

    private static final String CLIENT_ID = "681751757032-j0sin0l00292r7racqo06aaqj1e22obs.apps.googleusercontent.com";
/*"588506488220-pfvirp57eoumio5l6bsdbbcao2f9t68s.apps.googleusercontent.com "*/; // Remplacez par votre Client ID

    public static GoogleIdToken.Payload verifyToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            return payload;
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }

}