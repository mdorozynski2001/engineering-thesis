<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryTargetType']) && isset($_POST['InventoryID']) && isset($_POST['barcode'])&& isset($_POST['productInfo'])) {
    if ($db->dbConnect()) {
        $result = $db->findProductInventory($_POST['InventoryTargetType'], $_POST['InventoryID'], $_POST['barcode'], $_POST['productInfo']);
        if ($result != NULL){
            if ($result[$_POST['productInfo']] ==$_POST['barcode']) {
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
