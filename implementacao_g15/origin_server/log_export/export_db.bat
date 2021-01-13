@ECHO OFF
::Environment variables
set server=localhost
set db=museum
set table=test_export_result
set user=root
set pass=teste123

::Delete the old export file/files that are in the directory
IF EXIST ficheiro_exportacao.csv (
	del ficheiro_exportacao.csv
	ECHO FILE DELETED
)

::Import control line to the DB
mysql -u %user% -p%pass% %db% < "C:\xampp\htdocs\ProjetoSIDESII\implementacao_g15\origin_server\log_export\import_control.sql"
::Prints the updated Control Table
mysql -u %user% -p%pass% -h %server% -e "SELECT * FROM %table%;" %db%
::Calls Exportar SP
mysql -u %user% -p%pass% -h %server% -e "CALL exportar;" %db%
::Drop temporary table used in the import_controlo.sql
mysql -u %user% -p%pass% -h %server% -e "DROP TABLE IF EXISTS test_export_result;" %db%


PAUSE