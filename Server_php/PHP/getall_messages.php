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
	$last = $_POST['Ultimo'];

	$query = "SELECT id, Mittente, Dataora, Testo, 1 AS Ricevuto FROM Messages WHERE Destinatario = '$num' AND id > $last UNION SELECT id, Mittente, Dataora, Testo, 0 AS Ricevuto FROM Messages WHERE Mittente = '$num' AND id > $last ORDER BY Dataora;";
	$ris = mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($conn));
	while($row = mysql_fetch_array($ris)){
		$time = 1000 * strtotime($row['Dataora']);
		echo $row['id'] . "#" . $row['Mittente'] . "#" . $time . "#" . $row['Testo'] . "#" . $row['Ricevuto'] . "<br>";
	}
	
	mysql_close($connessione); //per chiudere la connessione al DBMS
}

?>