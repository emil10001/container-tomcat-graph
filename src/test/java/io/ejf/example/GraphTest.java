package io.ejf.example;

import com.thinkaurelius.titan.core.TitanGraph;
import io.ejf.example.graph.SampleGraphFactory;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;

import static org.junit.Assert.assertNotNull;

/**
 * Created by ejf3 on 3/12/16.
 */
public class GraphTest {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(GraphTest.class);

    @BeforeClass
    public static void init() {
        log.info("trying to get a graph instance");

        TitanGraph graph = SampleGraphFactory.INSTANCE;

        Person john = Person.get("John", "Thompson");
        Person jane = Person.get("Jane", "Thompson");
        Person jim = Person.get("Jim", "Beam");
        Person jenny = Person.get("Jenny", "Lopez");

        ArrayList<Person> people = new ArrayList<>();
        people.add(john);
        people.add(jane);
        people.add(jim);
        people.add(jenny);

        john.addFriend(jane, "sibling");
        john.addFriend(jim, "math_class");
        john.addFriend(jenny, "band");

        jane.addFriend(jenny, "gym_class");

        graph.tx().commit();

        log.info("got a graph instance, added {}", people);
    }

    @Test
    public void getJohn(){
        final Iterator<Vertex> it =
                SampleGraphFactory.INSTANCE.traversal().V().has(Person.FULL_NAME, "Thompson,John");
        Vertex johnVertex = it.hasNext() ? it.next() : null;

        assertNotNull(johnVertex);

        Person john = Person.get(johnVertex);

        assertNotNull(john);

        log.info("here's John {}", john);
    }

    @Test
    public void getAllRel(){
        Iterator<Edge> it =
                SampleGraphFactory.INSTANCE.traversal().E().hasLabel(Friend.FRIEND);

        log.info("found results? {}", it.hasNext());

        while (it.hasNext()) {
            Edge e = it.next();
            String rel = e.value(Friend.KNOWS_FROM);
            ArrayList<Person> friends = new ArrayList<>();
            friends.add(Person.get(e.inVertex()));
            friends.add(Person.get(e.outVertex()));
            log.info("{} : {}", rel, friends);
        }
    }

    @Test
    public void getRel(){
        Iterator<Edge> it =
                SampleGraphFactory.INSTANCE.traversal().E()
                        .hasLabel(Friend.FRIEND).has(Friend.KNOWS_FROM, "math class");

        log.info("found results? {}", it.hasNext());

        while (it.hasNext()) {
            Edge e = it.next();
            String rel = e.value(Friend.KNOWS_FROM);
            ArrayList<Person> friends = new ArrayList<>();
            friends.add(Person.get(e.inVertex()));
            friends.add(Person.get(e.outVertex()));
            log.info("{} : {}", rel, friends);
        }
    }

}
