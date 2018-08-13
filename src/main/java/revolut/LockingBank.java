package revolut;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class LockingBank implements Bank {

  private final Map<String,LockingAccount> accounts;
  private final Comparator<String> cmp = Comparator.naturalOrder();


  public LockingBank() {
    accounts = new ConcurrentHashMap<>();
  }


  @Override
  public Result<Void> createAccount(String id) {
    if (id==null || "".equals(id)) return new Result<>("Please provide correct id",null);
    LockingAccount newAcc = new LockingAccount(id);
    LockingAccount account = accounts.putIfAbsent(id, newAcc);
    if (account==null) {
      return new Result<>(null);
    } else {
      return new Result<>("Account with this id already exists.",null);
    }
  }

  @Override
  public Result<Long> getBalance(String id) {
    final LockingAccount account = accounts.get(id);
    if (account==null) {
      return new Result<>("Account with this id not found",null);
    } else {
      final Lock lock = account.getLock();
      try {
        boolean tryLock = lock.tryLock(30, TimeUnit.SECONDS);
        if (tryLock){
          return new Result<>(account.getBalance());
        } else {
          return new Result<>("Account is currently busy. Please try again later.",null);
        }
      } catch (InterruptedException e) {
        return new Result<>("Account is currently busy. Please try again later.",e);
      } finally {
        lock.unlock();
      }
    }
  }

  @Override
  public Result<Long> transfer(String from, String to, long amount) {
    if (amount<0l) return new Result<>("Transfer amount should be positive number", null);
    final LockingAccount accFrom = accounts.get(from);
    final LockingAccount accTo = accounts.get(to);
    if (accFrom == null) {
      return new Result<>("Account with id "+from+" not found", null);
    }  else if (accTo == null) {
      return new Result<>("Account with id "+to+" not found", null);
    } else {
      // Deadlock prevention start
      LockingAccount minId,maxId;
      final int compare = cmp.compare(from, to);
      if (compare==0){
        return new Result<>("From and To parameters should not be equal", null);
      } else if (compare<0){
        minId = accFrom;
        maxId = accTo;
      } else {
        minId = accTo;
        maxId = accFrom;
      }
      // Deadlock prevention end
      final Lock l1 = minId.getLock();
      final Lock l2 = maxId.getLock();
      try {
        boolean tryLock1 = l1.tryLock(30, TimeUnit.SECONDS);
        if (tryLock1){
          boolean tryLock2 = l2.tryLock(30,TimeUnit.SECONDS);
          if (tryLock2){
            long balance1 = accFrom.getBalance();
            long balance2 = accTo.getBalance();
            try {
              balance1 = Math.subtractExact(balance1, amount);
            } catch (ArithmeticException ae){
              return new Result<>("Account "+from+" has insufficient funds", ae);
            }
            try {
              balance2 = Math.addExact(balance2, amount);
            } catch (ArithmeticException ae){
              return new Result<>("Account "+to+" is too full", ae);
            }
            if (balance1<0l) {
              return new Result<>("Account "+from+" has insufficient funds", null);
            } else {
              accFrom.setBalance(balance1);
              accTo.setBalance(balance2);
              return new Result<>(balance1);
            }
          }
        }

      } catch (InterruptedException e) {
        return new Result<>("Account is currently busy. Please try again later.", null);
      } finally {
        l1.unlock();
        l2.unlock();
      }
    }


    return null;
  }

  @Override
  public Result<Long> topup(String id, long amount) {
    final LockingAccount account = accounts.get(id);
    if (account == null) {
      return new Result<>("Account with this id not found", null);
    } else {
      final Lock lock = account.getLock();
      try {
        boolean tryLock = lock.tryLock(30, TimeUnit.SECONDS);
        if (tryLock) {
          long balance = account.getBalance();
          balance = Math.addExact(balance, amount);
          account.setBalance(balance);
          return new Result<>(balance);
        } else {
          return new Result<>("Account is currently busy. Please try again later.", null);
        }
      } catch (ArithmeticException ae) {
        return new Result<>("This amount is too big to add", null);
      } catch (InterruptedException e) {
        return new Result<>("Account is currently busy. Please try again later.", e);
      } finally {
        lock.unlock();
      }
    }
  }

  @Override
  public Result<Long> withdraw(String id, long amount) {
    final LockingAccount account = accounts.get(id);
    if (account == null) {
      return new Result<>("Account with this id not found", null);
    } else {
      final Lock lock = account.getLock();
      try {
        boolean tryLock = lock.tryLock(30, TimeUnit.SECONDS);
        if (tryLock) {
          long balance = account.getBalance();
          balance = Math.subtractExact(balance, amount);
          if (balance<0){
            return new Result<>("This amount is too big to withdraw", null);
          } else {
            account.setBalance(balance);
            return new Result<>(balance);
          }
        } else {
          return new Result<>("Account is currently busy. Please try again later.", null);
        }
      } catch (ArithmeticException ae) {
        return new Result<>("This amount is too big to withdraw", null);
      } catch (InterruptedException e) {
        return new Result<>("Account is currently busy. Please try again later.", e);
      } finally {
        lock.unlock();
      }
    }
  }
}
