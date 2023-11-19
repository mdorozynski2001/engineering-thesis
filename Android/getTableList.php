<?php
require "DataBase.php";
$db = new DataBase();
if (isset($_POST['table'])) {
    if ($db->dbConnect()) {
        $result = $db->getTableList($_POST['table']);
        if ($result->num_rows > 0) {
            $data = array();
    
            while ($row = $result->fetch_assoc()) {
                $data[] = $row;
            }
            echo json_encode($data);
        } else {
            echo "Table is empty";
        }
    } else {
        echo "Error: Database connection";
    }
} else {
    echo "All fields are required";
}
?>