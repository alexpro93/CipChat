<?php
$location=$_SERVER['HTTP_HOST'];
$db_host = "localhost";
$db_user = "root";
$db_password = "admin";
$db_database = "App";

//mi collego al DBMS
$connessione=mysql_connect($db_host,$db_user,$db_password);
//seleziono un database
mysql_select_db($db_database,$connessione);
if(isset($_POST['Numero'])){
	$num = $_POST['Numero'];
	$code = $_POST['Codice'];
	$query = "SELECT count(*) AS c FROM Activate WHERE Numero = '$num' AND Activation_code = '$code';";
	$ris = mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($conn));
	$row = mysql_fetch_array($ris);
	$row = $row['c'];
	if($row == 1){
		echo "OK";
	}else{
		echo "NO";
	}
}
mysql_close($connessione); //per chiudere la connessione al DBMS
?>