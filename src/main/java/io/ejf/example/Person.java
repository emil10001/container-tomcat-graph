package io.ejf.example;

import io.ejf.example.graph.SampleGraphFactory;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by ejf3 on 3/12/16.
 */
public class Person {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(Person.class);

    public static String FULL_NAME = "fullName";
    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "lastName";

    private final String fullName;
    private final String firstName;
    private final String lastName;
    private Set<Person> friends = new HashSet<>();
    private String knowsFrom;
    private transient final Vertex vertex;


    public static Person get(String fullName) {
        final Iterator<Vertex> it =
                SampleGraphFactory.INSTANCE.traversal().V().has(FULL_NAME, fullName);
        Vertex vertex = it.hasNext() ? it.next() : null;

        return (null != vertex)? new Person(vertex) : null;
    }

    public static Person get(String firstName, String lastName) {
        String fullName = makeFullName(firstName, lastName);
        final Iterator<Vertex> it =
                SampleGraphFactory.INSTANCE.traversal().V().has(FULL_NAME, fullName);
        Vertex vertex = it.hasNext() ? it.next() : null;

        return (null != vertex)? new Person(vertex) : new Person(firstName, lastName);
    }

    public static Person get(Vertex vertex) {
        if (null == vertex)
            return null;

        if (vertex.property(FULL_NAME).isPresent())
            return new Person(vertex);

        return null;
    }

    private Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = makeFullName(firstName, lastName);
        this.vertex = createNewVertex(this);
    }

    private Person(Vertex vertex) {
        this.fullName = vertex.value(FULL_NAME);
        this.firstName = vertex.value(FIRST_NAME);
        this.lastName = vertex.value(LAST_NAME);
        this.vertex = vertex;
        buildFriends();
    }

    private static Vertex createNewVertex(Person person) {
        Vertex vertex = SampleGraphFactory.INSTANCE.addVertex();
        vertex.property(FULL_NAME, person.getFullName());
        vertex.property(FIRST_NAME, person.getFirstName());
        vertex.property(LAST_NAME, person.getLastName());
        return vertex;
    }

    public static String makeFullName(String firstName, String lastName) {
        return lastName + "," + firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void buildFriends() {
        GraphTraversal<Vertex, Map<String, Object>> z = SampleGraphFactory.INSTANCE.traversal().V()
                .has(FULL_NAME, fullName)
                .outE(Friend.FRIEND)
                .as("e")
                .dedup()
                .inV()
                .as("v")
                .dedup()
                .select("v", "e");

        while (z.hasNext()) {
            Map<String, Object> map = z.next();
            Person friend = Person.get((Vertex) map.get("v"));
            Edge e = (Edge) map.get("e");
            if (null != e) {
                friend.setKnowsFrom((String) e.value(Friend.KNOWS_FROM));
            }

            friends.add(friend);
            log.info("friend {}", friend.toString());
        }
    }

    public void addFriend(Person friend, String knowsFrom) {
        friends.add(friend);
        if (hasEdge(friend))
            return;
        Friend.create(this, friend, knowsFrom);
    }

    private boolean hasEdge(Person friend) {
        return SampleGraphFactory.INSTANCE.traversal()
                .V().has(FULL_NAME, fullName)
                .outE(Friend.FRIEND)
                .has(FULL_NAME, friend.getFullName())
                .hasNext();
    }

    private void setKnowsFrom(String knowsFrom) {
        this.knowsFrom = knowsFrom;
    }

    public String getKnowsFrom() {
        return knowsFrom;
    }

    public Set<Person> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        if (null != knowsFrom)
            return fullName + ", from " + knowsFrom;
        return fullName;
    }
}
