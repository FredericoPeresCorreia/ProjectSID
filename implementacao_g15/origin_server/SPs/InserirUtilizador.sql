DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `InserirUtilizador`(Email VARCHAR(100),  Nome VARCHAR(200), Tipo VARCHAR(3), Pass VARCHAR(10))
BEGIN
	SELECT user INTO @userId FROM (
		SELECT user, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
	SELECT default_role INTO @role FROM (
        SELECT default_role, CONCAT(user, '@', host) as userhost FROM mysql.user) base
    WHERE userhost = USER();
	 IF(CURRENT_ROLE = 'chefe_seguranca' OR @role= 'chefe_seguranca') THEN
		IF(Tipo = 'SEG') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT seguranca TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE seguranca FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
		END IF;
    ELSEIF (CURRENT_ROLE = 'diretor' OR @role = 'diretor' OR CURRENT_ROLE = 'admnistrador' OR @role='admnistrador') THEN
		IF(Tipo = 'SEG') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT seguranca TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE seguranca FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
		ELSEIF(Tipo = 'CSG') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT chefe_seguranca TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE chefe_seguranca FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
		ELSEIF(Tipo = 'DIR') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT diretor TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE diretor FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
        ELSEIF(Tipo = 'AUD') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT auditor TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE auditor FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt; 
		ELSEIF(Tipo = 'ADM') THEN
			INSERT INTO utilizador VALUES (
				Email,
				Nome,
				Tipo,
				Pass);
				SET Email := CONCAT('''', Email, '''', '@', 'localhost'),
					Pass := CONCAT('''', Pass, '''');
				SET @sql := CONCAT('CREATE USER ', Email, ' IDENTIFIED BY ', Pass);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				SET @sql := CONCAT('GRANT admnistrador TO ', Email);
                PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
				SET @sql := CONCAT('SET DEFAULT ROLE admnistrador FOR ', Email);
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				DEALLOCATE PREPARE stmt;
		END IF;
    END IF;
END$$

DELIMITER ;