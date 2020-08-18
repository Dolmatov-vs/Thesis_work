### С подробной задачей дипломного проекта и требованиями к его выполнению можно ознакомиться [тут](https://github.com/netology-code/qa-diploma "Дипломный проект").

**Для запуска приложения на основе БД MySQL.**
`docker-compose -f docker-compose-mysql.yml up`  
**Для запуска приложения на основе БД PostgresSQL**
`docker-compose -f docker-compose-postgres.yml up`  
*В зависимости от используемой ОС, для запуска могут потребоваться права администратора.*

После запуска всех контейнеров, приложение будет доступно по ссылке: `http://localhost:8080/`
    
**Для запуска тестов и генерации отчетности "Allure" используйте команду:**  
`./gradlew clean test --info "-Dselenide.headless=true"; ./gradlew allureReport` 
