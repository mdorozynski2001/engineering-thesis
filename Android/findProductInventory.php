<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryTargetType']) && isset($_POST['InventoryTargetName']) && isset($_POST['barcode'])) {
    if ($db->dbConnect()) {
        $result = $db->findProductInventory($_POST['InventoryTargetType'], $_POST['InventoryTargetName'], $_POST['barcode']);
        if ($result != NULL){
            if ($result["barcode"] == $_POST['barcode']) {
                $data = array();
                $data[] = $result;
                echo json_encode($data);
            } else {
                echo "Barcode doesn't exist";
            }
        }
        else echo "Barcode doesn't exist";  
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>