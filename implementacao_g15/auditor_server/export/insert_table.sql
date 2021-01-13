CREATE TABLE test (
	tableid varchar(20),
    field1 varchar(50),
    field2 varchar(50),
    field3 varchar(50),
    field4 varchar(50),
    field5 varchar(50), 
    field6 varchar(50), 
    field7 varchar(50), 
    field8 varchar(50), 
    field9 varchar(50), 
    field10 varchar(50),
	field11 varchar(50), 
    field12 varchar(50), 
    field13 varchar(50), 
    field14 varchar(50), 
    field15 varchar(50)
);


LOAD DATA INFILE 'C:\\Users\\Bernardo\\Desktop\\Iscte\\3Ano\\ProjetoSIDESII\\implementacao_g15\\auditor_server\\export\\teste.csv'
INTO TABLE test
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'  
LINES TERMINATED BY '\r\n'
(tableid, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11, field12, field13, field14, field15);