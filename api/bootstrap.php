<?php

define('DB_HOST', 'localhost');
define('DB_PORT', '3306');
define('DB_DATABASE', 'system_shield');
define('DB_USERNAME', 'root');
define('DB_PASSWORD', '');

define('NTF_TABLE', 'notifications');

define('API_FILLABLE', [
    'package_name',
    'title',
    'text',
    'created_at',
    'device',
    'model',
    'product',
    'manufacturer',
    'brand',
    'android_version',
    'sdk_version',
]);

$pdo = new PDO('mysql:host=' . DB_HOST . ';port=' . DB_PORT . ';dbname=' . DB_DATABASE, DB_USERNAME, DB_PASSWORD);
