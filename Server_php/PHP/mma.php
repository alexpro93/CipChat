<?php

$to      = 'alex.cavallin@gmail.com';
$subject = 'the subject';
$message = 'hello';
$headers = 'From: alex.cavallin@gmail.com';
echo $to;
echo $headers;
echo mail($to, $subject, $message, $headers);
echo date("H:i:s");
echo "<br>";

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
	$messaggio->AddAddress('alex.cavallin@gmail.com');
	$messaggio->AddReplyTo('alex.cavallin@gmail.com'); 
	$messaggio->Subject='Prova.';
	$messaggio->Body=stripslashes('Ciao!!!! funziona anche con gmail!!!!');

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

?>