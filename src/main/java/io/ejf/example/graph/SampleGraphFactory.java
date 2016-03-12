package io.ejf.example.graph;

import com.thinkaurelius.titan.core.Multiplicity;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.thinkaurelius.titan.graphdb.database.StandardTitanGraph;
import io.ejf.example.Friend;
import io.ejf.example.Person;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.LoggerFactory;

/**
 * Created by ejf3 on 3/12/16.
 */
public class SampleGraphFactory {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(SampleGraphFactory.class);

    public static TitanGraph INSTANCE = loadInMemory();

    private static TitanGraph loadInMemory(){
        BaseConfiguration config = new BaseConfiguration();
        config.addProperty("storage.backend","inmemory");

        StandardTitanGraph graph = (StandardTitanGraph) TitanFactory.open(config);

        TitanManagement mgmt = graph.openManagement();

        if (mgmt.getGraphIndex(Person.FULL_NAME) == null) {
            // unique indexes
            final PropertyKey fullNameKey = mgmt.makePropertyKey(Person.FULL_NAME).dataType(String.class).make();
            mgmt.buildIndex(Person.FULL_NAME, Vertex.class).addKey(fullNameKey).unique().buildCompositeIndex();

            // non-unique indexes
            final PropertyKey firstNameKey = mgmt.makePropertyKey(Person.FIRST_NAME).dataType(String.class).make();
            mgmt.buildIndex(Person.FIRST_NAME, Vertex.class).addKey(firstNameKey).buildCompositeIndex();

            final PropertyKey lastNameKey = mgmt.makePropertyKey(Person.LAST_NAME).dataType(String.class).make();
            mgmt.buildIndex(Person.LAST_NAME, Vertex.class).addKey(lastNameKey).buildCompositeIndex();

            // edge labels
            mgmt.makeEdgeLabel(Friend.FRIEND).multiplicity(Multiplicity.MULTI).make();
        }

        mgmt.commit();

        return graph;
    }
}
