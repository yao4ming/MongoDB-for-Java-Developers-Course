package com.mongodb.Week1;

import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.bson.conversions.Bson;

import java.io.StringWriter;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.*;
import static spark.Spark.get;
import static spark.Spark.halt;


public class HelloWorldSparkFreeMarkerStyle {

    public static void main(String[] args) {

        //set path for FreeMarker Template loading
        Configuration configuration = new Configuration();
        configuration.setClassForTemplateLoading(HelloWorldSparkFreeMarkerStyle.class, "/");

        //Configure MongoDB
        MongoClient mongoClient = new MongoClient();
        MongoDatabase mongodb = mongoClient.getDatabase("students");
        final MongoCollection<Document> collection = mongodb.getCollection("grades");

        get("/hello", (req, res) -> {

            StringWriter writer = new StringWriter();
            try {
                Template helloTemplate = configuration.getTemplate("hello.ftl");

                Bson filter = eq("type", "homework");
                Bson projection  = fields(include("student_id"), include("score"), excludeId());
                Bson sort = orderBy(ascending("student_id"), descending("score"));

                List<Document> docs = collection.find(filter)
                                                .projection(projection)
                                                .sort(sort)
                                                .into(new ArrayList<>());


                for (int id = 0, i = 0; i < docs.size(); i++) {
                    if ((int)docs.get(i).get("student_id") != id) {
                        //System.out.println(docs.get(i-1).toJson());
                        collection.deleteOne(eq("score", docs.get(i-1).get("score")));
                        id++;
                    }
                }

                //delete last document as special case
                collection.deleteOne(eq("score", docs.get(docs.size()-1).get("score")));

                helloTemplate.process(null, writer);

            } catch (Exception e) {
                halt(500);
                e.printStackTrace();
            }

            return writer;
        });
    }
}
