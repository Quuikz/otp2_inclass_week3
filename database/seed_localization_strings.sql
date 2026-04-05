USE fuel_calculator_localization;

DELETE FROM localization_strings;

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
('app.title', 'Fuel Consumption and Trip Cost Calculator', 'en-US'),
('language.label', 'Language:', 'en-US'),
('distance.label', 'Distance (km)', 'en-US'),
('consumption.label', 'Fuel Consumption (L/100 km)', 'en-US'),
('price.label', 'Fuel Price (per liter)', 'en-US'),
('distance.prompt', 'Example: 180', 'en-US'),
('consumption.prompt', 'Example: 6.5', 'en-US'),
('price.prompt', 'Example: 2.05', 'en-US'),
('calculate.button', 'Calculate Trip Cost', 'en-US'),
('result.placeholder', 'Result will appear here.', 'en-US'),
('result.label', 'Total fuel needed: {0} L | Total cost: {1}', 'en-US'),
('invalid.input', 'Invalid input', 'en-US')
ON DUPLICATE KEY UPDATE
`value` = VALUES(`value`);

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
('app.title', 'Calculateur de consommation de carburant et de cout du trajet', 'fr-FR'),
('language.label', 'Langue :', 'fr-FR'),
('distance.label', 'Distance (km)', 'fr-FR'),
('consumption.label', 'Consommation de carburant (L/100 km)', 'fr-FR'),
('price.label', 'Prix du carburant (par litre)', 'fr-FR'),
('distance.prompt', 'Exemple : 180', 'fr-FR'),
('consumption.prompt', 'Exemple : 6,5', 'fr-FR'),
('price.prompt', 'Exemple : 2,05', 'fr-FR'),
('calculate.button', 'Calculer le cout du trajet', 'fr-FR'),
('result.placeholder', 'Le resultat s''affichera ici.', 'fr-FR'),
('result.label', 'Carburant total necessaire : {0} L | Cout total : {1}', 'fr-FR'),
('invalid.input', 'Entree invalide', 'fr-FR')
ON DUPLICATE KEY UPDATE
`value` = VALUES(`value`);

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
('app.title', '燃料消費量と旅行費用計算機', 'ja-JP'),
('language.label', '言語：', 'ja-JP'),
('distance.label', '距離 (km)', 'ja-JP'),
('consumption.label', '燃費 (L/100 km)', 'ja-JP'),
('price.label', '燃料価格 (1リットルあたり)', 'ja-JP'),
('distance.prompt', '例: 180', 'ja-JP'),
('consumption.prompt', '例: 6.5', 'ja-JP'),
('price.prompt', '例: 2.05', 'ja-JP'),
('calculate.button', '旅行費用を計算', 'ja-JP'),
('result.placeholder', '結果がここに表示されます。', 'ja-JP'),
('result.label', '必要な燃料: {0} L | 合計費用: {1}', 'ja-JP'),
('invalid.input', '入力が無効です', 'ja-JP')
ON DUPLICATE KEY UPDATE
`value` = VALUES(`value`);

INSERT INTO localization_strings (`key`, `value`, `language`) VALUES
('app.title', 'محاسبه‌گر مصرف سوخت و هزینه سفر', 'fa-IR'),
('language.label', 'زبان:', 'fa-IR'),
('distance.label', 'مسافت (کیلومتر)', 'fa-IR'),
('consumption.label', 'مصرف سوخت (لیتر در 100 کیلومتر)', 'fa-IR'),
('price.label', 'قیمت سوخت (هر لیتر)', 'fa-IR'),
('distance.prompt', 'مثال: 180', 'fa-IR'),
('consumption.prompt', 'مثال: 6.5', 'fa-IR'),
('price.prompt', 'مثال: 2.05', 'fa-IR'),
('calculate.button', 'محاسبه هزینه سفر', 'fa-IR'),
('result.placeholder', 'نتیجه اینجا نمایش داده می‌شود.', 'fa-IR'),
('result.label', 'سوخت مورد نیاز: {0} لیتر | هزینه کل: {1}', 'fa-IR'),
('invalid.input', 'ورودی نامعتبر است', 'fa-IR')
ON DUPLICATE KEY UPDATE
`value` = VALUES(`value`);

