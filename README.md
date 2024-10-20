# Android-приложение для получения координат и информации о сотовых сетях

Приложение отображает текущие координаты (широта, долгота) и данные о сотовых базовых станциях (GSM, LTE, WCDMA) с использованием FusedLocationProviderClient и TelephonyManager.

## Функционал
- Получение координат устройства (широта, долгота).
- Сбор данных о базовых станциях: тип сети, Cell ID, уровень сигнала, LAC/TAC, оператор.
- Динамическое обновление данных.
- Обработка разрешений на доступ к геолокации.

## Основные компоненты
- **Местоположение**: Используется FusedLocationProviderClient для получения координат с обновлением в реальном времени.
- **Сотовые сети**: TelephonyManager собирает данные о базовых станциях (GSM, LTE, WCDMA).
- **Разрешения**: Приложение запрашивает доступ к геолокации и обрабатывает повторные запросы при необходимости.

## Структура проекта
```
com.example.visualprog
├── MainActivity.kt           # Основная логика приложения
├── ui/UIManager.kt           # Работа с пользовательским интерфейсом
├── location/LocationModule.kt # Модуль для геолокации
├── permissions/PermissionManager.kt # Обработка разрешений
└── cellinfo/CellInfoModule.kt # Сбор данных о сотовых базовых станциях
```

## Используемые технологии
- **Kotlin**
- **Fused Location Provider API**
- **TelephonyManager API**