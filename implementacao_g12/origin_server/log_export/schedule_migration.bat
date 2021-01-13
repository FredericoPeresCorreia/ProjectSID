@ECHO OFF

mysql -u auditor@mail --password="" -e "SELECT sistema.PeriocidadeImportacaoExportacaoAuditor FROM sistema WHERE IDSistema = 1;" g12_museum > temp.txt

more +1 <temp.txt >val_temp.txt

set /P p= < val_temp.txt

del "temp.txt"
del "val_temp.txt"

schtasks /create /sc minute /mo %p% /tn "Log Migration" /tr C:\xampp\htdocs\ProjetoSIDESII\implementacao_g12\origin_server\log_export\.migrate.bat

ECHO Log migration scheduled to happen every %p% minutes.

PAUSE