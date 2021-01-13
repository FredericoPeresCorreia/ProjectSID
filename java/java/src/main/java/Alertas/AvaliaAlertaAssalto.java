
package Alertas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Anomalias.InsereMedicoesNoMySql;
import Anomalias.Medicao;
import conn.ConnectToMySql;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * 
 * Checks weather a Movimento or Luminosidade Medicao is an alert or not. This
 * Medicoes have been checked before to see if they are valid or not. It this
 * verification, it the value is an alert, it checks if there is a Ronda
 * happening and if their is, it doesn't send an alert.
 */
public class AvaliaAlertaAssalto {

	private Medicao movimento;
	private Medicao luminosidade;
	private Connection conn;
	private String timestampUsedInRonda;
	private Double luminosidadeLuzEscuro;
	private String tipoAlerta;

	/**
	 * 
	 * Calls the method to checks weather there is an alert or not.
	 * 
	 * @param movimento             Medicao movimento
	 * @param luminosidade          Medicao luminosidade
	 * @param luminosidadeLuzEscuro Limit to the luminosidade value
	 */
	public AvaliaAlertaAssalto(Medicao movimento, Medicao luminosidade, Double luminosidadeLuzEscuro) {

		this.movimento = movimento;
		this.luminosidade = luminosidade;
		this.luminosidadeLuzEscuro = luminosidadeLuzEscuro;
		timestampUsedInRonda = movimento.getDataHoraMedicao();
		if (existeAlerta())
			insereTabelaAlerta();

	}

	public void connectMysqlAssalto() {
		conn = ConnectToMySql.connect();
	}

	public boolean existeAlerta() {
		return valorEAlerta() && !existeRonda();
	}

	/**
	 * Checks weather a Medicao value surpasses the limits(in the luminosidade case) or it's an 1 (there is moviment) 
	 * @return A boolean saying wether it should be an alert or not according to its value
	 */
	public boolean valorEAlerta() {
		// nao sao anomalos
		new InsereMedicoesNoMySql(movimento).insereMedicoesNoMySql();
		new InsereMedicoesNoMySql(luminosidade).insereMedicoesNoMySql();
		if (movimento.isAnomalo() && luminosidade.isAnomalo()) {
			return false;
		} else if (movimento.isAnomalo()) {
			if (luminosidade.getValorMedicao() > luminosidadeLuzEscuro) {
				tipoAlerta = "lum";
				return true;
			}
		} else if (luminosidade.isAnomalo()) {
			if (movimento.getValorMedicao() == 1.00) {
				tipoAlerta = "mov";
				return true;
			}
		} else if (luminosidade.getValorMedicao() > luminosidadeLuzEscuro && movimento.getValorMedicao() == 1.00) {
			tipoAlerta = "both";
			return true;
		} else if (luminosidade.getValorMedicao() > luminosidadeLuzEscuro) {
			tipoAlerta = "lum";
			return true;
		} else if (movimento.getValorMedicao() == 1.00) {
			tipoAlerta = "mov";
			return true;
		}
		return false;

	}

	/**
	 * Inserts into the tabela Alerta - according to the type of alert, it checks wether the last alert of that type was send was in an interval superior to what the user wanted.
	 * If it is not, it will not send an Alerta - it will not insert into the table and the Android application won't catch it. 
	 * If it sends an alert, the variable responsible for keeping track of the last time an alert of type x was send gets reseted.
	 */
	public void insereTabelaAlerta() {
		try {

			if (tipoAlerta.equals("mov")) {
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimaDataMovimento(), movimento.getDataHoraMedicao())) {
					Alerta.setUltimaDataMovimento(movimento.getDataHoraMedicao());
					Alerta.enviaAlerta("Possivel Assalto", movimento, "1");
				}
			} else if (tipoAlerta.equals("lum")) {
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimaDataLuminosidade(),
						luminosidade.getDataHoraMedicao())) {
					Alerta.setUltimaDataLuminosidade(luminosidade.getDataHoraMedicao());
					Alerta.enviaAlerta("Possivel Assalto", luminosidade, Double.toString(luminosidadeLuzEscuro));
				}
			} else if (tipoAlerta.equals("both")) {
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimaDataLuminosidade(),
						luminosidade.getDataHoraMedicao())) {
					Alerta.setUltimaDataLuminosidade(luminosidade.getDataHoraMedicao());
					Alerta.enviaAlerta("Possivel Assalto", luminosidade, Double.toString(luminosidadeLuzEscuro));
				}
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimaDataMovimento(), movimento.getDataHoraMedicao())) {
					Alerta.setUltimaDataMovimento(movimento.getDataHoraMedicao());
					Alerta.enviaAlerta("Possivel Assalto", movimento, "1");
				}

			}

		} catch (Exception e) {
			System.err.println("Enviar alerta falhou ou tipoAlerta não foi definido. TipoAlerta: " + tipoAlerta);
			e.printStackTrace();
		}
	}

	/**
	 * Checks wether there is a Ronda happening or not. If a ronda happens for more than 48h it will not work. 
	 * It will call a Stored Procedure to check in the medicao_sensores and medicao_sensores_anomalos if there is a ronda. 
	 * If there is a ronda happening a variable gets updated and it will not be check again until the current one finishes.
	 * @return wether a Ronda exists or not.
	 */
	public boolean existeRonda() {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
			Date parsedDate = dateFormat.parse(movimento.getDataHoraMedicao());
			if (Alerta.getFimRondaEmCurso() != null) {
				Date fimRondaEmCurso = dateFormat.parse(Alerta.getFimRondaEmCurso().toString());
				if (fimRondaEmCurso.after(parsedDate)) {
					return true;
				}
			}

		} catch (ParseException e1) {
			System.err.println("Could not parse the date from Movimento. Data Movimento:  "
					+ movimento.getDataHoraMedicao() + " " + e1);
		}

		// isto vai ter um problema que eu não consigo pensar nele, mas se rondas
		// tiverem mais de 24h rip(nao importante)
		// e se o cliente quiser aceitar tudo no medicoes_sensores e se o sensor estiver
		// avariado rip tambem, ou seja se sensor estiver a mandar data errada e valor
		// certo e só assim por acaso é um incendio rip
		// Rondas planeadas com mais de 24h vai ser um grande rip, espero que nunca
		// aconteca
		Statement st;
		try {
			connectMysqlAssalto();
			st = conn.createStatement();
			String Sqlcommando = "CALL VerificaSeExisteRonda('" + timestampUsedInRonda + "')";
			ResultSet rs = st.executeQuery(Sqlcommando);
			rs.next();
			Date result = rs.getTimestamp("fimRondaActual");
			conn.close();
			if (result != null) {
				Alerta.setFimRondaEmCurso(result);
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			System.err.println("SP VerificaSeExisteRonda failed. " + e);
		}
		return false;

	}

}