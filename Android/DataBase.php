<?php
require "DataBaseConfig.php";

class DataBase
{
    public $connect;
    public $data;
    private $sql;
    protected $servername;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $this->connect = null;
        $this->data = null;
        $this->sql = null;
        $dbc = new DataBaseConfig();
        $this->servername = $dbc->servername;
        $this->username = $dbc->username;
        $this->password = $dbc->password;
        $this->databasename = $dbc->databasename;
    }

    function dbConnect()
    {
        $this->connect = mysqli_connect($this->servername, $this->username, $this->password, $this->databasename);
        return $this->connect;
    }

    function prepareData($data)
    {
        return mysqli_real_escape_string($this->connect, stripslashes(htmlspecialchars($data)));
    }

    function logIn($table, $username, $password)
    {
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $this->sql = "select * from " . $table . " where username = '" . $username . "'";
        $result = mysqli_query($this->connect, $this->sql);
        $row = mysqli_fetch_assoc($result);
        if (mysqli_num_rows($result) != 0) {
            $dbusername = $row['username'];
            $dbpassword = $row['password'];
            if ($dbusername == $username && $dbpassword == $password) {
                $login = true;
            } else $login = false;
        } else $login = false;

        return $login;
    }

    function signUp($table, $name, $surname, $level, $username, $password)
    {
        $name = $this->prepareData($name);
        $surname = $this->prepareData($surname);
        $username = $this->prepareData($username);
        $password = $this->prepareData($password);
        $level = $this->prepareData($level);
        $password = password_hash($password, PASSWORD_DEFAULT);
        $this->sql =
            "INSERT INTO " . $table . " (name, surname, username, password, level) VALUES ('" . $name . "','" . $surname . "','" . $username . "','" . $password . "','" . $level . "')";
        if (mysqli_query($this->connect, $this->sql)) {
            return true;
        } else return false;
    }

    function findProduct($InventoryID, $productInfo, $productInfoType)
    {
        $InventoryID = $this->prepareData($InventoryID);
        $productInfo = $this->prepareData($productInfo);
        $productInfoType = $this->prepareData($productInfoType);
        
        $this->sql = "SELECT * FROM products WHERE " .$productInfoType. " = '" . $productInfo . "'";
        $product_result = mysqli_query($this->connect, $this->sql);
        if ( mysqli_num_rows($product_result) == 0){
            return NULL;
        }
        
        $product =$product_result->fetch_assoc();

        $this->sql = "SELECT number FROM stock_details WHERE stock_id = '" . $InventoryID . "' AND product_id = '" . $product["id"] . "'";
        $number_result = mysqli_query($this->connect, $this->sql);
        $number_row = $number_result->fetch_assoc();
        if($number_row == NULL){
            return NULL;
        }
        $number = $number_row['number'];
        $product["number"] = $number;
        return $product;

    }



    function correctProductNumber($InventoryId,$product_id,$number){
        $InventoryId = $this->prepareData($InventoryId);
        $product_id = $this->prepareData($product_id);
        $number = $this->prepareData($number);

        $this->sql = "SELECT * FROM products WHERE id = '" . $product_id . "'";
        $product_total_result = mysqli_query($this->connect, $this->sql);

        if ( mysqli_num_rows($product_total_result) == 0){
            return false;
        }
        $product_total =$product_total_result->fetch_assoc();
        $product_total_number = $product_total['total_number'];

        $this->sql = "SELECT * FROM stock_details WHERE stock_id = '" . $InventoryId . "' AND product_id = '" . $product_id . "'";
        $product_result = mysqli_query($this->connect, $this->sql);
        if ( mysqli_num_rows($product_result) == 0){
            return false;
        }

        $product =$product_result->fetch_assoc();
        $product_number = $product["number"];
        $product_diff = $product_number - $number;
        $product_new_total_number = $product_total_number - $product_diff;
        $this->sql = "UPDATE products SET total_number = '" . $product_new_total_number . "' WHERE id = '" . $product_id . "'";
        if(mysqli_query($this->connect, $this->sql)){
            $this->sql = "UPDATE stock_details SET number = '" . $number . "' WHERE product_id = '" . $product_id . "' AND stock_id ='" . $InventoryId . "'";
            if(mysqli_query($this->connect, $this->sql)){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }

    }


    function getTableList($table){
        $this->sql = "SELECT * FROM " . $table . "";
        $result = mysqli_query($this->connect, $this->sql);
        return $result;
    }

    function getInventoryTargetData($InventoryName){
        $InventoryName = $this->prepareData($InventoryName);

        $this->sql = "SELECT * FROM stock WHERE name = '" .$InventoryName. "'";
        $result = mysqli_query($this->connect, $this->sql);
        return $result;        
    }


    function getProductsFromInventoryTarget($InventoryId){
        $InventoryID = $this->prepareData($InventoryId);

        $this->sql = "SELECT product_id from stock_details WHERE stock_id = " .$InventoryID. "";
        $result = mysqli_query($this->connect, $this->sql);
        return $result;
    }
    
}


?>
