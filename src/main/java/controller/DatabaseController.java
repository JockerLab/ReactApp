package controller;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import domain.Currency;
import domain.Item;
import domain.User;
import org.bson.Document;
import rx.Observable;
import rx.functions.Func1;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseController {
    private static final MongoClient client = createMongoClient();
    private final String DATABASE_NAME = "reactdb";

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    public Observable<String> registerUser(User user) {
        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection("user");
        Document document = new Document("id", user.id)
                .append("name", user.name)
                .append("currency", user.currency.toString());
        return isNotExists(collection, user.id).flatMap(empty -> {
            String message = "User " + user.id + " already exists";
            if (empty) {
                message = "User " + user.id + ": " + user.name + " was registered";
                String finalMessage = message;
                return collection.insertOne(document).map(x -> finalMessage);
            }

            return Observable.just(message);
        });
    }

    public Observable<String> addItem(Item item) {
        MongoCollection<Document> collection = client.getDatabase(DATABASE_NAME).getCollection("item");
        Document document = new Document("id", item.id)
                .append("name", item.name);
        for (Currency currency : Currency.values()) {
            document.append("cost-" + currency.toString(), item.cost.get(currency));
        }
        return isNotExists(collection, item.id).flatMap(empty -> {
            String message = "Item " + item.id + " already exists";
            if (empty) {
                message = "Item " + item.id + ": " + item.name + " was added";
                String finalMessage = message;
                return collection.insertOne(document).map(x -> finalMessage);
            }
            return Observable.just(message);
        });
    }

    private static Observable<Boolean> isNotExists(MongoCollection<Document> collection, int id) {
        return collection.find(eq("id", id)).toObservable().isEmpty();
    }

    public Observable<String> showItemsForUser(int uId) {
        Observable<User> user = client
                .getDatabase(DATABASE_NAME)
                .getCollection("user")
                .find(eq("id", uId))
                .toObservable()
                .map(User::new);

        Observable<Item> items = client
                .getDatabase(DATABASE_NAME)
                .getCollection("item")
                .find()
                .toObservable()
                .map(Item::new);

        return user.isEmpty().flatMap(empty -> {
            if (empty) {
                return Observable.just("User " + uId + " is not registered");
            }
            return user.flatMap(u -> items.map(item ->
                item.toString(u.currency)
            ));
        });
    }

}
