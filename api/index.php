<?php

require_once 'bootstrap.php';

$today_datetime = new DateTime();
$today_datetime->setTime(0, 0, 0);

$current_date = clone $today_datetime;
if (isset($_GET['date'])) {
    try {
        $current_date = new DateTime($_GET['date']);
        $current_date->setTime(0, 0, 0);
    } catch (\Throwable $th) {
    }
}

$previous_date = clone $current_date;
$previous_date->modify('-1 day');

$next_date = clone $current_date;
$next_date->modify('+1 day');

$notifications = $pdo->query('SELECT * FROM ' . NTF_TABLE . ' WHERE created_at >= "' . $current_date->format('Y-m-d H:i:s') . '" AND created_at < "' . $next_date->format('Y-m-d H:i:s') . '" ORDER BY created_at DESC')->fetchAll(PDO::FETCH_ASSOC);

$filter_buttons = [
    [
        'datetime'  => $previous_date,
        'title'     => 'Previous',
        'classes'   => ''
    ],
    [
        'datetime'  => $current_date,
        'title'     => 'Current',
        'classes'   => 'text-blue-500'
    ],
    [
        'datetime'  => $next_date,
        'title'     => 'Next',
        'classes'   => $next_date > $today_datetime ? 'disabled opacity-30 pointer-events-none' : ''
    ],
    [
        'datetime'  => $today_datetime,
        'title'     => 'Today',
        'classes'   => 'text-rose-500'
    ],
]

?><!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Shield</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-200">

    <div class="max-w-md mx-auto">
        <div class=" grid grid-cols-4 text-xs ">
            <?php foreach ($filter_buttons as $filter_button) { ?>
                <a class="text-center block p-2 border  border-slate-300 bg-white hover:bg-slate-100 <?php echo $filter_button['classes']; ?>" href="?date=<?php echo $filter_button['datetime']->format('Y-m-d'); ?>">
                    <div class="font-bold"><?php echo $filter_button['title']; ?></div>
                    <div class="text-xs"><?php echo $filter_button['datetime']->format('M d'); ?></div>
                </a>
            <?php } ?>
        </div>
        <div class=" grid grid-cols-1 gap-2 mt-2">
            <?php if (count($notifications) == 0) { ?>
                <div class="py-10">
                    <div class="text-center text-slate-400">No notifications in this date</div> 
                    <div class="text-center text-sm text-rose-500"><?php echo $current_date->format('Y F d'); ?></div>
                </div>
            <?php } ?>
            <?php foreach ($notifications as $notification) { ?>
                <div class="border-t border-b md:border border-slate-300 bg-white p-2 ">
                    <div class="flex w-full justify-between text-slate-400 gap-2">
                        <div class="text-xs min-w-0 truncate"><?php echo $notification['package_name']; ?></div>
                        <div class="text-xs min-w-0 text-right"><?php echo $notification['created_at']; ?></div>
                    </div>
                    <div class="text-sm font-bold"><?php echo $notification['title']; ?></div>
                    <div class="text-sm"><?php echo $notification['text']; ?></div> 
                </div>
            <?php } ?>
        </div>
    </div>
    
</body>
</html>