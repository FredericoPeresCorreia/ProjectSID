
package Alertas;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import Anomalias.InsereMedicoesNoMySql;
import Anomalias.Medicao;
import conn.ConnectToMySql;
import conn.MongoParaMysql;


/**
 * 
 * General Alerta class - auxiliary class that adds values to the Temperature
 * and Humidity buffers, gets the values from the Sistema table(the values that
 * the user defined for the system) and sends the Alerta to the table.
 *
 */
public class Alerta {

	private Medicao medicao;
	private static HashMap<String, Double> valoresTabelaSistema;
	private static boolean primeiraMedicao = true;

	public Alerta(HashMap valoresTabelaSistema, Medicao medicao) {
		this.medicao = medicao;
	}

	private static LinkedList<Medicao> ultimosValoresTemperatura = new LinkedList<Medicao>();
	private static LinkedList<Medicao> ultimosValoresHumidade = new LinkedList<Medicao>();
	private static String ultimaAlertaMovimento = null;
	private static String ultimaAlertaLuminosidade = null;
	private static Date fimRondaEmCurso = null;
	private static String ultimoAlarmeLimiteTemperatura = null;
	private static String ultimoAlarmeCrescimentoTemperatura = null;
	private static String ultimoAlarmeLimiteHumidade = null;
	private static String ultimoAlarmeCrescimentoHumidade = null;
	private static Connection conn;

	public static int irBuscarBuffersAlerta() {
		return valoresTabelaSistema.get("tamanhoDosBuffersAlerta").intValue();

	}

	/**
	 * The Alerta Buffer works in a FIFO way. If it's the first Medicao it calls a
	 * method to read from the Sistema table. If it's not checks if the buffer is
	 * full to check for Alertas and removes the first element(oldest element), if
	 * it's not full adds it to the buffer.
	 * 
	 * @param medicao Medicao to add to the buffers according to it's type.
	 */
	public static void adicionaValor(Medicao medicao) {
		if (primeiraMedicao) {
			buscarValoresTabelaSistema();
			primeiraMedicao = false;
		}
		if (medicao.getTipoMedicao().equals("tmp")) {
			if (ultimosValoresTemperatura.size() == irBuscarBuffersAlerta()) {
				ultimosValoresTemperatura.poll();
			} else {
				ultimosValoresTemperatura.addFirst(medicao);
			}
		} else if (medicao.getTipoMedicao().equals("hum")) {
			if (ultimosValoresHumidade.size() == irBuscarBuffersAlerta()) {
				ultimosValoresHumidade.poll();
			} else
				ultimosValoresHumidade.addFirst(medicao);
		}
	}

	public static Double buscarIntervaloEntreAlertas() {
		return valoresTabelaSistema.get("TempoEntreAlertas");
	}

	public static void buscarValoresTabelaSistema() {
		valoresTabelaSistema = MongoParaMysql.getValoresTabelaSistema();
	}

	public static Double getLimite(String tipoMedicao) {

		if (tipoMedicao.equals("tmp"))
			return valoresTabelaSistema.get("limiteTemperatura");
		else if (tipoMedicao.equals("hum"))
			return valoresTabelaSistema.get("limiteHumidade");
		return null;
	}

	/**
	 * Checks weather if an alert should be sent if there is one already of the same
	 * type sent before - for example if the user says he should receive an alert of
	 * the same type every 5 minutes, this methods checks if there is a new alert to
	 * be sent of an x type, if the last one received of the x type by the user was
	 * longer than 5 minutes ago then it will be sent another alert of the x type.
	 * 
	 * @param dataAntiga Older date to compare
	 * @param dataNova   Newer date to compare
	 * @return wether a new alert should be sent
	 */
	public static boolean verificarSeMandaAlerta(String dataAntiga, String dataNova) {
		try {
			if (dataAntiga == null) {
				return true;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd kk:mm:ss");
			Date dataAntigaFormatada = sdf.parse(dataAntiga);
			Date dataNovaFormatada = sdf.parse(dataNova);
			if (dataNovaFormatada.getTime() - dataAntigaFormatada.getTime() > buscarIntervaloEntreAlertas() * 60
					* 1000) {
				return true;
			}

		} catch (ParseException e) {
			System.err.println(
					"Could not parse the dates received. The second date to compare might be null OR their format is too broken for the SimpleDataFormat to handle.");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @return Last value in the buffer
	 */
	public static Medicao getUltimoValor(String tipoMedicao) {
		if (tipoMedicao.equals("tmp")) {
			if (ultimosValoresTemperatura.size() != 0) {
				return ultimosValoresTemperatura.getLast();
			}
			return null;
		} else if (tipoMedicao.equals("hum")) {
			if (ultimosValoresHumidade.size() != 0) {
				return ultimosValoresHumidade.getLast();
			}
			return null;
		}
		return null;
	}

	/**
	 * 
	 * @return First value in the buffer
	 */
	public static Medicao getPrimeiroValor(String tipoMedicao) {
		if (tipoMedicao.equals("tmp")) {
			if (ultimosValoresTemperatura.size() != 0) {
				return ultimosValoresTemperatura.getFirst();
			}
			return null;
		} else if (tipoMedicao.equals("hum")) {
			if (ultimosValoresHumidade.size() != 0) {
				return ultimosValoresHumidade.getFirst();
			}
			return null;
		}
		return null;
	}

	public static Double getCrescimentoInstantaneo(String tipoMedicao) {

		if (tipoMedicao.equals("tmp"))
			return valoresTabelaSistema.get("crescimentoInstantaneoTemperatura");
		else if (tipoMedicao.equals("hum"))
			return valoresTabelaSistema.get("crescimentoInstantaneoHumidade");
		else {
			System.err.println("Cannot get CrescimentoInstaneo");
			return null;
		}
	}

	public static Double getCrescimentoGradual(String tipoMedicao) {
		if (tipoMedicao.equals("tmp"))
			return valoresTabelaSistema.get("crescimentoGradualTemperatura");
		else if (tipoMedicao.equals("hum"))
			return valoresTabelaSistema.get("crescimentoGradualHumidade");
		else {
			System.err.println("Cannot get CrescimentoInstaneo");
			return null;
		}
	}

	/**
	 * Calls the SP responsible to write in the Alerta table the new alert.
	 * 
	 * @param descricao What the alert should be named
	 * @param medicao   The Medicao containing the value, type and date
	 * @param limite    The limit, in % or absolute, that triggered the alert
	 */
	public static void enviaAlerta(String descricao, Medicao medicao, String limite) {
		try {
			conn = ConnectToMySql.connect();
			Statement st = conn.createStatement();
			String Sqlcommando = "CALL InserirAlerta( '" + medicao.getDataHoraMedicao() + "','"
					+ medicao.getTipoMedicao() + "','" + medicao.getValorMedicao() + "','" + limite + "','" + descricao
					+ "',0,'');";

			ResultSet rs = st.executeQuery(Sqlcommando);
		} catch (SQLException e) {
			System.err.println("SP inserir alerta falhou ");
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("could not close MySQL connection");
				e.printStackTrace();
			}
		}
	}
	// TODO substituir e por e.print...

	public static String getUltimaDataMovimento() {
		return ultimaAlertaMovimento;
	}

	public static String getUltimaDataLuminosidade() {
		return ultimaAlertaLuminosidade;
	}

	public static void setUltimaDataMovimento(String ultimaData) {
		ultimaAlertaMovimento = ultimaData;
	}

	public static void setUltimaDataLuminosidade(String ultimaData) {
		ultimaAlertaLuminosidade = ultimaData;
	}

	public static Date getFimRondaEmCurso() {
		return fimRondaEmCurso;
	}

	public static void setFimRondaEmCurso(Date ultimaData) {
		fimRondaEmCurso = ultimaData;
	}

	public static String getUltimoAlarmeLimiteTemperatura() {
		return ultimoAlarmeLimiteTemperatura;
	}

	public static void setUltimoAlarmeLimiteTemperatura(String ultimoAlarmeLimiteTemperatura) {
		Alerta.ultimoAlarmeLimiteTemperatura = ultimoAlarmeLimiteTemperatura;
	}

	public static String getUltimoAlarmeCrescimentoTemperatura() {
		return ultimoAlarmeCrescimentoTemperatura;
	}

	public static void setUltimoAlarmeCrescimentoTemperatura(String ultimoAlarmeCrescimentoTemperatura) {
		Alerta.ultimoAlarmeCrescimentoTemperatura = ultimoAlarmeCrescimentoTemperatura;
	}

	public static String getUltimoAlarmeLimiteHumidade() {
		return ultimoAlarmeLimiteHumidade;
	}

	public static void setUltimoAlarmeLimiteHumidade(String ultimoAlarmeLimiteHumidade) {
		Alerta.ultimoAlarmeLimiteHumidade = ultimoAlarmeLimiteHumidade;
	}

	public static String getUltimoAlarmeCrescimentoHumidade() {
		return ultimoAlarmeCrescimentoHumidade;
	}

	public static void setUltimoAlarmeCrescimentoHumidade(String ultimoAlarmeCrescimentoHumidade) {
		Alerta.ultimoAlarmeCrescimentoHumidade = ultimoAlarmeCrescimentoHumidade;
	}
}