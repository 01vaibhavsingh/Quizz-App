<?php
require "conn.php";
$name = $_POST["name"];
$surname = $_POST["surname"];
$age = $_POST["age"];
$username = $_POST["username"];
$password = $_POST["password"];
$mysql_qry = "insert into employee_data (name, surname, age, username, password) values ('$name', '$surname', '$age', '$username', '$password')";

if($conn->query($mysql_qry) === TRUE){
	echo "Insert successful";
}
else{
	echo "Error: " . $mysql_qry . "<br>" . $conn->error;
}
$conn->close();
?>