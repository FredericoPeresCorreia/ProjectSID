<?php

   set_time_limit(60*30);

   /*********************************************Auxiliar Functions************************************************/

   //Auxiliar Function to Handle DB connections
   function connectToDB($server, $user, $pass, $db) {
      $conn = new mysqli($server, $user, $pass, $db);

      if ($conn->connect_error) {
         die("Connection failed: " . $conn->connect_error);
      }
      
      return $conn;
   }

   function getLastID($conn, $table) {
      $last_id = 0;
      $last_id_migrated = $conn->query("SELECT * FROM ". $table ." ORDER BY id DESC LIMIT 1");
      
      if ($last_id_migrated->num_rows > 0) {
         // output data of each row
         while($row = $last_id_migrated->fetch_assoc()) {
             echo "<i>[".$table."] Last Log migrated was: " . $row["id"] . "</i></br>";
             $last_id = $row["id"];
         }
      } else {
         echo "<i>[".$table."] This is the first migration </i></br>";
      }

      return $last_id;
   }

   function migrateLogData($conn1, $conn2) {
      echo "<b>Starting Log Migration...</b> </br><hr>";
      $rows_migrated = 0;

      //Migrate g12_logmedicao_sensores
      $new_logs = $conn1->query("SELECT * FROM g12_logmedicao_sensores WHERE id > " .getLastID($conn2, "g12_logmedicao_sensores"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_logmedicao_sensores] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_logmedicao_sensores VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["IDMedicaoAntigo"]. "', '" .$row["IDMedicalNovo"]. "', '" .$row["ValorMedicaoAnterior"]. "', '" .$row["ValorMedicaoNovo"]. "', '" .$row["TipoDeSensorAnterior"]. "', '" .$row["TipoDeSensorNovo"]. "', '" .$row["DataHoraMedicaoAnterior"]. "', '" .$row["DataHoraMedicaoNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_logmedicao_sensores] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_logmedicao_sensores] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_logmedicao_sensores] No logs to migrate.</i>";
      }

      echo "<hr>";
      //Migrate g12_logmedicao_sensores_anomalos
      $new_logs = $conn1->query("SELECT * FROM g12_logmedicao_sensores_anomalos WHERE id > " .getLastID($conn2, "g12_logmedicao_sensores_anomalos"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_logmedicao_sensores_anomalos] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_logmedicao_sensores_anomalos VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["IDMedicaoAntigo"]. "', '" .$row["IDMedicalNovo"]. "', '" .$row["ValorMedicaoAnterior"]. "', '" .$row["ValorMedicaoNovo"]. "', '" .$row["TipoDeSensorAnterior"]. "', '" .$row["TipoDeSensorNovo"]. "', '" .$row["DataHoraMedicaoAnterior"]. "', '" .$row["DataHoraMedicaoNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_logmedicao_sensores_anomalos] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_logmedicao_sensores_anomalos] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_logmedicao_sensores_anomalos] No logs to migrate.</i>";
      }

      echo "<hr>";
      //Migrate g12_logronda_extra
      $new_logs = $conn1->query("SELECT * FROM g12_logronda_extra WHERE id > " .getLastID($conn2, "g12_logronda_extra"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_logronda_extra] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_logronda_extra VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["dataHoraEntradaAntigo"]. "', '" .$row["dataHoraEntradaNovo"]. "', '" .$row["dataHoraSaidaAntigo"]. "', '" .$row["dataHoraSaidaNovo"]. "', '" .$row["EmailUtilizadorAntigo"]. "', '" .$row["EmailUtilizadorNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_logronda_extra] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_logronda_extra] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_logronda_extra] No logs to migrate.</i>";
      }

      echo "<hr>";
      //Migrate g12_logronda_planeada
      $new_logs = $conn1->query("SELECT * FROM g12_logronda_planeada WHERE id > " .getLastID($conn2, "g12_logronda_planeada"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_logronda_planeada] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_logronda_planeada VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["EmailUtilizadorAntigo"]. "', '" .$row["EmailUtilizadorNovo"]. "', '" .$row["DiaSemanaAntigo"]. "', '" .$row["DiaSemanaNovo"]. "', '" .$row["HoraRondaInicioAntigo"]. "', '" .$row["HoraRondaInicioNovo"]. "', '" .$row["HoraRondaSaidaAntigo"]. "', '" .$row["HoraRondaSaidaNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_logronda_planeada] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_logronda_planeada] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_logronda_planeada] No logs to migrate.</i>";
      }

      echo "<hr>";
      //Migrate g12_logsistema
      $new_logs = $conn1->query("SELECT * FROM g12_logsistema WHERE id > " .getLastID($conn2, "g12_logsistema"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_logsistema] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_logsistema VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["IDSistemaAntigo"]. "', '" .$row["IDSistemaNovo"]. "', '" .$row["IntervaloImportacaoMongoAntigo"]. "', '" .$row["IntervaloImportacaoMongoNovo"]. "', '" .$row["TempoLimiteMedicaoAntigo"]. "', '" .$row["TempoLimiteMedicaoNovo"]. "', '" .$row["TempoEntreAlertasAntigo"]. "', '" .$row["TempoEntreAlertasNovo"]. "', '" .$row["tamanhoDosBuffersAnomaliaAntigo"]. "', '" .$row["tamanhoDosBuffersAnomaliaNovo"]. "', '" .$row["tamanhoDosBuffersAlertaAntigo"]. "', '" .$row["tamanhoDosBuffersAlertaNovo"]. "', '" .$row["variacaoAnomalaTemperaturaAntigo"]. "', '" .$row["variacaoAnomalaTemperaturaNovo"]. "', '" .$row["variacaoAnomalaHumidadeAntigo"]. "', '" .$row["variacaoAnomalaHumidadeNovo"]. "', '" .$row["crescimentoInstantaneoTemperaturaAntigo"]. "', '" .$row["crescimentoInstantaneoTemperaturaNovo"]. "', '" .$row["crescimentoGradualTemperaturaAntigo"]. "', '" .$row["crescimentoGradualTemperaturaNovo"]. "', '" .$row["crescimentoInstantaneoHumidadeAntigo"]. "', '" .$row["crescimentoInstantaneoHumidadeNovo"]. "', '" .$row["crescimentoGradualHumidadeAntigo"]. "', '" .$row["crescimentoGradualHumidadeNovo"]. "', '" .$row["luminosidadeLuzesDesligadasAntigo"]. "', '" .$row["luminosidadeLuzesDesligadasNovo"]. "', '" .$row["limiteTemperaturaAntigo"]. "', '" .$row["limiteTemperaturaNovo"]. "', '" .$row["limiteHumidadeAntigo"]. "', '" .$row["limiteHumidadeNovo"]. "', '" .$row["periocidadeImportacaoExportacaoAuditorAntigo"]. "', '" .$row["periocidadeImportacaoExportacaoAuditorNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_logsistema] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_logsistema] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_logsistema] No logs to migrate.</i>";
      }

      echo "<hr>";
      //Migrate g12_loguser
      $new_logs = $conn1->query("SELECT * FROM g12_loguser WHERE id > " .getLastID($conn2, "g12_loguser"). ";");

      if($new_logs->num_rows > 0) {
         echo "<i>[g12_loguser] Successfully imported data from origin.</i></br></br>";

         while($row = $new_logs->fetch_assoc()) {
            $insert_query = "INSERT INTO g12_loguser VALUES ('" .$row["id"]. "', '" .$row["User"]. "', '" .$row["Operacao"]. "', '" .$row["Time"]. "', '" .$row["EmailAnterior"]. "', '" .$row["EmailNovo"]. "', '" .$row["NomeUtilizadorAnterior"]. "', '" .$row["NomeUtilizadorNovo"]. "', '" .$row["TipoUtilizadorAnterior"]. "', '" .$row["TipoUtilizadorNovo"]. "', '" .$row["MoradaAnterior"]. "', '" .$row["MoradaNovo"]. "')";
            $insert_new_log = $conn2->query($insert_query);

            //Check if log export insertion went well
            if ($insert_new_log === TRUE) {
               echo "<i>[g12_loguser] Successfully exported Log with id: " . $row["id"]. "</i></br>";
               $rows_migrated += 1;
            } else {
               echo "<i>[g12_loguser] Error exporting Log with id: " . $row["id"]. "</i></br>" . $conn2->error;
            }
         }
      } else {
         echo "<i>[g12_loguser] No logs to migrate.</i>";
      }

      echo "<hr>";
      echo "<i>Log Migration has finished.</i>";
      echo "</br></br><i>Number of rows migrated: ".$rows_migrated.".</i></br>";
   }

   /*********************************************** DB Migration logic ***********************************************/
   
   $starttime = microtime(true);

   //Open DB Connections
   $auditor_conn = connectToDB("localhost", "auditor@mail", "", "g12_auditor");
   $origin_conn = connectToDB("localhost", "auditor@mail", "", "g12_museum"); // Localhost [Origin]

   //Log Migration
   migrateLogData($origin_conn, $auditor_conn);

   //Close DB Connections
   $auditor_conn->close();
   $origin_conn->close();
   
   $endtime = microtime(true);

   echo "<i>Elapsed time: ".number_format(($endtime - $starttime), 0)." seconds.</i></br>";
?>