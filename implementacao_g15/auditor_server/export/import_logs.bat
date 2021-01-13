@Echo off
:: Verifica se existe um ficheiro csv para importar
IF NOT EXIST *.csv (
  ECHO "Não tem ficheiro para importar"
  EXIT
)

:: Adiciona um ; ao úlitmo campo de cada linha do csv, pois sem ele a importação não funciona
powershell -command "(Get-Content C:\Users\Bernardo\Desktop\Iscte\3Ano\ProjetoSIDESII\implementacao_g15\auditor_server\export\teste.csv) -replace '\\N', '\"NULL\"' | Set-Content C:\Users\Bernardo\Desktop\Iscte\3Ano\ProjetoSIDESII\implementacao_g15\auditor_server\export\teste.csv"
mysql --user=root auditor -e "DROP TABLE test;"
:: Verifica quantas linhas já existem nas tabelas (pré-insert) e mete numa variável
mysql --user=root auditor -e "call soma_logs" > tempfile 
powershell -command "& {get-content tempfile |select-object -last 1}" > beforeImport
FOR /F "tokens=* USEBACKQ" %%F IN (`powershell -command "& {get-content tempfile |select-object -last 1}"`) DO ( SET /a valorAnterior=%%F )
del tempfile
echo "Numero De Linhas Antes da insercao: %valorAnterior%"

:: Importa a tabela para dentro do mysql e chama o sp de importação
mysql --user=root auditor < "C:\Users\Bernardo\Desktop\Iscte\3Ano\ProjetoSIDESII\implementacao_g15\auditor_server\export\insert_table.sql"
mysql --user=root auditor -e "call importacao" > tempfile
type tempfile
del tempfile

:: Verifica quantas linhas existem nas tabelas (pós-insert) e mete numa variável
mysql --user=root auditor -e "call soma_logs" > tempfile 
FOR /F "tokens=* USEBACKQ" %%F IN (`powershell -command "& {get-content tempfile | select-object -last 1}"`) DO ( SET /a valorSeguinte=%%F )
echo "Numero De Linhas Depois da insercao: %valorSeguinte%"
del tempfile

:: Verifica se o número de linhas é igual e escreve o ficheiro de controlo de acordo
set /a "ValorFinal=%valorSeguinte%-%valorAnterior%"
echo "Numero de logs novos: %ValorFinal%" 


:: Numero de linhas ficheiro controlo
mysql --user=root auditor -e "SELECT tableid FROM test WHERE concat('',test.tableid * 1) = test.tableid;" > tempfile 
FOR /F "tokens=* USEBACKQ" %%F IN (`powershell -command "& {get-content tempfile | select-object -last 1}"`) DO ( SET /a numeroImportacao=%%F)
echo "Numero da Importacao: %numeroImportacao%"
del tempfile

:: Data do Export
mysql --user=root auditor -e "SELECT field2 FROM test WHERE concat('',test.tableid * 1) = test.tableid;" > tempfile 
FOR /F "tokens=* USEBACKQ" %%F IN (`powershell -command "& {get-content tempfile | select-object -last 1}"`) DO ( SET dataFicheiro=%%F)
echo "Data: %dataFicheiro%"
del tempfile

:: Data do Export
mysql --user=root auditor -e "SELECT field5 FROM test WHERE concat('',test.tableid * 1) = test.tableid;" > tempfile 
FOR /F "tokens=* USEBACKQ" %%F IN (`powershell -command "& {get-content tempfile | select-object -last 1}"`) DO ( SET /a numeroTeorico =%%F)
echo "Numero Teorico: %numeroTeorico%"
del tempfile
mysql --user=root auditor -e "DROP TABLE test;"

:: 
::mysql --user=root auditor -e "DROP TABLE test;" 
IF /I "%ValorFinal%" EQU "%numeroTeorico%" (
  ECHO "%numeroImportacao%";"OK";"%dataFicheiro%";"controlo.txt";"";"";"%numeroTeorico%" > controlo.txt
) ELSE (
  ECHO "%numeroImportacao%";"NOK";"%dataFicheiro%";"controlo.txt";"";"";"%numeroTeorico%" > controlo.txt
)

:: Cleanup

del beforeImport