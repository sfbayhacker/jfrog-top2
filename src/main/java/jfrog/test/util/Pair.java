package jfrog.test.util;

/**
 * A generic holder for a pair of objects
 * 
 * @author arun
 */
public class Pair<A, B> {
  public A a;
  public B b;

  public Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Pair))
      return false;

    @SuppressWarnings("unchecked")
    final Pair<A, B> pair = (Pair<A, B>) o;

    if (a != null ? !a.equals(pair.a) : pair.a != null)
      return false;
    if (b != null ? !b.equals(pair.b) : pair.b != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    result = (a != null ? a.hashCode() : 0);
    result = 29 * result + (b != null ? b.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "(" + a + ", " + b + ")";
  }
}