<?php
require("postGCM.php");
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

IF(isset($_POST['Testo'])){
	$query = "INSERT INTO Messages(Testo, Destinatario, Mittente, Dataora) VALUES('". $_POST['Testo'] ."','". $_POST['Destinatario'] ."','". $_POST['Mittente'] ."', NOW());";
	echo $query;
	mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($connessione));
	$id = mysql_insert_id($connessione);
	notification($id, $connessione);
}
mysql_close($connessione);
?>