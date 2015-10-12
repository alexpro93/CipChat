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
IF(isset($_POST['Numero'])){
	$num = $_POST['Numero'];
	$contatti = $_POST['Contatti'];
	$i = 0;
	foreach ($contatti as $value) {
		$query = "SELECT count(*) AS c FROM Users WHERE Numero = '$value' AND Numero <> '$num';";
		$ris = mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($conn));
		$row = mysql_fetch_array($ris);
		if($row['c'] != 0){
			if($i == 0){
				echo $value;
				$i = 1;
			}else{
				echo "<br>" . $value;
			}
		}
	}
	
	mysql_close($connessione); //per chiudere la connessione al DBMS
}

?>