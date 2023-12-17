<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryTargetType']) && isset($_POST['InventoryID']) && isset($_POST['productInfo'])&& isset($_POST['productInfoType'])) {
    if ($db->dbConnect()) {
        $result = $db->findProduct($_POST['InventoryTargetType'], $_POST['InventoryID'], $_POST['productInfo'], $_POST['productInfoType']);
        if ($result != NULL){
            if ($result[$_POST['productInfoType']] ==$_POST['productInfo']) {
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
