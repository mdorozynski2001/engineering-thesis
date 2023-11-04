<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['table']) && isset($_POST['barcode']) && isset($_POST['number'])) {
    if ($db->dbConnect()) {
        if ($db->correctProductNumber($_POST['table'], $_POST['barcode'], $_POST['number'])) {
            echo "Correction succeeded";
        } else {
            echo "Correction failed";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>
