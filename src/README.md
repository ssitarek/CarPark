The customer wants to build a web application for managing the city parking
spaces.

Your job is to:

1. design the API (REST preferably)

2. create a model

3. implement customer’s user stories

User stories

1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid
parking.
2. As a parking operator, I want to check if the vehicle has started the parking meter.
3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
4. As a driver, I want to know how much I have to pay for parking.
5. As a parking owner, I want to know how much money was earned during a given day.

Important notes
The parking rates are:

```
Driver type, 1st hour, 2nd hour, 3rd and each next hour
Regular:     1 PLN,    2 PLN,    1.5x more than hour before
VIP:         free,     2 PLN,    1.2x more than hour before
```
Don’t implement any payment transactions, penalties and so on.
For now, only one currency is accepted but it’s likely to change in the future.Rules
Project should be written in Java, Kotlin, Groovy or Scala.
Project should be built by Maven, Gradle or SBT. You should also prepare bash script
start.sh that runs it.
You can mock/stub/fake the database.
Concentrate on the good design and the clean code, rather than supplying a fully
functioning application.
Remember about the tests.
If you will make any additional assumptions, please write them down in a separate file.
Create an online repository, add a pull request with all your files and send us the link to the
created pull request. In this way we can review and comment on your code.