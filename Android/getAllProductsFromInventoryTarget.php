<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['InventoryType']) && isset($_POST['InventoryName'])) {
    if ($db->dbConnect()) {
        $result = $db->getProductsFromInventoryTarget($_POST['InventoryType'], $_POST['InventoryName']);
        if ($result->num_rows > 0) {
            $data = array();
    
            while ($row = $result->fetch_assoc()) {
                $data[] = $row;
            }
            echo json_encode($data);
        } else {
            echo "Barcode doesn't exist";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>