## С подробной задачей дипломного проекта и требованиями к его выполнению можно ознакомиться [тут](https://github.com/netology-code/qa-diploma "Дипломный проект").

#### Запуск приложения:
* **С использованием БД MySQL.**  
`docker-compose -f docker-compose-mysql.yml up`  
* **С использованием БД PostgresSQL**  
`docker-compose -f docker-compose-postgres.yml up`  

*В зависимости от используемой ОС, для запуска могут потребоваться права администратора.*

После запуска всех контейнеров, приложение будет доступно по ссылке: `http://localhost:8080/`

#### Запуск тестов и генерация отчётности Allure:    
* **С использованием СУБД MySQL:**  
`./gradlew clean test --info -Dselenide.headless=true -DjdbcUrl=jdbc:mysql://localhost:3306/app; ./gradlew allureReport`

* **С использованием СУБД Postgresql:**  
`./gradlew clean test --info -Dselenide.headless=true -DjdbcUrl=jdbc:postgresql://localhost:5432/app; ./gradlew allureReport` 
