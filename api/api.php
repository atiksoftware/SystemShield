<?php

require_once 'bootstrap.php';

// if method get
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    echo '0x00';
    die();
}

if (empty($_POST)) {
    die();
}

$inputs = [];

foreach (API_FILLABLE as $fillable) {
    $inputs[$fillable] = $_POST[$fillable] ?? null;
}

if (isset($inputs['created_at'])) {
    $inputs['created_at'] = date('Y-m-d H:i:s', $inputs['created_at'] / 1000);
}

$stmt = $pdo->prepare('INSERT INTO ' . NTF_TABLE . ' (' . implode(', ', API_FILLABLE) . ') VALUES (:' . implode(', :', API_FILLABLE) . ')');

foreach ($inputs as $key => $value) {
    $stmt->bindValue(':' . $key, $value);
}

$stmt->execute();
