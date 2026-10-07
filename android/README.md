# NormoGram для Android

Неофициальная Android-версия NormoGram, основанная на открытом исходном коде [Telegram for Android](https://github.com/DrKLO/Telegram). Проект не связан с Telegram FZ-LLC. Общая информация и лицензии — в [README проекта](../README.md).

## Перед сборкой

1. Установите Android Studio, Android SDK и NDK версий, указанных в [инструкции upstream](https://github.com/DrKLO/Telegram#compilation-guide).
2. Для локальной тестовой сборки в `BuildVars.java` настроены публичные тестовые данные Telegram (`APP_ID=17349`). Они ограничены Telegram и могут не позволить войти в обычный аккаунт. Перед публикацией обязательно замените их на собственные `APP_ID` и `APP_HASH`, полученные через [my.telegram.org](https://my.telegram.org).
3. Для Firebase push-уведомлений создайте собственные Android-приложения с идентификаторами `org.normogram.messenger`, `org.normogram.messenger.beta` и `org.normogram.messenger.web`, затем замените `TMessagesProj/google-services.json` на свой файл.
4. Настройте собственный ключ Google Maps, если нужны карты.
5. Для публикации APK создайте свой release keystore и храните его пароли вне Git.

Тестовые API-данные предназначены только для локальной проверки и не являются ключами NormoGram. Firebase, Google Maps и подпись APK требуют отдельной настройки; без них часть интеграций и публикация сборки могут быть недоступны.

## Изменения NormoGram

В приложении есть отдельная страница NormoGram в настройках. На ней можно выбрать синий или ночной значок приложения и включить отображение секунд во времени сообщений.
