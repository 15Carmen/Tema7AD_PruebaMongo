package org.example;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.BSONCallback;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;

public class Conexion {

    static MongoDatabase database;

    public static void conexionLocal(){
        String uri = "mongodb://localhost:27017/";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("PruebaMongo");
            MongoCollection<Document> collection = database.getCollection("Alumnos");
            Document doc = collection.find(eq("nombre", "Maria")).first();
            if (doc != null) {
                System.out.println(doc.toJson());
            } else {
                System.out.println("No matching documents found.");
            }
        }
    }

    public static void conexionRemota(){

        ConnectionString connectionString = new ConnectionString("mongodb+srv://cmartin:Marnu@cluster1.j1rulfz.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("PruebaMongo");
        MongoCollection<Document> collection = database.getCollection("Alumnos");
        Document doc = collection.find(eq("nombre", "Jesus")).first();
        if (doc != null) {
            System.out.println(doc.toJson());
        } else {
            System.out.println("No matching documents found.");
        }
    }

    public void insertar(){
        database.getCollection("Alumnos").insertOne(new Document()
                .append("nombre", "Paula")
                .append("apellidos", "Castillo"));
    }

    public void actualizar(String antes, String despues){
        Document query = new Document().append("nombre", antes);
        Bson updates = Updates.combine(
                Updates.set("runtime", 99),
                Updates.addToSet("nombre", despues),
                Updates.currentTimestamp("lastUpdated"));
        UpdateOptions options = new UpdateOptions().upsert(true);

        try{
            UpdateResult result = database.getCollection("Alumno").updateOne(query, updates, options);
            System.out.println("Modified document count: " + result.getModifiedCount());
            System.out.println("Upserted id: " + result.getUpsertedId());
        } catch (MongoException me) {
            System.err.println("Error al actualizar la tabla");
        }
    }
}
