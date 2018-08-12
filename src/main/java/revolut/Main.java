package revolut;

import com.wizzardo.http.HttpConnection;
import com.wizzardo.http.HttpServer;
import com.wizzardo.http.response.Status;

public class Main {

  public static void main(String[] args){
    final Bank bank = new LockingBank();
    //final revolut.Bank bank = new revolut.EventLoopBank();
    HttpServer<HttpConnection> server = new HttpServer<>(8080);
    server.getUrlMapping()
        .append("/", (request, response) -> response.setBody("Running"))
        .append("/accounts/create", (request, response) -> {
          final String id = request.param("id");
          if (id==null||"".equals(id)){
            response.setBody("Please specify id");
            response.setStatus(Status._400);
          } else {
            final Result<Void> res = bank.createAccount(id);
            if (res.success){
              response.setBody("");
              response.setStatus(Status._200);
            } else {
              response.setBody(res.comment);
              response.setStatus(Status._500);
            }
          }
          return response;
        })
        .append("/accounts/balance",(request, response) -> {
          final String id = request.param("id");
          if (id == null || "".equals(id)) {
            response.setBody("Please specify id");
            response.setStatus(Status._400);
          } else {
            final Result<Long> res = bank.getBalance(id);
            if (res.success) {
              response.setBody(String.valueOf(res.value));
              response.setStatus(Status._200);
            } else {
              response.setBody(res.comment);
              response.setStatus(Status._500);
            }
          }
          return response;
        })
        .append("/accounts/topup",(request, response) -> {
          final String id = request.param("id");
          final String amountStr = request.param("amount");
          Long amount = 0l;
          try {
            amount = Long.parseLong(amountStr);
          } catch (NumberFormatException nfe){
          }
          if (id == null || "".equals(id)) {
            response.setBody("Please specify id");
            response.setStatus(Status._400);
          } else if (amount <= 0l){
            response.setBody("Please specify correct amount");
            response.setStatus(Status._400);
          } else {
            final Result<Long> res = bank.topup(id,amount);
            if (res.success) {
              response.setBody(String.valueOf(res.value));
              response.setStatus(Status._200);
            } else {
              response.setBody(res.comment);
              response.setStatus(Status._500);
            }
          }
          return response;
        })
        .append("/accounts/withdraw",(request, response) -> {
          final String id = request.param("id");
          final String amountStr = request.param("amount");
          Long amount = 0l;
          try {
            amount = Long.parseLong(amountStr);
          } catch (NumberFormatException nfe){
          }
          if (id == null || "".equals(id)) {
            response.setBody("Please specify id");
            response.setStatus(Status._400);
          } else if (amount <= 0l){
            response.setBody("Please specify correct amount");
            response.setStatus(Status._400);
          } else {
            final Result<Long> res = bank.withdraw(id,amount);
            if (res.success) {
              response.setBody(String.valueOf(res.value));
              response.setStatus(Status._200);
            } else {
              response.setBody(res.comment);
              response.setStatus(Status._500);
            }
          }
          return response;
        })
        .append("/accounts/transfer",(request, response) -> {
          final String id1 = request.param("from");
          final String id2 = request.param("to");
          final String amountStr = request.param("amount");
          Long amount = 0l;
          try {
            amount = Long.parseLong(amountStr);
          } catch (NumberFormatException nfe){
          }
          if (id1 == null || "".equals(id1) || id2 == null || "".equals(id2)) {
            response.setBody("Please specify from and to parameters");
            response.setStatus(Status._400);
          } else if (amount <= 0l){
            response.setBody("Please specify correct amount");
            response.setStatus(Status._400);
          } else {
            final Result<Long> res = bank.transfer(id1,id2,amount);
            if (res.success) {
              response.setBody(String.valueOf(res.value));
              response.setStatus(Status._200);
            } else {
              response.setBody(res.comment);
              response.setStatus(Status._500);
            }
          }
          return response;
        });

    server.start();
  }
}