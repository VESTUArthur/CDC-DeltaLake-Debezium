
<?php 
//controle les entrées et execute l'inscription.
session_start();

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

for ($i = 1; $i <= 150; $i++) {
    $seed = rand(0, 100);

    $email = 'subscriber' . srand($seed)  . '@example.com';
    $password = 'password' . srand($seed);
    $type = rand(0, 2) === 0 ? 'Free' : 'Pro';
    $price = $type === 'Free' ? 0 : 15;
    echo $i;
    if ($stmt = $mysqli->prepare("INSERT INTO subscriber_sb (sb_email, sb_password, sb_type, sb_price) VALUES (?, ?, ?, ?)")) {
        $password = password_hash($password, PASSWORD_BCRYPT, $options);
        $stmt->bind_param("sssi", $email, $password, $type, $price);

        //execution de la requete
        if(!$stmt->execute()) {
            die('Erreur lors de l\'enregistrement');
        }
    }
}

$_SESSION['message'] = "Enregistrement de 1000 abonnés réussi";
header('Location: index.php'); 
?>
