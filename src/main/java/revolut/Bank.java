package revolut;

public interface Bank {

  public Result<Void> createAccount(String id);
  public Result<Long> getBalance(String id);
  public Result<Long> transfer(String from, String to, long amount);
  public Result<Long> topup(String id, long amount);
  public Result<Long> withdraw(String id, long amount);
}
