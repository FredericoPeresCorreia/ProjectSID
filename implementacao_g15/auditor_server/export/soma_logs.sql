CREATE DEFINER=`root`@`localhost` PROCEDURE `soma_logs`()
BEGIN
	DECLARE valoresAlerta int;    
    DECLARE valoresCartao int;
    DECLARE valoresMedicao int;
    DECLARE valoresRonda int;
    DECLARE valoresRondaExtra int;
	DECLARE valoresSistema int;
    DECLARE valoresUtilizador int;
    
	SET valoresAlerta= (SELECT Count(*) from logalerta);
	SET valoresCartao= (SELECT Count(*) from logcartao);
	SET valoresMedicao= (SELECT Count(*) from logmedicaosensores);
	SET valoresRonda= (SELECT Count(*) from logronda);
	SET valoresRondaExtra= (SELECT Count(*) from logrondaextra);
    SET valoresSistema = (SELECT Count(*) from logsistema);
    SET valoresUtilizador = (SELECT Count(*) from logutilizador);
	
    SELECT valoresAlerta + ValoresCartao + ValoresMedicao + ValoresRonda + ValoresRondaExtra + ValoresSistema + ValoresUtilizador;
END