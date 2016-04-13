<?php
require_once 'config.php';

class DB_Connection {

	private $connect;
	function __construct() {
		$this->connect = mysqli_connect(hostname,user,password,db_name) or die("DB error");
	}
	public function get_connection()
	{
		return $this->connect;
	}
}



?>