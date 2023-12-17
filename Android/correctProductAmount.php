<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryID']) && isset($_POST['product_id']) && isset($_POST['amount'])) {
    if ($db->dbConnect()) {
        if ($db->correctProductAmount($_POST['InventoryID'], $_POST['product_id'], $_POST['amount'])) {
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
