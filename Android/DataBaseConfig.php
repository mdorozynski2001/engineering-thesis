<?php

class DataBaseConfig
{
    public $servername;
    public $username;
    public $password;
    public $databasename;

    public function __construct()
    {

        $this->servername = '192.168.0.201';
        $this->username = 'admin';
        $this->password = 'admin';
        $this->databasename = 'inventory_app';

    }
}

?>
