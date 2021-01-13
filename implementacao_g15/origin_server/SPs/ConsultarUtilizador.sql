DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `ConsultarUtilizador`(Email VARCHAR(100))
BEGIN
	SELECT user INTO @userId FROM (
		SELECT user, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
	SELECT default_role INTO @role FROM (
        SELECT default_role, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
	 IF(CURRENT_ROLE = 'chefe_seguranca' OR @role= 'chefe_seguranca') THEN
		SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Nomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Tipoo;
        SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cNomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cTipoo;
		IF (Email = 'NULL') Then
			SELECT * FROM utilizador where (TipoUtilizador = 'SEG' OR EmailUtilizador = @userId);
            INSERT INTO logutilizador VALUES ( DEFAULT, "NULL", "NULL", "NULL", @useriD ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		ELSEIF ( Email = @userId) THEN
			SELECT * FROM utilizador where (EmailUtilizador = @userId);
            INSERT INTO logutilizador VALUES ( DEFAULT, Email, @Nomee, @Tipoo, @userId ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		ELSE 
			SELECT * FROM utilizador where (EmailUtilizador = Email AND TipoUtilizador = 'SEG');
            INSERT INTO logutilizador VALUES ( DEFAULT, Email, @Nomee, @Tipoo, @userId ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
        END IF;
	ELSEIF(CURRENT_ROLE = 'seguranca' OR @role= 'seguranca') THEN 
		SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Nomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Tipoo;
        SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cNomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cTipoo;
		IF (Email = 'NULL') Then
			SELECT * FROM utilizador where (EmailUtilizador = @userId);
            INSERT INTO logutilizador VALUES ( DEFAULT, "NULL", "NULL", "NULL", @useriD ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		ELSEIF ( Email = @userId) THEN
			SELECT * FROM utilizador where (EmailUtilizador = @userId);
            INSERT INTO logutilizador VALUES ( DEFAULT, Email, @Nomee, @Tipoo, @userId ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		ELSE
			SELECT * FROM utilizador where (EmailUtilizador = @userId AND EmailUtilizador = Email);
        END IF;
	ELSEIF(CURRENT_ROLE = 'diretor' OR @role= 'diretor' OR CURRENT_ROLE = 'admnistrador' OR @role= 'admnistrador') THEN
		SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Nomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = Email INTO @Tipoo;
        SELECT NomeUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cNomee;
        SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = @userId INTO @cTipoo;
        IF (Email = 'NULL') Then
			SELECT * FROM utilizador;
             INSERT INTO logutilizador VALUES ( DEFAULT, "NULL", "NULL", "NULL", @useriD ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		ELSE
			SELECT * FROM utilizador where (EmailUtilizador = Email);
            INSERT INTO logutilizador VALUES ( DEFAULT, Email, @Nomee, @Tipoo, @userId ,@cNomee, @cTipoo, now(), 'CONSULTA',DEFAULT);
		END IF;
            
	END IF;
END$$

DELIMITER ;