package om.github.crowin;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * AuthClient using OkHttp. Sends form-encoded POST with given credentials and returns OkHttp Response.
 * Caller is responsible for closing the Response body.
 */
public class AuthClient implements SessionClient {
    private final OkHttpClient client;

    public AuthClient() {
        this.client = new OkHttpClient();
    }

    public Map<String, String> authenticate(String username, String password) throws IOException {
        Objects.requireNonNull(username, "url is required");
        Objects.requireNonNull(password, "credentials are required");
        var authRequest = ":{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        var body = RequestBody.create(authRequest.getBytes());
        Request request = new Request.Builder()
                .url("")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        var cookies = client.newCall(request).execute().headers("Set-Cookie");
        return cookies
                .stream()
                .map(c -> c.split(";")[0])
                .collect(Collectors.toMap(c -> c.split("=")[0], c -> c.split("=")[1]));
    }

    @Override
    public Map<String, String> initSession(Map<String, String> userCredentials) throws Exception {
        return authenticate(userCredentials.get("username"), userCredentials.get("password"));
    }
}
