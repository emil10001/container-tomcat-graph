package io.ejf.example;

import com.google.gson.Gson;
import com.thinkaurelius.titan.core.TitanGraph;
import io.ejf.example.graph.SampleGraphFactory;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by ejf3 on 3/12/16.
 */
@WebServlet(
        asyncSupported = false,
        name = "Graph",
        urlPatterns = {"/*"},
        initParams = {
                @WebInitParam(name = "readonly", value = "false"),
                @WebInitParam(name = "showServerInfo", value = "false")
        }
)
public class Main extends HttpServlet {
    private static final org.slf4j.Logger log = LoggerFactory
            .getLogger(Main.class);

    public void init() {
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


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        log.info("doGet");
        PrintWriter out = response.getWriter();
        try {
            if (null == request.getQueryString() || request.getQueryString().isEmpty())
                return;

            log.info("doGet with a queryString {}", request.getQueryString());

            Gson gson = new Gson();
            HashMap<String, ArrayList> collection = new HashMap<>();
            String[] queryArray = request.getQueryString().split("\\&");

            for (String query : queryArray) {
                if (query.startsWith("rel=")) {
                    String relationship = query.split("=")[1];
                    log.info("query with relationship {}", relationship);
                    ArrayList<Person> people = new ArrayList<>();
                    people.addAll(Friend.getFriends(relationship));
                    collection.put(relationship, people);
                }

                if (query.startsWith("name=")) {
                    String fullName = query.split("=")[1];
                    log.info("query with name {}", fullName);

                    ArrayList<Person> people = new ArrayList<>();
                    Person person = Person.get(fullName);
                    if (null != person) {
                        log.info("found {}", person.toString());
                        people.add(person);
                    } else
                        log.info("couldn't find {}", fullName);

                    collection.put("person", people);
                }
            }
            String json = gson.toJson(collection);
            out.println(json);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
}
