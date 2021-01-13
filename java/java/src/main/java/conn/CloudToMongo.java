/**
 * Treats the connection aspect of the MySQL and MongoDB. Receives data and parses it correctly for other classes to deal with them.
 */
package conn;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.util.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;


/**
 * 
 * Reads the messages from the MQTT broker, "cleans"(formats) that message according to the sensor being used and inserts the message into the Mongo Client specified
 *
 */
public class CloudToMongo implements MqttCallback {
	MqttClient mqttclient;
	static MongoClient mongoClient;
	static DB db;
	static DBCollection mongocol;
	static String cloud_server = new String();
	static String cloud_topic = new String();
	static String mongo_host = new String();
	static String mongo_database = new String();
	static String mongo_collection = new String();

	/**
	 * 
	 * Reads from the cloudToMongo.ini and starts the connections to the Broker and Mongo Client
	 */
	public static void main(String[] args) {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("cloudToMongo.ini"));
			cloud_server = p.getProperty("cloud_server");
			cloud_topic = p.getProperty("cloud_topic");
			mongo_host = p.getProperty("mongo_host");
			mongo_database = p.getProperty("mongo_database");
			mongo_collection = p.getProperty("mongo_collection");
		} catch (Exception e) {

			System.err.println("Error reading CloudToMongo.ini file " );
			e.printStackTrace();
		}
		
		new CloudToMongo().connectMQTTBroker();
		new CloudToMongo().connectMongo();
	}

	/**
	 * Connects to the broker according to the cloud server and topic defined in the cloudToMongo.ini
	 */
	public void connectMQTTBroker() {
		int i;
		try {
			i = new Random().nextInt(100000);
			mqttclient = new MqttClient(cloud_server, "CloudToMongo_" + String.valueOf(i) + "_" + cloud_topic);
			mqttclient.connect();
			mqttclient.setCallback(this);
			mqttclient.subscribe(cloud_topic);
		} catch (MqttException e) {
			System.err.println("Could not connect to MQTT brocker. Cloud topic is: " + cloud_topic );
			e.printStackTrace();
		}
	}

	/**
	 * Connects to the Mongo Client according to the mongo host defined in the cloudToMongo.ini
	 */
	public void connectMongo() {
		try {
		mongoClient = new MongoClient(new MongoClientURI(mongo_host));
		db = mongoClient.getDB(mongo_database);
		mongocol = db.getCollection(mongo_collection);
		} catch (Exception e) {
			System.err.println("Failed to start MongoClient with Mongo host " + mongo_host );
			e.printStackTrace();
		}
	}

	//TODO clean the first nullpointer exception .check: https://stackoverflow.com/questions/26827020/nullpointerexception-while-subscribing-to-a-topic-on-mqttandroidclient
	public void messageArrived(String topic, MqttMessage c) throws Exception {
		try {
			DBObject document_json;

			document_json = (DBObject) JSON.parse(clean(c.toString()));
			mongocol.insert(document_json, WriteConcern.MAJORITY);
		} catch (Exception e) {
			System.err.println("Failed to get a Mongo Document " );
			e.printStackTrace();
		}
	}



	/**
	 * Cleans the message received from the sensor according to the sensor being used: for example if it has two double quotes 
	 * and it should only have one, it returns it with one double quote only
	 * @param message Message from sensor, unformatted
	 * @return Message from the sensor, clean and ready to be inserted into MongoDB
	 */
	public String clean(String message) {
			if(cloud_topic.equals("sid_exame"))
					return message.replaceAll("\", " , ",\""); 
			else if(cloud_topic.equals("/sid_lab_2019_2"))
				return message.replace("\"\"", "\",\"");
			else if(cloud_topic.equals("/sid_lab_2020"))
				return message.replace("\"\", ", "\",\"");
			else System.err.println("Não temos este tipo de sensor definido.");
		return null;

	}

	public void connectionLost(Throwable cause) {
		System.err.println("Connection to the MQTT broker lost because of:" + cause);
		
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		
	}

}