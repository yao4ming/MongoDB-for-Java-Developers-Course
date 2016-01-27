package course;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Sorts.orderBy;

public class BlogPostDAO {
    MongoCollection<Document> postsCollection;

    public BlogPostDAO(final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public Document findByPermalink(String permalink) {

        // XXX HW 3.2,  Work Here
        Bson filter = eq("permalink", permalink);
        Document post = postsCollection.find(filter).first();
        //System.out.println(post.toJson());
        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Document> findByDateDescending(int limit) {

        // XXX HW 3.2,  Work Here
        // Return a list of DBObjects, each one a post from the posts collection
        Bson sort = orderBy(descending("date"));
        List<Document> posts = postsCollection.find()
                                              .sort(sort)
                                              .limit(limit)
                                              .into(new ArrayList<Document>());

        //for (Document post : posts) System.out.println(post.toJson());

        return posts;
    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        // XXX HW 3.2, Work Here
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.


        // Build the post object and insert it
        Document post = new Document("title", title)
                                    .append("author", username)
                                    .append("body", body)
                                    .append("permalink", permalink)
                                    .append("tags", tags)
                                    .append("comments", Arrays.asList())
                                    .append("date", new Date());

        System.out.println(post.toJson());

        postsCollection.insertOne(post);

        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // XXX HW 3.3, Work Here
        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments
        Document commentInfo;
        if (email.equals("")) {
            commentInfo = new Document("author", name).append("body", body);
        } else {
            commentInfo = new Document("author", name).append("body", body).append("email", email);
        }

        postsCollection.updateOne(eq("permalink", permalink), new Document("$addToSet", new Document("comments", commentInfo)));
    }
}
