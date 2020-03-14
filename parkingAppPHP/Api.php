<?php

require_once 'DbConnect.php';

$response = array();

if (isset($_GET['apicall'])) {
    
    switch ($_GET['apicall']) {
        
        case 'signup':
            if (isTheseParametersAvailable(array(
                'fullname',
                'password',
                'mobile_no',
                'vehicle_no',
            ))) {
                $fullname 		= $_POST['fullname'];
                $password 		= md5($_POST['password']);
                $mobile_no    	= $_POST['mobile_no'];
                $vehicle_no   	= $_POST['vehicle_no'];
                
                $stmt = $conn->prepare("SELECT id FROM users WHERE mobile_no = ? OR vehicle_no = ?");
                $stmt->bind_param("ss", $mobile_no, $vehicle_no);
                $stmt->execute();
                $stmt->store_result();
                
                if ($stmt->num_rows > 0) {
                    $response['error']   = true;
                    $response['message'] = 'User already registered';
                    $stmt->close();
                } else {
                    $stmt = $conn->prepare("INSERT INTO users (fullname, password, mobile_no, vehicle_no) VALUES (?, ?, ?, ?)");
                    $stmt->bind_param("ssss", $fullname, $password, $mobile_no, $vehicle_no);
                    
                    if ($stmt->execute()) {
                        $stmt = $conn->prepare("SELECT id, id, fullname, mobile_no, vehicle_no FROM users WHERE mobile_no = ?");
                        $stmt->bind_param("s", $mobile_no);
                        $stmt->execute();
                        $stmt->bind_result($userid, $id, $fullname, $mobile_no, $vehicle_no);
                        $stmt->fetch();
                        
                        $user = array(
                            'id' => $id,
                            'fullname' => $fullname,
                            'mobile_no' => $mobile_no,
                            'vehicle_no' => $vehicle_no
                        );
                        
                        $stmt->close();
                        
                        $response['error']   = false;
                        $response['message'] = 'User registered successfully';
                        $response['user']    = $user;
                    }
                }
                
            } else {
                $response['error']   = true;
                $response['message'] = 'required parameters are not available';
            }
            
            break;
        
        case 'login':
            
            if (isTheseParametersAvailable(array(
                'mobile_no',
                'password'
            ))) {
                
                $mobile_no = $_POST['mobile_no'];
                $password = md5($_POST['password']);
                
                $stmt = $conn->prepare("SELECT id, fullname, mobile_no, vehicle_no FROM users WHERE mobile_no = ? AND password = ?");
                $stmt->bind_param("ss", $mobile_no, $password);
                
                $stmt->execute();
                
                $stmt->store_result();
                
                if ($stmt->num_rows > 0) {
                    
                    $stmt->bind_result($id, $fullname, $mobile_no, $vehicle_no);
                    $stmt->fetch();
                    
                    $user = array(
                        'id' => $id,
                        'fullname' => $fullname,
                        'mobile_no' => $mobile_no,
                        'vehicle_no' => $vehicle_no
                    );
                    
                    $response['error']   = false;
                    $response['message'] = 'Login successfully';
                    $response['user']    = $user;
                } else {
                    $response['error']   = false;
                    $response['message'] = 'Invalid mobile number or password';
                }
            }
            break;
        
        default:
            $response['error']   = true;
            $response['message'] = 'Invalid Operation Called';
    }
    
} else {
    $response['error']   = true;
    $response['message'] = 'Invalid API Call';
}

echo json_encode($response);

function isTheseParametersAvailable($params){
    
    foreach ($params as $param) {
        if (!isset($_POST[$param])) {
            return false;
        }
    }
    return true;
}


?>