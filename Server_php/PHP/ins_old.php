<?php

//imposto parametri per accedere al database
$location=$_SERVER['HTTP_HOST'];
$db_host = "localhost";
$db_user = "root";
$db_password = "admin";
$db_database = "App";

//mi collego al DBMS
$connessione=mysql_connect($db_host,$db_user,$db_password);
//seleziono un database
mysql_select_db($db_database,$connessione);
//mysql_close($connessione); per chiudere la connessione al DBMS
phpinfo();
IF(isset($_POST['mex'])){
	echo "INSERT INTO Messages(Testo) VALUES('". $_POST['Testo'] ."');";
	mysql_query("INSERT INTO pMessages(Testo) VALUES('". $_POST['Testo'] ."');",$connessione) or die("Query fallita!" . mysql_error($conn));

}
mysql_close($connessione);

?>