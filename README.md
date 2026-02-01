# ReaperApp

Приложение для скачивания, хранения и чтения книг на Android.

## Функциональность
- Регистрация и вход через Firebase Authentication / Google
- Загружать книги в формате `.txt`, `.epub` и `.pdf`
- Сохранять книги локально и синхронизировать их с Firebase / Yandex.Cloud
- Читать книги с сохранением позиции
- Оффлайн режим
- Управлять профилем пользователя
- Использовать светлую и тёмную тему

## Технический стек
- **UI**: Jetpack Compose
- **Навигация**: Navigation Component
- **База данных**: Room
- **Хранилище настроек и позиции чтения**: DataStore (Preferences)
- **DI**: Hilt
- **Сетевое взаимодействие**: OkHttp
- **Аутентификация**: Firebase Authentication
- **Облачные хранилища**:     Yandex.Cloud Object Storage, Cloudinary
- **Асинхронность и потоковые данные**: Kotlin Coroutines, Flow
- **Парсинг EPUB/HTML**: Jsoup
- **PDF работа**: PdfiumCore

## Архитектура и структура проекта

Проект построен по принципам **Clean Architecture + MVI + multi-module**.

### Модули

- **core** - общий функционал:
  - `core-domain` - модели, сущности, интерфейсы репозиториев, UseCases  
  - `core-data` - общие репозитории, DataSources, di
  - `core-ui` - общие ui-компоненты и темы
  - 
- **feature-модули**:
  - `auth` - экраны регистрации и входа через Firebase  
  - `books` - экран скачанных книг, локальное хранение и чтение  
  - `upload` - экран загрузки книг в облако и локальное сохранение  
  - `profile` - экран профиля пользователя  
  - `book-reader` - экран чтения книги, управление положением, шрифтом и темой  

## Демонстрация интерфейса
<img width="1584" height="717" alt="Frame 1618872826" src="https://github.com/user-attachments/assets/24adfc3a-1351-45a1-a2b7-22c0674776fa" />

## Демонстрация работы приложения
[![Watch the video](https://img.youtube.com/vi/KtGC2CS05vw/0.jpg)](https://www.youtube.com/shorts/KtGC2CS05vw)
