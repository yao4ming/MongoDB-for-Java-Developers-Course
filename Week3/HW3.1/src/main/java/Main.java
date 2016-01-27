import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class Main {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        MongoClient client = new MongoClient("localhost", 27017);
        MongoDatabase db = client.getDatabase("school");
        MongoCollection<Document> collection = db.getCollection("students");

        try (MongoCursor<Document> cursor = collection.find().iterator()) {

            //iterate through students
            while (cursor.hasNext()) {
                Document student = cursor.next();
                ArrayList<Document> scores = (ArrayList) student.get("scores");

                //iterate through their scores
                double lowest = 100;
                for (Document score : scores) {

                    //only process homework scores
                    if (score.get("type").equals("homework")) {
                        double scoreNum = (double) score.get("score");
                        if (scoreNum < lowest) {
                            lowest = scoreNum;
                        }
                    }
                }

                //remove lowest hw score
                UpdateResult result = collection.updateOne(eq("_id", student.get("_id")),
                        new Document("$pull", new Document("scores", new Document("score", lowest))));

            }
        }
    }
}
