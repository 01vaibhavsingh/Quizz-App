<?php
require "conn.php";
$email = $_POST["email"];
$date_time = $_POST["date_time"];
$category = $_POST["category"];
$score = $_POST["score"];

$mysql_qry = "insert into scores_table values ('$email', '$date_time', '$category', '$score');";

if($conn->query($mysql_qry) === TRUE){
	echo "success";
}
else{
	echo "fail";
}
$conn->close();
?>