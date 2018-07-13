I have realized CarPark application.

I have prepared 5 enpoints that are corresponding to the user stories.

I have also prepared different currency option. Main curency is BigDecimal type,
 and the basic is 1 cent of PLN. All calculations has been done in PLN, 
 and other currencies are used for the car park client (car owner) payment.
I have assumed that the car park operator wants to know the daily income 
in different currencies, that is why the result of the fifth endpoint is a map
of the <currency, value>.

Each currency has got his own value which corresponds to the NBP (or other) 
exchange rate and can be simply modified in case of the price change. 
The only one that should not  to be change is PLN(100). 
I have also prepared some instruction how to use  different currency during the payment process.  

To operate it is necessary to run the CarparkApplication, open the browser,
and paste there commands.

Examples of commands:

 query example for userStory01:
 ```
 http://localhost:8080/carpark/startPark?number=AB 12345&type=regular
 http://localhost:8080/carpark/startPark?number=AB 12345&type=vip
 ```
 query example for userStory02:
 ```
 http://localhost:8080/carpark/checkIfStarted?number=AB 12345
 ```
 query example for userStory03:
 ```
 http://localhost:8080/carpark/stopPark?ticket=0&currency=PLN
 ```
 query example for userStory04:
 ```
http://localhost:8080/carpark/getTicketFee?number=0
 ```
 query example for userStory05:
 ```
 http://localhost:8080/carpark/getDailyIncome?day=20180712
 ```
And the final two that weren't a task:
welcome
```
http://localhost:8080/carpark
```
and "healthCheck"
```
http://localhost:8080/carpark/hello
```

I have added very simple car registration number validator - length of the string
has to be lower than 15. More about car registration number can be found here: 
```
https://www.zpp.pl/storage/files/2017-04//535bcf66c5a7d0ca1d1484fc95573c864140.pdf
```
I will by happy to meet you face to face.

If you look into my resume you will see that I have started to learn Java 
less than one year ago an it was pure Java, without any web applications.
Despite of my gray hair I am a real junior.   