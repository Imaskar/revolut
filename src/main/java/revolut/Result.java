package revolut;

public class Result<T> {

  public final boolean success;
  public final T value;
  public final String comment;
  public final Throwable error;

  public Result(T value) {
    this.success = true;
    this.value = value;
    this.comment = null;
    this.error = null;
  }

  public Result(String comment, Throwable error) {
    this.comment = comment;
    this.error = error;
    this.success = false;
    this.value = null;
  }
}
