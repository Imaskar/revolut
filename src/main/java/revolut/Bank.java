package revolut;

public interface Bank {

  /**
   * Create new account.
   *
   * @param id unique id of new account
   * @return null when account was created, error otherwise
   */
  public Result<Void> createAccount(String id);

  /**
   * Get account balance by id.
   *
   * @param id account id
   * @return
   */
  public Result<Long> getBalance(String id);

  /**
   * Transfer 'amount' of funds from 'from' account to 'to' account
   *
   * @param from   id of sending account
   * @param to     id of receiving account
   * @param amount amount of funds to transfer
   * @return remaining balance of sending account
   */
  public Result<Long> transfer(String from, String to, long amount);

  /**
   * Add funds to account
   *
   * @param id id of account to top up
   * @param amount amount of funds to add
   * @return resulting account balance
   */
  public Result<Long> topup(String id, long amount);

  /**
   * Withdraw funds from account
   *
   * @param id id of account to withdraw
   * @param amount amount of funds to withdraw
   * @return resulting account balance
   */
  public Result<Long> withdraw(String id, long amount);
}
