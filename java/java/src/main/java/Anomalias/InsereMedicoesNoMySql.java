
package Anomalias;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import conn.ConnectToMySql;
import conn.MongoParaMysql;

/**
 * It only inserts Medicoes into MySQL.
 */
public class InsereMedicoesNoMySql {

	private Medicao medicao;
	private Connection conn;
	private String database_password;
	private String database_user;
	private String database_connection;
	private Statement s;
	private String Sqlcommando;

	public InsereMedicoesNoMySql(Medicao medicao) {
		this.medicao = medicao;

	}

	/**
	 * Connects to the MySQL database
	 */
	public void connect() {
		try {
			conn = ConnectToMySql.connect();
			s = conn.createStatement();
		} catch (Exception e) {
			System.err.println("Insere Medicoes No MySQL - Server down, unable to make the connection. " + e);
		}
	}

	/**
	 * Inserts through a Stored Procedure into the MySQL medicoes_sensores table or
	 * medicoes_sensores_anomalos the Medicoes received, if they are valid or not,
	 * respectively.
	 */
	public void insereMedicoesNoMySql() {
		connect();
		if (medicao.getValorAnomalia() == 0) {
			Sqlcommando = "CALL InserirMedicao('" + medicao.getValorMedicao() + "','" + medicao.getTipoMedicao() + "','"
					+ medicao.getDataHoraMedicao() + "');";
		} else if (medicao.getValorAnomalia() == 1) {

			Sqlcommando = "CALL InserirMedicaoAnomala('" + medicao.getValorMedicaoAnomalo() + "','"
					+ medicao.getTipoMedicao() + "','" + medicao.getDataHoraMedicao() + "');";
		}
		try {
			if (Sqlcommando != null) {
				conn.createStatement().executeQuery(Sqlcommando);
			} else
				System.err.println("SQL command is null");
		} catch (SQLException e) {
			System.err.println("The InserirMedicao didn't insert into Table. Possible reasons: fields are too long or fields are invalid " + e);
		} finally 	{
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("Could not close MySQL connection");
				e.printStackTrace();
			}
		}
	}

}