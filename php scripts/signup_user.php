<?php
require "conn.php";
$name = $_POST["name"];
$email = $_POST["email"];
$pass = $_POST["pass"];
$dob = $_POST["dob"];
$phone_no = $_POST["phone_no"];
$encoded_photo = $_POST["encoded_photo"];
$secQues = $_POST["secQues"];
$secAns = $_POST["secAns"];

$decoded_string = base64_decode($encoded_photo);
	
	$path = 'images/'.$email.'.png';
	
	$file = fopen($path, 'wb');
	
	$is_written = fwrite($file, $decoded_string);
	fclose($file);
	
	if($is_written > 0) {
		
		$query = "insert into user_table values ('$name', '$email', '$pass', '$dob', '$phone_no', '$path', '$secQues', '$secAns');";
		
		$result = mysqli_query($conn, $query) ;
		
		if($result){
			echo "success";
		}else{
			echo "fail";
		}
		
		mysqli_close($conn);
	}
	
?>