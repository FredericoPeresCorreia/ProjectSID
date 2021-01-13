	<?php
	$url="127.0.0.1";
	$database="g12_museum"; // Alterar nome da BD se necessario
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);
	// Alterar nome da tabela MedicoesSensores, nome dos campos DataHoraMedicao e ValorMedicao, e a sigla do tipo de sensor de temperatura ("TEM") se necessario
	$sql = "SELECT DataHoraMedicao,ValorMedicao from medicao_sensores where TipoSensor = 'tmp' AND DataHoraMedicao >= now() - interval 5 minute ORDER BY DataHoraMedicao ASC";
	$result = mysqli_query($conn, $sql);
	$response["medicoes"] = array();
	if ($result){
		
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
			
				// Alterar nome dos campos se necessario
				$ad["ValorMedicao"] = $r['ValorMedicao'];
				$ad["DataHoraMedicao"] = $r['DataHoraMedicao'];
				array_push($response["medicoes"], $ad);
			}
		}	
	}
	$json = json_encode($response["medicoes"]);
	echo $json;
	mysqli_close ($conn);