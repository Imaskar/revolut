# revolut
Test assignment by Revolut

Usage:  
command line:  
`    mvn clean install`

`    cd target/`

`    java -jar revolut.jar`

In browser:  
    http://localhost:8080/accounts/create?id=1  
    http://localhost:8080/accounts/create?id=2  
    http://localhost:8080/accounts/topup?id=2&amount=200  
    http://localhost:8080/accounts/transfer?from=2&to=1&amount=20  
    http://localhost:8080/accounts/balance?id=1  

Please note, that it is linux-only, because wizzardo-http library uses high performance native epoll library.
