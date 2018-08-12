package revolut;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockingAccount extends BasicAccount {

  private final ReentrantLock lock;

  public LockingAccount(String id) {
    super(id);
    this.lock = new ReentrantLock();
  }

  public Lock getLock(){
    return lock;
  }
}
