<?php
require "conn.php";
$email = $_POST["email"];
$mysql_qry = "select * from scores_table where email like '$email' order by date_time desc;";
$result = mysqli_query($conn, $mysql_qry);
if(mysqli_num_rows($result) > 0){
	$response = array();
	while($row = mysqli_fetch_array($result)){
		array_push($response, array("category"=>$row[2],"date_time"=>$row[1],"score"=>$row[3]));
	}
	echo json_encode(array("server_response"=>$response));
}
else{
	echo "fail";
}
mysqli_close($conn);
?>