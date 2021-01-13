	<?php
	$url="127.0.0.1";
	$database="g12_museum"; // Alterar nome da BD se necessario	
    $conn = mysqli_connect($url,$_POST['username'],$_POST['password'],$database);	
	// Alterar nome da tabela Alerta e nome do campo DataHoraMedicao se necessario
	$sql = "SELECT * from alerta where DATE(Alerta.DataHoraMedicao) = '" . $_POST['date'] . "';";
	$result = mysqli_query($conn, $sql);
	$response["avisos"] = array();
	if ($result){
		if (mysqli_num_rows($result)>0){
			while($r=mysqli_fetch_assoc($result)){
				$ad = array();
				// Alterar nome dos campos se necessario
				$ad["DataHoraMedicao"] = $r['DataHoraMedicao'];
				$ad["TipoSensor"] = $r['TipoSensor'];
				$ad["ValorMedicao"] = $r['ValorMedicao'];
				$ad["Limite"] = $r['Limite'];
				$ad["Descricao"] = $r['Descricao'];
				$ad["Controlo"] = $r['Controlo'];
				$ad["Extra"] = $r['Extra'];
				array_push($response["avisos"], $ad);
			}
		}	
	}
	$json = json_encode($response["avisos"]);
	echo $json;
	mysqli_close ($conn);