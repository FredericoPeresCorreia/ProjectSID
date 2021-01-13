DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `AlterarUtilizador`(Mail VARCHAR(100), nEmail VARCHAR(100),  nNome VARCHAR(200), nTipo VARCHAR(3), nPass VARCHAR(10))
BEGIN
	SELECT user INTO @userId FROM (
		SELECT user, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
	SELECT default_role INTO @role FROM (
        SELECT default_role, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
    IF(CURRENT_ROLE = 'seguranca' OR @role= 'seguranca') THEN 
		IF(Mail = @userID) THEN
			IF(NOT(nNome = 'NULL')) THEN
				UPDATE utilizador
                SET NomeUtilizador = nNome WHERE EmailUtilizador = @userId;
			END IF;
            IF(NOT(nPass = 'NULL')) THEN
				UPDATE utilizador
                SET utilizador.Password = nPass  WHERE EmailUtilizador = @userId;
                SET @sql := CONCAT('ALTER USER ','''', Mail,'''','@localhost', ' IDENTIFIED BY ', '''', nPass,'''');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
			END IF;
            IF(NOT(nEmail = 'NULL')) THEN
				UPDATE utilizador
                SET EmailUtilizador = nEmail WHERE EmailUtilizador = @userId;
                SET @sql := CONCAT('FLUSH PRIVILEGES');
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
                SET @sql := CONCAT('RENAME USER ', '''', Mail,'''', '@localhost',' TO ','''', nEmail,'''','@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
			END IF;
        
        END IF;
		
    ELSEIF(CURRENT_ROLE = 'chefe_seguranca' OR @role= 'chefe_seguranca') THEN 
		IF(Mail = @userID) THEN
			IF(NOT(nNome = 'NULL')) THEN
				UPDATE utilizador
                SET NomeUtilizador = nNome WHERE EmailUtilizador = @userId;
			END IF;
            IF(NOT(nPass = 'NULL')) THEN
				UPDATE utilizador
                SET utilizador.Password = nPass  WHERE EmailUtilizador = @userId;
                SET @sql := CONCAT('ALTER USER ','''', Mail,'''','@localhost', ' IDENTIFIED BY ', '''', nPass,'''');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
			END IF;
            IF(NOT(nEmail = 'NULL')) THEN
				UPDATE utilizador
                SET EmailUtilizador = nEmail WHERE EmailUtilizador = @userId;
                SET @sql := CONCAT('FLUSH PRIVILEGES');
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
                SET @sql := CONCAT('RENAME USER ', '''', Mail,'''', '@localhost',' TO ','''', nEmail,'''','@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
			END IF;
        ELSE
			SELECT TipoUtilizador FROM utilizador WHERE EmailUtilizador = Mail INTO @Tipppo;
            IF (@Tipppo = 'SEG') THEN
				IF(NOT(nNome = 'NULL')) THEN
					UPDATE utilizador
					SET NomeUtilizador = nNome WHERE EmailUtilizador = Mail;
				END IF;
				IF(NOT(nPass = 'NULL')) THEN
					UPDATE utilizador
					SET utilizador.Password = nPass  WHERE EmailUtilizador = Mail;
					SET @sql := CONCAT('ALTER USER ','''', Mail,'''','@localhost', ' IDENTIFIED BY ', '''', nPass,'''');
					PREPARE stmt FROM @sql;
					EXECUTE stmt;
				END IF;
				IF(NOT(nEmail = 'NULL')) THEN
					UPDATE utilizador
					SET EmailUtilizador = nEmail WHERE EmailUtilizador = Mail;
					SET @sql := CONCAT('FLUSH PRIVILEGES');
					PREPARE stmt FROM @sql;
					EXECUTE stmt;
					SET @sql := CONCAT('RENAME USER ', '''', Mail,'''', '@localhost',' TO ','''', nEmail,'''','@localhost');
					PREPARE stmt FROM @sql;
					EXECUTE stmt;
				END IF;
			END IF;	
        END IF;
	ELSE 
		IF(NOT(nNome = 'NULL')) THEN
				UPDATE utilizador
                SET NomeUtilizador = nNome WHERE EmailUtilizador = Mail;
		END IF;
		IF(NOT(nPass = 'NULL')) THEN
			UPDATE utilizador
			SET utilizador.Password = nPass  WHERE EmailUtilizador = Mail;
			SET @sql := CONCAT('ALTER USER ','''', Mail,'''','@localhost', ' IDENTIFIED BY ', '''', nPass,'''');
			PREPARE stmt FROM @sql;
			EXECUTE stmt;
		END IF;
        IF(NOT(nTipo = 'NULL')) THEN
			IF(nTipo = 'SEG') THEN
				UPDATE utilizador
				SET TipoUtilizador = nTipo WHERE EmailUtilizador = Mail;
				SET @sql := CONCAT('GRANT seguranca TO ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE seguranca FOR ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
			ELSEIF(nTipo = 'CSG') THEN
				UPDATE utilizador
				SET TipoUtilizador = nTipo WHERE EmailUtilizador = Mail;
				SET @sql := CONCAT('GRANT chefe_seguranca TO ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE chefe_seguranca FOR ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
			ELSEIF(nTipo = 'AUD') THEN
				UPDATE utilizador
				SET TipoUtilizador = nTipo WHERE EmailUtilizador = Mail;
				SET @sql := CONCAT('GRANT auditor TO ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE auditor FOR ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
			ELSEIF(nTipo = 'ADM') THEN
				UPDATE utilizador
				SET TipoUtilizador = nTipo WHERE EmailUtilizador = Mail;
				SET @sql := CONCAT('GRANT admnistrador TO ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE admnistrador FOR ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
			ELSEIF(nTipo = 'DIR') THEN
				UPDATE utilizador
				SET TipoUtilizador = nTipo WHERE EmailUtilizador = Mail;
				SET @sql := CONCAT('GRANT diretor TO ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE diretor FOR ', '''', Mail,'''', '@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
			END IF;
		END IF;
		IF(NOT(nEmail = 'NULL')) THEN
				UPDATE utilizador
                SET EmailUtilizador = nEmail WHERE EmailUtilizador = Mail;
                SET @sql := CONCAT('FLUSH PRIVILEGES');
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
                SET @sql := CONCAT('RENAME USER ', '''', Mail,'''', '@localhost',' TO ','''', nEmail,'''','@localhost');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
		END IF;
        
    END IF;

END$$

DELIMITER ;