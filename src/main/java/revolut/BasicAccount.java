package revolut;

public class BasicAccount implements Account {

  protected final String id;
  protected long balance;

  public BasicAccount(String id) {
    this.id = id;
    balance = 0l;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public long getBalance() {
    return balance;
  }

  @Override
  public void setBalance(long balance) {
    this.balance = balance;
  }
}
