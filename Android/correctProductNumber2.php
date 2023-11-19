<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryTargetType']) && isset($_POST['InventoryTargetName']) && isset($_POST['barcode']) && isset($_POST['number'])) {
    if ($db->dbConnect()) {
        if ($db->correctProductNumber2($_POST['InventoryTargetType'], $_POST['InventoryTargetName'], $_POST['barcode'], $_POST['number'])) {
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
