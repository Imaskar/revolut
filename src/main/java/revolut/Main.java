package revolut;

import com.google.gson.Gson;
import com.wizzardo.http.HttpConnection;
import com.wizzardo.http.HttpServer;
import com.wizzardo.http.request.Header;
import com.wizzardo.http.response.Response;
import com.wizzardo.http.response.Status;

public class Main {

  private static final Gson JSON = new Gson();

  public static void main(String[] args){
    final Bank bank = new LockingBank();
    HttpServer<HttpConnection> server = new HttpServer<>(8080);
    server.getUrlMapping()
        .append("/", (request, response) -> response.setBody("Running"))
        .append("/accounts/create", (request, response) -> {
          final String id = request.param("id");
          if (id==null||"".equals(id)){
            return response(response, "Please specify id", Status._400);
          } else {
            final Result<Void> res = bank.createAccount(id);
            if (res.success){
              return response(response, res);
            } else {
              return response(response, res.comment, Status._500);
            }
          }
        })
        .append("/accounts/balance",(request, response) -> {
          final String id = request.param("id");
          if (id == null || "".equals(id)) {
            return response(response, "Please specify id", Status._400);
          } else {
            final Result<Long> res = bank.getBalance(id);
            if (res.success) {
              return response(response, res);
            } else {
              return response(response, res.comment, Status._500);
            }
          }
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
            return response(response, "Please specify id", Status._400);
          } else if (amount <= 0l){
            return response(response, "Please specify correct amount", Status._400);
          } else {
            final Result<Long> res = bank.topup(id,amount);
            if (res.success) {
              return response(response, res);
            } else {
              return response(response, res.comment, Status._500);
            }
          }
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
            return response(response, "Please specify id", Status._400);
          } else if (amount <= 0l){
            return response(response, "Please specify correct amount", Status._400);
          } else {
            final Result<Long> res = bank.withdraw(id,amount);
            if (res.success) {
              return response(response, res);
            } else {
              return response(response, res.comment, Status._500);
            }
          }
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
            return response(response, "Please specify from and to parameters", Status._400);
          } else if (amount <= 0l){
            return response(response, "Please specify correct amount", Status._400);
          } else {
            final Result<Long> res = bank.transfer(id1,id2,amount);
            if (res.success) {
              return response(response, res);
            } else {
              return response(response, res.comment, Status._500);
            }
          }
        });

    server.start();
  }

  private static Response response(Response response, Result<?> result) {
    response.header(Header.KV_CONTENT_TYPE_APPLICATION_JSON);
    response.setBody(JSON.toJson(result));
    response.setStatus(result.success ? Status._200 : Status._500);
    return response;
  }

  private static Response response(Response response, String text, Status status) {
    response.header(Header.KV_CONTENT_TYPE_APPLICATION_JSON);
    response.setBody(JSON.toJson(new Result<Void>(text, null)));
    response.setStatus(status);
    return response;
  }

}