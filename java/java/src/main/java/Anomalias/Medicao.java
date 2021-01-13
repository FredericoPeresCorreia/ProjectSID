
package Anomalias;

import conn.MongoParaMysql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

/**
 * 
 * Gets a Medicao from a single sensor, converts it to the correct time (Portugal's timezone) and check for Anomalias not related to variations - for example, checks if the valorMedicao is positive for some cases, if the date is valid, etc.
 *
 */
public class Medicao {

	private double valorMedicao;
	private String tipoMedicao;
	private String dataHoraMedicao;
	private boolean possivelAnomalia;
	private String valorMedicaoAnomalo;

	public String getDataHoraMedicao() {
		return dataHoraMedicao;
	}

	/**
	 * Checks wether the program is ready to deal with the type of sensor that it came in, then checks the type of the data received - a Double, if the date is valid(according to the user's needs) and in some cases if it's positive or is different from certaint values allowed
	 * .
	 * @param valorMedicao Value from the medicao that might or not be valid
	 * @param tipoMedicao Type of sensor used to read the value of the medicao; The type of sensor might be valid or not.
	 * @param dataHoraMedicao The time and date from when the Medicao was taken according or <b>not</b> the timezone
	 */
	public Medicao(String valorMedicao, String tipoMedicao, String dataHoraMedicao) {
		if (tipoMedicao.equals("tmp") || tipoMedicao.equals("hum") || tipoMedicao.equals("lum")
				|| tipoMedicao.equals("mov")) {
			this.dataHoraMedicao = this.dataHoraParaFormatoCerto(dataHoraMedicao);
			this.tipoMedicao = tipoMedicao;
			verificaSeEDouble(valorMedicao);
			verificaSeDataEValida(valorMedicao);
			if ((tipoMedicao.equals("hum") || tipoMedicao.equals("lum")) && !possivelAnomalia) {
				verificaSeEPositivo(valorMedicao);
			}
			if (tipoMedicao.equals("mov") && !possivelAnomalia) {
				verificaSeMovimentoEAnomalia(valorMedicao);
			}
			
		} else {
			System.err.println("TipoMedicao is invalid - only tmp, hum, lum and mov are allowed");
		}
	}


	/**
	 * Checks whether the valorMedicao already in double form(which is confirmed
	 * previously to be a double) is not negative.
	 * 
	 * @param valorMedicao
	 */
	private void verificaSeEPositivo(String valorMedicao) {
		if (this.valorMedicao < 0.00) {
			this.valorMedicaoAnomalo = valorMedicao;
			marcarComoAnomalia(valorMedicao);
		}
	}

	/**
	 * Checks weather the Movimento value is 0 or 1 - if it is it's marked as an Anomalia
	 * @param valorMedicao value that the sensor measured of the correct data type 
	 */
	private void verificaSeMovimentoEAnomalia(String valorMedicao) {
		if (Double.compare(this.valorMedicao, 0.00) != 0 && Double.compare(this.valorMedicao, 1.00) != 0) {
			marcarComoAnomalia(valorMedicao);
		}
	}

	
	private void verificaSeEDouble(String valorMedicao) {
		try {
			this.valorMedicao = Double.valueOf(valorMedicao);
		} catch (Exception e) {
			marcarComoAnomalia(valorMedicao);
		}
	}

	/**
	 * Checks wether the Medicao should be accepted according to its date - it compares it against the interval defined by the user to be valid, if it's not valid it's marked as an anomalia.
	 * @param valorMedicao Value from a Medicao, valid or not
	 */
	private void verificaSeDataEValida( String valorMedicao) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		try {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formatDateTime = now.format(formatter);
			Date dataAgora = dateFormat.parse(formatDateTime);
			Date parsedDate = dateFormat.parse(this.dataHoraMedicao);
			if (dataAgora.getTime() - parsedDate.getTime() > MongoParaMysql.getTempoLimiteMedicao() * 60 * 1000) {
				marcarComoAnomalia(valorMedicao);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void marcarComoAnomalia(String valorMedicao) {
		this.valorMedicaoAnomalo = valorMedicao;
		possivelAnomalia = true;
	}

	public void marcarComoAnomalia() {
		this.valorMedicaoAnomalo = String.valueOf(valorMedicao);
		possivelAnomalia = true;

	}
	public String getValorMedicaoAnomalo() {
		return valorMedicaoAnomalo;
	}

	public double getValorMedicao() {
		return valorMedicao;
	}

	public String getTipoMedicao() {
		return tipoMedicao;
	}

	public boolean isAnomalo() {
		return possivelAnomalia;
	}

	public void setPossivelAnomalia(boolean possivelAnomalia) {
		this.possivelAnomalia = possivelAnomalia;
	}

	public int getValorAnomalia() {
		if (isAnomalo()) {
			return 1;
		}
		return 0;
	}

	public void setValorMedicao(double valorMedicao) {
		this.valorMedicao = valorMedicao;
	}

	/**
	 * The data comes in as xx-xx-xx xx:xx:xx but without leading zeros. For example 1990-5-3 12:4:20 gets converted to 1990-05-03 12:04:20
	 * Due to the sensor's hour being an hour behind, it also add it to the correct date(GMT +1 +1 again).
	 * @param dataHoraMedicao Date with leading zeros and not checked for its timezone
	 * @return Date without leading zeros and in the correct timezone
	 */
	
	public String dataHoraParaFormatoCerto(String dataHoraMedicao) {

		String timezone = null;
		SimpleDateFormat timeFormatISO = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("cloudToMongo.ini"));
			String sensor = p.getProperty("cloud_topic");
			Date date = timeFormatISO.parse(dataHoraMedicao);
			Timestamp stamp = new Timestamp(date.getTime());
			SimpleDateFormat timeFormatISO2 = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
			if (sensor.equals("/sid_lab_2019_2") || sensor.equals("/sid_lab_2020"))
				timezone = "GMT+2";
			else if (sensor.equals("sid_exame"))
				timezone = "GMT+1";
			else System.err.println("Tipo de Sensor não definido - não é nem /sid_lab_2020 nem /sid_lab_2019_2 nem grupo12. Mudar código no dataHoraParaFormatoCerto na classe Medicao ");
			timeFormatISO2.setTimeZone(TimeZone.getTimeZone(timezone));
			return timeFormatISO2.format(stamp);
		} catch (ParseException e) {
			System.err.println("Unable to parse/find the date: " + dataHoraMedicao + " " + e);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find cloudToMongo.ini " + e);
		} catch (IOException e) {
			System.err.println("I/O exception when reading cloudToMongo.ini ");
		} catch (NullPointerException e){
			return null;
		}

		return null;
	}
}