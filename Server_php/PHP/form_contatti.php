<?php
echo "ciao";
?>
<form action="check_contacts.php" method="POST">
	<input type="text" name="Numero">
	<input name="Contatti[]" type="checkbox" value="3486067125"/>3486067125
	<input name="Contatti[]" type="checkbox" value="3486068327"/>3486068327
	<input name="Contatti[]" type="checkbox" value="3486005930"/>3486005930
	<input type="submit">
</form>