<?php
require "conn.php";
$email = $_POST["email"];
$password = $_POST["password"];
$mysql_qry = "select * from user_table where email like '$email' and password like '$password';";
$result = mysqli_query($conn, $mysql_qry);
if(mysqli_num_rows($result) > 0){
	$response = array();
	while($row = mysqli_fetch_array($result)){
		array_push($response, array("name"=>$row[0],"email"=>$row[1],"password"=>$row[2],"dob"=>$row[3],"phone_no"=>$row[4],"photo_path"=>$row[5],"sec_quest"=>$row[6],"sec_ans"=>$row[7]));
	}
	echo json_encode(array("server_response"=>$response));
}
else{
	echo "fail";
}
mysqli_close($conn);
?>