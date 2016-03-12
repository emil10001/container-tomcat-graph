package io.ejf.example;

import io.ejf.example.graph.SampleGraphFactory;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ejf3 on 3/12/16.
 */
public class Friend {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(Friend.class);

    public static final String FRIEND = "friend";
    public static final String KNOWS_FROM = "knowsFrom";

    public static void create(Person me, Person friend, String knowsFrom) {
        Edge edge = me.getVertex().addEdge(FRIEND, friend.getVertex());
        edge.property(KNOWS_FROM, knowsFrom);
    }

    public static ArrayList<Person> getFriends(String knowsFrom) {
        Iterator<Edge> it =
                SampleGraphFactory.INSTANCE.traversal().E()
                        .hasLabel(Friend.FRIEND).has(Friend.KNOWS_FROM, knowsFrom);

        log.info("found results? {}", it.hasNext());

        ArrayList<Person> friends = new ArrayList<>();

        while (it.hasNext()) {
            Edge e = it.next();
            String rel = e.value(Friend.KNOWS_FROM);
            friends.add(Person.get(e.inVertex()));
            friends.add(Person.get(e.outVertex()));
            log.info("{} : {}", rel, friends);
        }

        return friends;
    }
}
