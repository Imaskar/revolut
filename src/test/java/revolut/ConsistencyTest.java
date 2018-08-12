package revolut;

import org.junit.Before;
import org.junit.Test;
import revolut.Bank;
import revolut.LockingBank;
import revolut.Result;

import static org.junit.Assert.*;

public class ConsistencyTest {

  public static final String UNARY_TEST = "unaryTest";
  public static final String TRANSFER_TEST_1 = "transferTest1";
  public static final String TRANSFER_TEST_2 = "transferTest2";

  public ConsistencyTest(){}

  private Bank bank;

  @Before
  public void setUp() {
    bank = new LockingBank();
  }

  @Test
  public void testCreateAccounts() {
    Result<Void> account;
    account = bank.createAccount("");
    assertEquals(false,account.success);
    account = bank.createAccount("1");
    assertEquals(true,account.success);
    account = bank.createAccount("1");
    assertEquals(false,account.success);
    account = bank.createAccount("2");
    assertEquals(true,account.success);
  }

  @Test
  public void testUnaryOperations() {
    Result<Void> account;
    account = bank.createAccount(UNARY_TEST);
    Result<Long> result;
    result = bank.getBalance(UNARY_TEST);
    assertEquals(true,result.success);
    assertEquals(0l,(long)result.value);
    result = bank.topup(UNARY_TEST,10l);
    assertEquals(true,result.success);
    assertEquals(10l,(long)result.value);
    result = bank.withdraw(UNARY_TEST,20l);
    assertEquals(false,result.success);
    result = bank.withdraw(UNARY_TEST,10l);
    assertEquals(true,result.success);
    assertEquals(0l,(long)result.value);
  }

  @Test
  public void testTransfer() {
    Result<Void> a1,a2;
    a1 = bank.createAccount(TRANSFER_TEST_1);
    a2 = bank.createAccount(TRANSFER_TEST_2);
    Result<Long> result;
    result = bank.topup(TRANSFER_TEST_1,100);
    assertEquals(true,result.success);
    assertEquals(100l,(long)result.value);
    result = bank.topup(TRANSFER_TEST_2,100);
    assertEquals(true,result.success);
    assertEquals(100l,(long)result.value);

    result = bank.transfer(TRANSFER_TEST_1,TRANSFER_TEST_2, -20l);
    assertEquals(false,result.success);

    result = bank.transfer(TRANSFER_TEST_1,TRANSFER_TEST_2, 20l);
    assertEquals(true,result.success);
    assertEquals(80l,(long)result.value);
    result = bank.getBalance(TRANSFER_TEST_2);
    assertEquals(true,result.success);
    assertEquals(120l,(long)result.value);

    result = bank.transfer(TRANSFER_TEST_2,TRANSFER_TEST_1, 200l);
    assertEquals(false,result.success);
    result = bank.getBalance(TRANSFER_TEST_2);
    assertEquals(true,result.success);
    assertEquals(120l,(long)result.value);
    result = bank.getBalance(TRANSFER_TEST_1);
    assertEquals(true,result.success);
    assertEquals(80l,(long)result.value);
  }
}
