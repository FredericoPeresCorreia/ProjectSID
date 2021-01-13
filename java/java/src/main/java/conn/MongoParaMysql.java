
package conn;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import javax.swing.*;

import Alertas.Alerta;
import Alertas.AvaliaAlertaAssalto;
import Anomalias.AvaliaAnomaliasVariacao;
import Anomalias.Medicao;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import java.sql.*;

/**
 * Reads the parameters set by the client in the Sistema table. Connects to
 * Mongo, reads the new Medicoes there and sends them to be tested in the
 * Medicao and AvaliaAnomalia to see if they are valid.
 */
public class MongoParaMysql {
    private Connection conn;
    private Statement s;
    private ResultSet rs;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> mongocol;
    private String mongo_host;
    private String mongo_database;
    private String mongo_collection;
    private String SqlCommando;
    private static HashMap<String, Double> valoresTabelaSistema;
    private static String ultimaDataVálida;
    private HashMap<String, Medicao> valoresASerConferidos;
    private AvaliaAnomaliasVariacao avaliaAnomaliasTemperatura;
    private AvaliaAnomaliasVariacao avaliaAnomaliasHumidade;
    private Document ultimaMedicao;
    private final Bson lastFilter = new Document("_id", -1);

    /**
     * Calls the methods that set up what the class needs - a MySQL connections,
     * check the user's parameters and created the Anomalias Buffers. Constantly
     * checks for new values from Mongo.
     */
    private void run() {

        connectMongo();
        irBuscarDadosMysql();
        Alerta.buscarValoresTabelaSistema();
        criaBuffersAnomalia();
        setDataUltimaMedicao(LocalDateTime.now().toString());
        ultimaMedicao = getUltimoValor(); // Primeira medição do mongo

        while (true) {
            verificaValoresNovos();
        }

    }

    /**
     * Opens a new Mongo connection similar to the one done in CloudToMongo.
     */
    public void connectMongo() {
        Properties p = new Properties();
        try {
            p.load(new FileInputStream("cloudToMongo.ini"));
            mongo_host = p.getProperty("mongo_host");
            mongo_database = p.getProperty("mongo_database");
            mongo_collection = p.getProperty("mongo_collection");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        mongoClient = new MongoClient(new MongoClientURI(mongo_host));
        db = mongoClient.getDatabase(mongo_database);
        mongocol = db.getCollection(mongo_collection);
    }

    public static HashMap<String, Double> getValoresTabelaSistema() {
        if (valoresTabelaSistema == null)
            System.err.println("a tabela sistema esta vazia");
        return valoresTabelaSistema;
    }

    public static String getDataUltimaMedicao() {
        return ultimaDataVálida;
    }

    public static void setDataUltimaMedicao(String data) {
        ultimaDataVálida = data;
    }

    public static double getTempoLimiteMedicao() {
        return valoresTabelaSistema.get("TempoLimiteMedicao");
    }

    /**
     * Checks wether the last Medicao received is the same as the new one read - if
     * it is it waits the time defined by the user. If they are different it makes
     * them check for Anomalias - in the verificarAssalto() or it adds them to the
     * buffers, according to the type of sensor.
     */
    private void verificaValoresNovos() {
        Document novo = getUltimoValor();
        if (!ultimaMedicao.equals(novo)) {
            ultimaMedicao = novo;
            valoresASerConferidos = getValoresMedicao(ultimaMedicao);
            verificarAssalto();
            avaliaAnomaliasTemperatura.adicionarValores(valoresASerConferidos.get("tmp"));
            avaliaAnomaliasHumidade.adicionarValores(valoresASerConferidos.get("hum"));
            try {
                Thread.sleep(valoresTabelaSistema.get("IntervaloImportacaoMongo").intValue() * 1000);
            } catch (InterruptedException e) {
                System.err.println("Failed to sleep. ");
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(valoresTabelaSistema.get("IntervaloImportacaoMongo").intValue() * 1000);
            } catch (InterruptedException e) {
                System.err.println("Thread sleep was interrupted - outside of our control: ");
                e.printStackTrace();
            }
        }
    }

    /**
     * When the medicao object is created it checks if it's not an Anomalia. Calls
     * the AvaliaAlerta class to check weather the movimento and luminosidade values
     * received in a Medicao are an alert.
     */
    private void verificarAssalto() {
        Medicao movimento = valoresASerConferidos.get("mov");
        Medicao luminosidade = valoresASerConferidos.get("lum");
        new AvaliaAlertaAssalto(movimento, luminosidade, valoresTabelaSistema.get("luminosidadeLuzesDesligadas"));
    }

    /**
     * Creates the Temperatura and Humidity buffers <b>only</b> when the system is
     * started and only then.
     */
    private void criaBuffersAnomalia() {
        avaliaAnomaliasTemperatura = new AvaliaAnomaliasVariacao(
                valoresTabelaSistema.get("tamanhoDosBuffersAnomalia").intValue(),
                valoresTabelaSistema.get("variacaoAnomalaTemperatura"));
        avaliaAnomaliasHumidade = new AvaliaAnomaliasVariacao(
                valoresTabelaSistema.get("tamanhoDosBuffersAnomalia").intValue(),
                valoresTabelaSistema.get("variacaoAnomalaHumidade"));
    }

    /**
     * Connects to the MySQL database, gets the system values that the user defined
     * for the project and puts them in a HashMap to be used in the future. If the
     * table is empty, it calls an SP to fill it according to the default values.
     */
    private void irBuscarDadosMysql() {
        conn = ConnectToMySql.connect();
        try {
            s = conn.createStatement();
        } catch (SQLException e2) {
            System.err.println("Failed to connect to MySql. ");
            e2.printStackTrace();
        }
        valoresTabelaSistema = new HashMap<String, Double>();
        // TODO isto não lê vários valores da tabela sistema????
        SqlCommando = "SELECT * from sistema;";
        try {
            rs = s.executeQuery(SqlCommando);
            rs.next();
            valoresTabelaSistema.put("IntervaloImportacaoMongo", rs.getDouble("IntervaloImportacaoMongo"));
            valoresTabelaSistema.put("TempoLimiteMedicao", rs.getDouble("TempoLimiteMedicao"));
            valoresTabelaSistema.put("TempoEntreAlertas", rs.getDouble("TempoEntreAlertas"));
            valoresTabelaSistema.put("tamanhoDosBuffersAnomalia", rs.getDouble("tamanhoDosBuffersAnomalia"));
            valoresTabelaSistema.put("tamanhoDosBuffersAlerta", rs.getDouble("tamanhoDosBuffersAlerta"));
            valoresTabelaSistema.put("variacaoAnomalaTemperatura", rs.getDouble("variacaoAnomalaTemperatura"));
            valoresTabelaSistema.put("variacaoAnomalaHumidade", rs.getDouble("variacaoAnomalaHumidade"));
            valoresTabelaSistema.put("crescimentoInstantaneoTemperatura",
                    rs.getDouble("crescimentoInstantaneoTemperatura"));
            valoresTabelaSistema.put("crescimentoGradualTemperatura", rs.getDouble("crescimentoGradualTemperatura"));
            valoresTabelaSistema.put("crescimentoInstantaneoHumidade", rs.getDouble("crescimentoInstantaneoHumidade"));
            valoresTabelaSistema.put("crescimentoGradualHumidade", rs.getDouble("crescimentoGradualHumidade"));
            valoresTabelaSistema.put("luminosidadeLuzesDesligadas", rs.getDouble("luminosidadeLuzesDesligadas"));
            valoresTabelaSistema.put("limiteTemperatura", rs.getDouble("limiteTemperatura"));
            valoresTabelaSistema.put("limiteHumidade", rs.getDouble("limiteHumidade"));

        } catch (SQLException e) {
            try {
                rs = s.executeQuery("CALL InsereTabelaSistemaValoresDefault();");
                irBuscarDadosMysql();
            } catch (SQLException e1) {
                System.err.println(
                        "Could not call the SP InsereTabelaSistemaValoresDefault - maybe it is deleted or it is modified"
                                + e1);
            }
        }
        try {
            conn.close();
        } catch (SQLException e1) {
            System.err.println("Failed to close MySql connection. " + e1);
        }

    }

    private Document getUltimoValor() {
        List<Document> novosresultados = new ArrayList<Document>();
        mongocol.find().sort(lastFilter).limit(1).into(novosresultados);

        return novosresultados.get(0);
    }

    /**
     * Gets a message from the sensor converted already to the Document format and
     * splits it into Medicao objects, 1 for each type of sensor and puts them in an
     * HashMap.
     *
     * @param document Mongo Document that came from the MongoDB.
     * @return The hashmap containing a single String(the medicoes that we got in a
     * single date time) that are divided into Medicao objects, each one for
     * each type of sensor.
     */
    private HashMap<String, Medicao> getValoresMedicao(Document document) {

            String date_fixed;
            try {
                String[] date_split = document.getString("dat").split("/");
                date_fixed = date_split[2] + "-" + date_split[1] + "-" + date_split[0] + " "
                        + document.getString("tim");
            } catch (Exception e1) {
                date_fixed ="0000-00-00 00:00:00";
                System.err.println("A data recebida não está com um formato xx-yy-yyyy, convertida para null");
                e1.printStackTrace();
            }

            if (document.getString("tmp") == null || document.getString("hum") == null
                    || document.getString("cell") == null || document.getString("mov") == null)
                System.err.println(
                        "One of the fields came as null in  one or more of the following fields: lum, hum, cell, mov probably from MongoDB");
            //TODO 1 valor nulo destroi o programa
            Medicao medicaoTemperatura = new Medicao(document.getString("tmp"), "tmp", date_fixed);
            Medicao medicaoHumidade = new Medicao(document.getString("hum"), "hum", date_fixed);
            Medicao medicaoLuminosidade = new Medicao(document.getString("cell"), "lum", date_fixed);
            Medicao medicaoMovimento = new Medicao(document.getString("mov"), "mov", date_fixed);
            HashMap<String, Medicao> medicoes = new HashMap<String, Medicao>();
            medicoes.put("tmp", medicaoTemperatura);
            medicoes.put("hum", medicaoHumidade);
            medicoes.put("lum", medicaoLuminosidade);
            medicoes.put("mov", medicaoMovimento);
            return medicoes;


    }

    public static void main(String[] args) {
        MongoParaMysql mongoParaMysql = new MongoParaMysql();
        mongoParaMysql.run();
    }
}