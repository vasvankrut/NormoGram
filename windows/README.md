# NormoGram для Windows

Неофициальная версия NormoGram для Windows, основанная на открытом исходном коде [Telegram Desktop](https://github.com/telegramdesktop/tdesktop). Проект не связан с Telegram FZ-LLC. Общая информация и лицензии — в [README проекта](../README.md).

## Перед сборкой

1. Подготовьте Visual Studio, SDK и зависимости по [инструкции сборки Windows](docs/building-win.md).
2. Для тестовой сборки можно включить штатные ограниченные данные Telegram через CMake `-D TDESKTOP_API_TEST=ON`. Для публикации получите собственные `api_id` и `api_hash` Telegram API и задайте их параметрами `TDESKTOP_API_ID` и `TDESKTOP_API_HASH`. Инструкция: [Telegram API](https://core.telegram.org/api/obtaining_api_id).
3. Не добавляйте ключи, сертификаты или signing secrets в Git.

## Изменения NormoGram

В приложении есть отдельная страница NormoGram в настройках. На ней можно включить секунды в форматировании времени сообщений и переключить значок на ночной вариант. Во время работы меняются значки окон и панели задач; значок `.exe` выбирается при сборке.

Инструкции upstream могут упоминать Telegram Desktop и требуют адаптации для NormoGram.
