
package Alertas;

import java.util.LinkedList;

import Anomalias.Medicao;

/**
 * 
 * Checks the Alertas for temperature and humidity. Adds a new Medicao to its respective buffer and checks wether it's an alert or not in a round-robin fashion. 
 * If there is a value that it is an alarm, checks weather there is already an alert of the same type and not within the interval defined by the user to not receive any more alerts.
 *
 */
public class AvaliaAlertaVariacaoTemperaturaHumidade {

	private Medicao medicao;

	public AvaliaAlertaVariacaoTemperaturaHumidade(Medicao m) {
		this.medicao = m;
		Alerta.adicionaValor(m);
		avaliaAlerta();
	}

	/**
	 * Calls a method to insert into the Alert table if there is a value that is an alert and not within the interval defined by the user to not receive any more alerts.
	 * First it checks if the temperature or humidity is above their respective limits, then checks for their growth.
	 */
	private void avaliaAlerta() {
		Double limiteMaximo = Alerta.getLimite(medicao.getTipoMedicao());
		if (medicao.getValorMedicao() >= limiteMaximo) {
			if (medicao.getTipoMedicao().equals("hum")) {
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteHumidade(),
						medicao.getDataHoraMedicao())) {
					Alerta.enviaAlerta("Limite ultrapassado", medicao, Double.toString(limiteMaximo));
					Alerta.setUltimoAlarmeLimiteHumidade(medicao.getDataHoraMedicao());
				}
			}
			else if (medicao.getTipoMedicao().equals("tmp")) {
				if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteTemperatura(),
						medicao.getDataHoraMedicao())) {
					Alerta.enviaAlerta("Limite ultrapassado", medicao, Double.toString(limiteMaximo));
					Alerta.setUltimoAlarmeLimiteTemperatura(medicao.getDataHoraMedicao());
				}
			}
		} else {
			String tipoMedicaousada = null;
			if (medicao.getTipoMedicao().equals("tmp"))
				tipoMedicaousada = "tmp";
			else if (medicao.getTipoMedicao().equals("hum")) {
				tipoMedicaousada = "hum";

			}
			if (tipoMedicaousada != null) {
				boolean crescimentoInstantaneo = (medicao.getValorMedicao()
						/ Alerta.getUltimoValor(tipoMedicaousada).getValorMedicao()
						- 1 > Alerta.getCrescimentoInstantaneo(tipoMedicaousada));
				boolean crescimentoGradual = (medicao.getValorMedicao()
						/ Alerta.getPrimeiroValor(tipoMedicaousada).getValorMedicao()
						- 1 > Alerta.getCrescimentoGradual(tipoMedicaousada));

				if (crescimentoInstantaneo) {
					if (tipoMedicaousada.equals("hum")) {
						if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeCrescimentoHumidade(),
								medicao.getDataHoraMedicao())
								&& Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteHumidade(),
										medicao.getDataHoraMedicao())) {
							Alerta.enviaAlerta("Aumento Humidade", medicao,
									limiteEmPercentagem(Alerta.getCrescimentoInstantaneo(tipoMedicaousada)));
							Alerta.setUltimoAlarmeCrescimentoHumidade(medicao.getDataHoraMedicao());
						}
					} else if (tipoMedicaousada.equals("tmp")) {
						if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeCrescimentoTemperatura(),
								medicao.getDataHoraMedicao())
								&& Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteTemperatura(),
										medicao.getDataHoraMedicao())) {

							Alerta.enviaAlerta("Aumento Temperatura", medicao,
									limiteEmPercentagem(Alerta.getCrescimentoInstantaneo(tipoMedicaousada)));
							Alerta.setUltimoAlarmeCrescimentoTemperatura(medicao.getDataHoraMedicao());
						}
					}
				}
					else if (crescimentoGradual) {
						if (tipoMedicaousada.equals("hum")) {
							if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeCrescimentoHumidade(),
									medicao.getDataHoraMedicao())
									&& Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteHumidade(),
											medicao.getDataHoraMedicao())) {
								Alerta.enviaAlerta("Aumento Humidade", medicao,
										limiteEmPercentagem(Alerta.getCrescimentoGradual(tipoMedicaousada)));
								Alerta.setUltimoAlarmeCrescimentoHumidade(medicao.getDataHoraMedicao());
							}
						}
						else if (tipoMedicaousada.equals("tmp")) {
								if (Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeCrescimentoTemperatura(),
										medicao.getDataHoraMedicao())
										&& Alerta.verificarSeMandaAlerta(Alerta.getUltimoAlarmeLimiteTemperatura(),
												medicao.getDataHoraMedicao())) {
									Alerta.enviaAlerta("Aumento Temperatura", medicao,
											limiteEmPercentagem(Alerta.getCrescimentoGradual(tipoMedicaousada)));
									Alerta.setUltimoAlarmeCrescimentoTemperatura(medicao.getDataHoraMedicao());
								}
							}
						}
					}
				}
			}



	public String limiteEmPercentagem(double limite) {
		return Double.toString(limite * 100) + "%";

	}
}