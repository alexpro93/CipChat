<?php
require("functions.php");
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
echo "passo";
IF(isset($_POST['Email'])){
	$nick = $_POST['Nickname'];
	$num = $_POST['Numero'];
	$reg = $_POST['Reg_id'];
	$email = $_POST['Email'];
	echo $num;
	//controllo se si è già registrato
	$query = "SELECT count(*) AS c FROM Users WHERE Email = '$email' AND Numero = '$num';";
	$ris = mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($conn));
	echo $query;
	$row = mysql_fetch_array($ris);
	$row = $row['c'];
	$code = generateRandomString();
	if($row == 1){
		$query1 = "UPDATE Users SET Nickname = '$nick' WHERE Email = '$email';";
		echo $query1 . "<br>";
		$query2 = "UPDATE Activate SET Activation_code = '$code' WHERE Numero = '$num';";
		echo $query2;
	}else{
		$query1 = "INSERT INTO Users VALUES('$nick','$num','$email');";
		echo $query1 . "<br>";
		$query2 = "INSERT INTO Activate VALUES('$num','$code');";
		echo $query2;
	}
	$query3 = "INSERT INTO Reg_ids VALUES('$num','$reg');";
	echo $query3 . "<br>";
	mysql_query($query1,$connessione) or die("Query1 fallita!" . mysql_error($connessione));
	mysql_query($query2,$connessione) or die("Query2 fallita!" . mysql_error($connessione));
	mysql_query($query3,$connessione) or die("Query3 fallita!" . mysql_error($connessione));
	
	require ("../PHPMailer/class.phpmailer.php");
    //istanziamo la classe
	$messaggio = new PHPmailer();
	$messaggio->IsSMTP();
	$messaggio->SMTPDebug = 1;
	$messaggio->SMTPAuth = true; 
	$messaggio->Host='smtp.gmail.com';
	$messaggio->Port = 465;
	$messaggio->SMTPSecure = 'ssl';
	$messaggio->Username = "alex.cavallin@gmail.com"; 
    $messaggio->Password = "alexcava93"; 
	//definiamo le intestazioni e il corpo del messaggio
	$messaggio->From='alex.cavallin@gmail.com';
	$messaggio->FromName = "Support Team CipChat";
	$messaggio->AddAddress($email);
	$messaggio->AddReplyTo('alex.cavallin@gmail.com'); 
	$messaggio->Subject='Attivazione CipChat';
	$messaggio->Body=stripslashes("Codice di attivazione: $code");

	//definiamo i comportamenti in caso di invio corretto 
	//o di errore
	if(!$messaggio->Send()){ 
	  echo $messaggio->ErrorInfo; 
	}else{ 
	  echo 'Email inviata correttamente!';
	}

	//chiudiamo la connessione
	$messaggio->SmtpClose();
	unset($messaggio);

	mysql_close($connessione); //per chiudere la connessione al DBMS
}

?>