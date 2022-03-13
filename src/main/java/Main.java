import controller.DatabaseController;
import controller.ServerController;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import javax.xml.crypto.Data;

public class Main {
    public static void main(final String[] args) {
        DatabaseController database = new DatabaseController();
        HttpServer
                .newServer(8081)
                .start((req, resp) -> {
                    String type = req.getDecodedPath().substring(1);
                    Observable<String> response = new ServerController(database).makeRequest(type, req);
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }
}
