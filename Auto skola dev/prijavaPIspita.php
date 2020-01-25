<?php

$username = 'Lusciante';
$passencoded = 'YXp2S2VhM3Vh';
$password = base64_decode($passencoded);

$kandidatID = $_GET["id"];
$task = $_GET["key"];

$url = "http://asteam.ddns.net:8000/rest/api/2/issue/".$task;
$ch = curl_init();
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_VERBOSE, 1);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_USERPWD, "$username:$password");

$resultStr = curl_exec($ch);
$responsecode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

curl_close($ch);

$result = json_decode($resultStr, true);
$realID = $result["id"];
$realKey = $result["key"];
$statusID = $result["fields"]["status"]["id"];
$responseMsg = "";

// TODO: error if wrong link
if ($realID == $kandidatID && $realKey == $task) {
  if ($statusID == "10307") {
    $responseMsg = "Kandidat prijavljen";
  } else if ($statusID == "10308" || $statusID == "10306" || $statusID == "10309") {
      $responseCode = transitionKandidat();    
    if ($responseCode == 200 || $responseCode == 204) {
      $responseMsg = "Kandidat uspješno prijavljen";
    } else {
      $responseMsg = "Došlo je do greške pri prijavi kandidata";
    }
  }   
} else {
  $responseMsg = "nay";
  return $responseCode;
}

function transitionKandidat() {
  global $url;
  global $username;
  global $password;
  global $task;
  global $statusID;

  $updateIssueCH = curl_init();
  $headers = array(
    'Accept: application/json',
    'Content-Type: application/json'
  );

  $array = array("transition" => array(
          "id" => 151)); // Prijava kandidata
  $data = json_encode($array);
  $url = $url."/transitions";

  curl_setopt($updateIssueCH, CURLOPT_RETURNTRANSFER, true);
  curl_setopt($updateIssueCH, CURLOPT_VERBOSE, 1);
  curl_setopt($updateIssueCH, CURLOPT_SSL_VERIFYPEER, 0);
  curl_setopt($updateIssueCH, CURLOPT_SSL_VERIFYHOST, 0);
  curl_setopt($updateIssueCH, CURLOPT_HTTPHEADER, $headers);
  curl_setopt($updateIssueCH, CURLOPT_HEADER, 0);
  curl_setopt($updateIssueCH, CURLOPT_CUSTOMREQUEST, "POST");
  curl_setopt($updateIssueCH, CURLOPT_POSTFIELDS, $data);
  curl_setopt($updateIssueCH, CURLOPT_URL, $url);
  curl_setopt($updateIssueCH, CURLOPT_USERPWD, "$username:$password");
  
  $updateIssueResult = curl_exec($updateIssueCH);
  $responsecode = curl_getinfo($updateIssueCH, CURLINFO_RESPONSE_CODE);
  curl_close($updateIssueCH);
  return $responsecode;     
}
?> 

<!DOCTYPE html>
<html>
 <head>
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous"><script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
  <style>
  .html, body, {
      position:fixed;
      top:0;
      bottom:0;
      left:0;
      right:0;
  }

  .jumbotron{
    position: relative;
    padding:15px;
    margin-top:30px;
    background: #eee;
    margin-top: 1px;
    text-align:center;
    margin-bottom:0;
  }

  .divlogo{
    padding:15px; 
    text-align:center;   
  }

  .form-group{
    margin-bottom: 10px;
  }

  @media (min-width: 768px) {
      .jumbotron {
        position: relative;
        padding:15px;
        margin-top:30px;
        background: #eee;
        margin-top: 1px;
        text-align:center;
        margin-bottom:0;
    }
    .jumbotron p {
      font-size: 3.5rem;
    }
    .jumbotron img {
      width: 50%;
    }
    .jumbotron form {
      text-align:left
    }
  }

  @media (min-width: 1200px) {
      .jumbotron {
        position: relative;
        padding:20px;
        margin-top:30px;
        background: #eee;
        margin-top: 1px;
        text-align:center;
        margin-bottom:0;
    }
    .jumbotron p {
      font-size: 2rem;
    }
    .jumbotron img {
      width: 30%;
    }
    .jumbotron form {
      text-align:left
    }
  }
  </style>
 </head>
 <body style="font-family: -apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,'Helvetica Neue',Arial,sans-serif,'Apple Color Emoji','Segoe UI Emoji','Segoe UI Symbol';">
  <div class="container">
    <div class="jumbotron">
    <img src="http://www.autoskola.me/images/Auto_skola_as-team_logo.png"/>
    <p class="lead"></p>
      <hr class="my-4">
      <p></p>
      <div>
        <?php echo $responseMsg?>
      </div>
    </div>
   </div>
  <div class="container">
    <div class="divlogo">
      <a href="http://www.ivisol.com"><img src="https://visol.ddns.net:8888/s/-vp54mx/800009/148610e72bf109a73d697fdbea3f36b9/_/jira-logo-scaled.png"/></a>
    </div>
   </div>
 </body>
</html>



 