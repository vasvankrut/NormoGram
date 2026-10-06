# NormoGram для Android

Неофициальная Android-версия NormoGram, основанная на открытом исходном коде [Telegram for Android](https://github.com/DrKLO/Telegram). Проект не связан с Telegram FZ-LLC. Общая информация и лицензии — в [README проекта](../README.md).

## Перед сборкой

1. Установите Android Studio, Android SDK и NDK версий, указанных в [инструкции upstream](https://github.com/DrKLO/Telegram#compilation-guide).
2. Укажите собственные `APP_ID` и `APP_HASH` Telegram API в `TMessagesProj/src/main/java/org/telegram/messenger/BuildVars.java`.
3. Для Firebase push-уведомлений создайте собственные Android-приложения с идентификаторами `org.normogram.messenger`, `org.normogram.messenger.beta` и `org.normogram.messenger.web`, затем замените `TMessagesProj/google-services.json` на свой файл.
4. Настройте собственный ключ Google Maps, если нужны карты.
5. Для публикации APK создайте свой release keystore и храните его пароли вне Git.

В `BuildVars.java` и `google-services.json` сейчас оставлены пустые либо замещающие значения. Они нужны, чтобы в репозитории не использовались чужие учётные данные; вход и интеграции не будут работать до настройки своих ключей.

## Изменения NormoGram

В приложении есть отдельная страница NormoGram в настройках. На ней можно выбрать синий или ночной значок приложения и включить отображение секунд во времени сообщений.
