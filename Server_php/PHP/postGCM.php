<?php
 function notification($id, $connessione){
    $url = 'https://android.googleapis.com/gcm/send';
    echo $id;
    $query = "SELECT * FROM Messages WHERE id='$id';";
    $ris = mysql_query($query,$connessione) or die("Query fallita!" . mysql_error($connessione));
    $ris = mysql_fetch_array($ris);
    $value = $ris['Testo'];
    $dest = $ris['Destinatario'];
    $mitt = $ris['Mittente'];
    $time = $ris['Dataora'];
    $time = 1000 * strtotime($time);

    $query1 = "SELECT Reg_id FROM Reg_ids WHERE Numero='$dest';";
    $ris1 = mysql_query($query1,$connessione) or die("Query fallita!" . mysql_error($connessione));
    while ($row = mysql_fetch_assoc($ris1)) {
        $reg_id[]=$row['Reg_id'];
    }

    $registatoin_ids = $reg_id;
    $message = array("dataKey" => $value, "time" => $time, "mittente" => $mitt);

    $fields = array(
    'registration_ids' => $registatoin_ids,
    'data' => $message,
    );
 
    $headers = array(
    'Authorization: key=' . 'AIzaSyBimoSK9hXl-GKyu50rnYRBqjQss1gnmkY',
    'Content-Type: application/json'
    );
    // Open connection
    $ch = curl_init();
 
    // Set the url, number of POST vars, POST data
    curl_setopt($ch, CURLOPT_URL, $url);
 
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
 
    // Disabling SSL Certificate support temporarly
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
 
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
 
    // Execute post
    $result = curl_exec($ch);
    if ($result === FALSE) {
    die('Curl failed: ' . curl_error($ch));
    }
 
    // Close connection
    curl_close($ch);
    echo $result;
    echo $reg_id;
}
?>