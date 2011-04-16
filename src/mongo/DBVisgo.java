package mongo;

import java.net.UnknownHostException;
import java.util.regex.Pattern;

import srl.visgo.data.Document;

import com.google.gdata.data.introspection.Collection;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;



public class DBVisgo {
	Mongo mongo;
	DB database;
	public static void main(String[] args) {
		try {
			Mongo m = new Mongo( "localhost" );
			DB db = m.getDB("testdb");
			DBCollection collection = db.getCollection("collection1");
			
			
			QueryBuilder query = new QueryBuilder();
			query.or(new BasicDBObject("name.first",Pattern.compile("\\.*eorge")));
			/*DBObject doc = collection.findOne(query);
			
			if(doc==null){
				doc = new BasicDBObject();
				doc.put("namefirst", "George");
				doc.put("namelast",  "Lucchese");
				collection.insert(doc);
			}*/
			DBCursor cur = collection.find(query.get());
			while(cur.hasNext()) {
				DBObject currentDoc = cur.next();
	            System.out.println(currentDoc);
				//collection.remove(currentDoc);
	        }
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DBVisgo(String url,String username, char[] password) throws UnknownHostException, MongoException{
		mongo = new Mongo(url);
		database = mongo.getDB("visgo");
		database.authenticate(username, password);
	}
	
	public Document getDocumentByGoogleId(String gid){
		DBCollection collection = database.getCollection("documents");
		collection.find(new BasicDBObject("mGoogleId",gid));
		
		return null;
	}
}
