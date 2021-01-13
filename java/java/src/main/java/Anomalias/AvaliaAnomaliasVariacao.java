

package Anomalias;

import java.util.ArrayList;
import java.util.List;

import Alertas.Alerta;
import Alertas.AvaliaAlertaAssalto;
import Alertas.AvaliaAlertaVariacaoTemperaturaHumidade;

/**
 * 
 * In charge of finding out if a temperature or humidity measurement(the size is variable according to the users needs) is valid. 
 * If it's valid inserts into the MySQL database and checks for alarms. If it's not valid, it only inserts into the MySQL database.
 *
 */

//TODO verificar no java tambem se buffers são inferiores a 2.
public class AvaliaAnomaliasVariacao {
	private int tamanhoBuffer;
	private Double ultimaMedicaoValida = null;
	private double variacaoMaxima;
	private ArrayList<Medicao> medicoes;

	public AvaliaAnomaliasVariacao(int tamanhoBuffer, double variacaoMaxima) {
		this.tamanhoBuffer = tamanhoBuffer;
		this.variacaoMaxima = variacaoMaxima;
		medicoes = new ArrayList<Medicao>();
	}

	/**
	 * Tests a certaint number of Medicoes to check if it's valid - the definition of valid is according to what the user wants. 
	 * If the last one is invalid it <b>WILL NOT</b> appear as invalid because it can't compare to the future Medicoes.
	 * If it's valid it inserts into the MySQL database and checks for Alertas.
	 * If it's invalid it only inserts into the MySQL database in a separate table. 
	 * @param lista A certaint number of Medicoes ready to be tested for its Anomalias
	 */
	private void testaAnomalia(List<Medicao> lista) {
		boolean anomalia = false;
		// Caso seja o primeiro buffer em verificação, ele compara o primeiro valor com
		// ele mesmo dado que não há um ultimo valor valido.
		if (ultimaMedicaoValida == null) {
			ultimaMedicaoValida = lista.get(0).getValorMedicao();
		}

		// Vai verificar cada valor do array
		for (int i = 0; i < lista.size(); i++) {
			anomalia = false;
			// Caso este valor tenha um crescimento acima do normal relativamente ao ultimo
			// valor valido, entra porque pode ser uma possivel anomalia
			if (Math.abs((lista.get(i).getValorMedicao() / ultimaMedicaoValida) - 1) >= variacaoMaxima) {
				int j = i + 1;

				while ((j < lista.size()) && !anomalia) {

					if (Math.abs((lista.get(j - 1).getValorMedicao() / lista.get(j).getValorMedicao())
							- 1.00) >= variacaoMaxima) {
						
						lista.get(i).marcarComoAnomalia();
						anomalia = true;
						new InsereMedicoesNoMySql(lista.get(i)).insereMedicoesNoMySql();
						break;
					}

					j++;

				}

				if (!anomalia) {
					ultimaMedicaoValida = lista.get(i).getValorMedicao();
					avaliaPossivelAlertaEInsereMedicaoNoMysql(lista.get(i));
				}

			} else {
				ultimaMedicaoValida = lista.get(i).getValorMedicao();
				avaliaPossivelAlertaEInsereMedicaoNoMysql(lista.get(i));
			}
		}
	}

	/**
	 * If it's not full it adds to the buffer. It it's full it starts to check for Anomalias and <b>clears</b> the buffer.
	 * @param medicao Medicao to be added to the buffer.
	 */
	public void adicionarValores(Medicao medicao) {
		if (medicoes.size() < tamanhoBuffer) {
			medicoes.add(medicao);
		}
		if (medicoes.size() == tamanhoBuffer) {
			testaAnomalia(medicoes);
			
			medicoes.clear();
		}
	}

	/**
	 * It inserts a valid Medicao into the MySQL table and checks for Alerta's.
	 * @param medicao Medicao ready to be inserted into the MySQL Medicoes table
	 */
	private void avaliaPossivelAlertaEInsereMedicaoNoMysql(Medicao medicao) {
		new InsereMedicoesNoMySql(medicao).insereMedicoesNoMySql();
		new AvaliaAlertaVariacaoTemperaturaHumidade(medicao);
	}

}
