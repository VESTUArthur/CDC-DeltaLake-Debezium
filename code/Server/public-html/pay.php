

<?php 
//controle les entrées et execute l'inscription.
session_start();
if (!empty($_POST['email']) || !empty($_POST['password']) || !empty($_POST['type']) ) {
   
    $email =  htmlentities($_POST['email']);
    $password = htmlentities($_POST['password']);
    $type = htmlentities($_POST['type']);
    
    switch ($type) {
        case 'Free $0/mo':
            $price = 0;
            $type = 'Free';
            break;
        case 'Pro $15/mo':
            $price = 15;
            $type = "Pro";
            break;
        case 'Enterprise $29/mo':
            $price = 29;
            $type = "Entreprise";
            break;
        default:
            $price = 0;
    }
    echo $email, $password, $type;
    $host = 'db';
    $login = 'root';
    $pw = 'root';
    $dbname = 'ping';
    $mysqli = new mysqli($host,$login,$pw,$dbname);

    $options = [
        'cost' => 12,
    ];

    if ($mysqli->connect_error) {
        die('Erreur de connexion (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
    }        
    if ($stmt = $mysqli->prepare("INSERT INTO subscriber_sb (sb_email, sb_password, sb_type, sb_price) VALUES (?, ?, ?, ?)")) {
        $password = password_hash($password, PASSWORD_BCRYPT, $options);
        $stmt->bind_param("sssi", $email, $password, $type, $price);

        //execution de la requete
        if($stmt->execute()) {
            $_SESSION['message'] = "Enregistrement réussi";
        } else {
            $_SESSION['message'] =  "Impossible d'enregistrer";
        }
    }
    header('Location: index.php'); 
}else{
    die('Erreur une ou plusieurs valeurs non fournie');
}
?>
