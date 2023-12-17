<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryType']) && isset($_POST['InventoryName'])) {
    if ($db->dbConnect()) {
        $result = $db->getInventoryTargetData($_POST['InventoryType'], $_POST['InventoryName']);
        if ($result != NULL){
            $result =$result->fetch_assoc();
            $data = array();
            $data[] = $result;
            echo json_encode($data);
        }
        else echo "Inventory targed doesn't exist";  
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>