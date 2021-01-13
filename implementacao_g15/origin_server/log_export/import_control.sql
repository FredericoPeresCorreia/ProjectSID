--criar tabela temporária  para alojar o ficheiro de controlo
CREATE TABLE test_export_result (`tipo_operacao` int AUTO_INCREMENT, `estado` varchar(5), `data` TIMESTAMP, `ficheiro` varchar(100), `data_inicio` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `data_fim` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `numero_entradas` int, `numero_entradas_total` int, PRIMARY KEY( `tipo_operacao` ) ) ;
--Importar o ficheiro de controlo para a tabela temp
LOAD DATA INFILE 'C:/xampp/htdocs/ProjetoSIDESII/implementacao_g15/origin_server/log_export/ficheirocontrolo.csv'
INTO TABLE test_export_result
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n';


