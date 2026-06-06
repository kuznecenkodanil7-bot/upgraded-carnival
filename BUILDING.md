# Сборка без готового wrapper JAR

В архиве есть `gradle-wrapper.properties`, но бинарный `gradle-wrapper.jar` намеренно не вложен.

Самый простой способ:

1. Установить JDK 21 и Gradle 9.4.1.
2. Выполнить в корне проекта:

```bash
gradle wrapper
./gradlew build
```

Либо открыть папку как Gradle-проект в IntelliJ IDEA и запустить задачу `build`.
