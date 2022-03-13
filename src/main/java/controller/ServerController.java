package controller;

import domain.Currency;
import domain.Item;
import domain.User;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerController {
    private final DatabaseController database;

    public ServerController(DatabaseController database) {
        this.database = database;
    }

    public Observable<String> makeRequest(String type, HttpServerRequest<ByteBuf> request) {
        try {
            switch (type) {
                case "register_user":
                    return registerUser(request);
                case "add_item":
                    return addItem(request);
                case "show_item":
                    return showItem(request);
                default:
                    return Observable.just("Invalid request");
            }
        } catch (Exception e) {
            return Observable.just("An error occurred: " + e.getMessage());
        }
    }

    private Observable<String> registerUser(HttpServerRequest<ByteBuf> request) {
        List<String> id = request.getQueryParameters().get("id");
        List<String> name = request.getQueryParameters().get("name");
        List<String> currency = request.getQueryParameters().get("currency");
        return database.registerUser(new User(
                Integer.parseInt(id.get(0)),
                name.get(0),
                Currency.valueOf(currency.get(0))
        ));
    }

    private Observable<String> addItem(HttpServerRequest<ByteBuf> request) {
        List<String> id = request.getQueryParameters().get("id");
        List<String> name = request.getQueryParameters().get("name");
        final Map<Currency, Integer> costs = new HashMap<>();
        int i = 0;
        for (Currency currency : Currency.values()) {
            List<String> value = request.getQueryParameters().get("cost-" + currency.toString());
            costs.put(currency, Integer.parseInt(value.get(0)));
            i++;
        }
        return database.addItem(new Item(
                Integer.parseInt(id.get(0)),
                name.get(0),
                costs
        ));
    }

    private Observable<String> showItem(HttpServerRequest<ByteBuf> request) {
        List<String> id = request.getQueryParameters().get("id");
        return database.showItemsForUser(Integer.parseInt(id.get(0)));
    }

}
