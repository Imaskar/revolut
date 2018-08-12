package revolut;

public interface Account {
  String getId();

  long getBalance();

  void setBalance(long balance);
}
