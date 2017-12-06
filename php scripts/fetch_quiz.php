<?php
require "conn.php";
$category = $_POST["category"];
$mysql_qry = "select * from questions where category like '$category' ORDER BY RAND() LIMIT 20;";
$result = mysqli_query($conn, $mysql_qry);
if(mysqli_num_rows($result) > 0){
	$response = array();
	while($row = mysqli_fetch_array($result)){
		array_push($response, array("question"=>$row[2],"option1"=>$row[3],"option2"=>$row[4],"option3"=>$row[5],"option4"=>$row[6],"correct_option"=>$row[7]));
	}
	echo json_encode(array("server_response"=>$response));
}
else{
	echo "fail";
}
mysqli_close($conn);
?>