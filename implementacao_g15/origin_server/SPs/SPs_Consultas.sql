DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarAlertas`(idAlertaCheck int)
BEGIN
	IF ( idAlertaCheck = 'NULL') THEN
		SELECT * FROM alerta;
	ELSE
		SELECT * FROM alerta WHERE idalerta = idAlertaCheck;
	END IF;
END


CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarCartao`(mailUtilizador varchar(100))
BEGIN
	IF ( mailUtilizador = 'NULL') THEN
		SELECT * FROM cartao;
	ELSE
		SELECT * FROM cartao WHERE EmailUtilizador = mailUtilizador;
	END IF;
END


CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarMedicoesSensores`(idMedicaoCheck int)
BEGIN
	IF ( idMedicaoCheck = 'NULL') THEN
		SELECT * FROM medicaosensores;
	ELSE
		SELECT * FROM medicaosensores WHERE idMedicao = idMedicaoCheck;
	END IF;
END


CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarRonda`(DiaSemanaCheck varchar(20), HoraRonda time)
BEGIN
IF ( DiaSemanaCheck = 'NULL' AND HoraRonda = 'NULL') THEN
		SELECT * FROM ronda_planeada;
	ELSE
		SELECT * FROM ronda_planeada WHERE DiaSemana = DiaSemanaCheck AND HoraRondaInicio = HoraRonda;
	END IF;
END


CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarRondaExtra`(mailUtilizador varchar(100))
BEGIN
	IF ( mailUtilizador = 'NULL') THEN
		SELECT * FROM rondaextra;
	ELSE
		SELECT * FROM rondaextra WHERE EmailUtilizador = mailUtilizador;
	END IF;
END


CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarSistema`(idSistemaCheck int)
BEGIN
	IF ( idSistemaCheck = 'NULL') THEN
		SELECT * FROM sistema;
	ELSE
		SELECT * FROM sistema WHERE IDSistema = idSistemaCheck;
	END IF;
END//

DELIMITER ;