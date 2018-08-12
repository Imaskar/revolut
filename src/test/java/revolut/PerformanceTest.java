package revolut;

import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
public class PerformanceTest {

  public static final long AMOUNT = 100000l;
  private Bank bank = null;
  private static final String FROM_ID = "FROM";
  private static final String TO_ID = "TO";

  @Setup
  public void setup(){
    bank = new LockingBank();
    bank.createAccount(FROM_ID);
    bank.createAccount(TO_ID);
    bank.topup(FROM_ID, AMOUNT);
    bank.topup(TO_ID, AMOUNT);
  }

  @Test
  public void
  launchBenchmark() throws Exception {

    Options opt = new OptionsBuilder()
        .include(this.getClass().getName() + ".*")
        .mode (Mode.AverageTime)
        .timeUnit(TimeUnit.NANOSECONDS)
        .warmupTime(TimeValue.seconds(1))
        .warmupIterations(3)
        .measurementTime(TimeValue.seconds(1))
        .measurementIterations(1)
        .threads(4)
        .forks(3)
        .shouldFailOnError(true)
        .shouldDoGC(true)
        .jvmArgs("-Xmx1g", "-Xms1g")
        .build();

    new Runner(opt).run();
  }

  @Benchmark
  public long transfer(){
    Result<Long> transfer;
    transfer = bank.transfer(FROM_ID, TO_ID, 10l);
    transfer = bank.transfer(TO_ID,FROM_ID,10l);
    return transfer.success?transfer.value:0l;
  }

  /*
    Benchmark                 Mode  Cnt    Score    Error  Units
    PerformanceTest.transfer  avgt   30  567.862 ± 21.664  ns/op
   */

  @TearDown
  public void teardown(){
    Result<Long> b1 = bank.getBalance(FROM_ID);
    Result<Long> b2 = bank.getBalance(TO_ID);
    if (b1.success&&b2.success&&b1.value+b2.value==AMOUNT+AMOUNT){
      System.out.println("Balances are consistent");
    } else {
      throw new AssumptionViolatedException("Balances are not consistent");
    }
  }
}
