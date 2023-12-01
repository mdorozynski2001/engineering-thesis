<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryTargetType']) && isset($_POST['InventoryID'])) {
    if ($db->dbConnect()) {
        $result = $db->getProductNumberInInventoryTarget($_POST['InventoryTargetType'], $_POST['InventoryID']);
        if ($result > 0) {
            echo $result;            
        } else {
            echo "Inventory target is empty";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>