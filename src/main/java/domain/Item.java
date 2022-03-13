package domain;

import com.mongodb.connection.Stream;
import org.bson.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Item {
    public final int id;
    public final String name;
    public final Map<Currency, Integer> cost;

    public Item(int id, String name, Map<Currency, Integer> cost) {
        this.id = id;
        this.name = name;
        this.cost = cost;
    }

    public static Map<Currency, Integer> getCosts(Document doc) {
        HashMap<Currency, Integer> costs = new HashMap<>();
        for (Currency currency : Currency.values()) {
            costs.put(currency, doc.getInteger("cost-" + currency.toString()));
        }
        return costs;
    }

    public Item(Document doc) {
        this(
                doc.getInteger("id"),
                doc.getString("name"),
                getCosts(doc)
        );
    }

    public String toString(Currency currency) {
        return "Item {" +
                "id: " + id +
                ", name: '" + name + '\'' +
                ", cost: '" + cost.get(currency) + '\'' +
                '}';
    }
}
