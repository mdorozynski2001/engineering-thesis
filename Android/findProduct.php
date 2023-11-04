<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['table']) && isset($_POST['barcode'])) {
    if ($db->dbConnect()) {
        $result = $db->findProduct($_POST['table'], $_POST['barcode']);
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