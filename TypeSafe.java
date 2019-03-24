package playground;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.annotation.CheckReturnValue;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Description: Helps avoiding common Java typing pitfalls. <br>
 * Keywords csv: typing, type safety, compile, compiler
 * 
 * <p>
 * NOTE: Use <code>Ctrl+Shift+G</code> on the pitfall method you want to find occurences (scroll down to your packages).
 * <p>
 * NOTE: Findbugs may also find some of these bugs.
 * 
 * @author croussy
 * 
 * @author NOTE: This is a neutral tool, it could be open-source.
 */
@CheckReturnValue
public final class TypeSafe<T> {
  private final T element;

  private TypeSafe(final T element) {
    // NOTE: assert not null is used to avoid late NullPointerExceptions.
    assert element != null;
    this.element = element;
  }

  /**
   * NEVER USE THIS METHOD, use {@link #equalsTyped(Object)} instead (shows Object due to type erasure) !
   * 
   * Overriding of equals for this class is meant to enforce the use of equalsTyped in case equals was used by mistake.
   */
  @Override
  @Deprecated
  public boolean equals(final Object other) {
    assert false;
    // Always break
    throw new IllegalArgumentException("Use equalsTyped instead of equals !");
  }

  /**
   * <p>
   * <code>new TypeSafe<String>(first).equalsTyped(second)</code>
   * <p>
   * NOTE: for Strings you can use the simpler {@link #equalsString(String, String)} method.
   * <p>
   * NOTE: Cannot be named equals because of type erasure.
   */
  @SuppressWarnings("null")
  public boolean equalsTyped(final @NonNull T other) {
    return element.equals(other);
  }

  @Override
  public int hashCode() {
    // Should not be used.
    assert false;
    return -1;
  }

  public static <T> TypeSafe<T> build(final T element) {
    return new TypeSafe<>(element);
  }

  public static <T> boolean collectionContains(final Collection<T> collection, final T element) {
    return collection.contains(element);
  }

  public static <T> boolean collectionContainsAll(final Collection<T> collection, final Collection<T> elements) {
    assert elements != null;
    return collection.containsAll(elements);
  }

  public static <T> boolean collectionIntersects(final Collection<T> collection, final Collection<T> elements) {
    assert collection != null;
    assert elements != null;
    // Make a copy to avoid side-effects.
    final HashSet<T> copy = new HashSet<T>(collection);
    copy.retainAll(elements);
    return !copy.isEmpty();
  }

  public static <T> boolean collectionRemove(final Collection<T> collection, final T element) {
    return collection.remove(element);
  }

  public static <T> boolean collectionRemoveAll(final Collection<T> collection, final Collection<T> elements) {
    assert elements != null;
    return collection.removeAll(elements);
  }

  public static <T> boolean collectionRetainAll(final Collection<T> collection, final Collection<T> elements) {
    assert elements != null;
    return collection.retainAll(elements);
  }

  /**
   * For types different from String use {@link #equalsTyped(String, String)}.
   */
  public static boolean equalsString(final String s1, final String s2) {
    return s1.equals(s2);
  }

  /**
   * Type-safe equals check for UUID for convenience.
   */
  public static boolean equalsUuid(final UUID u1, final UUID u2) {
    return u1.equals(u2);
  }

  // Some examples:
  public static void main(final String[] args) {
    {
      final String bob = "Bob";
      final String tom = "Tom";
      bob.equals(tom);
      // Equals breaks typing by accepting an Integer !
      // bob.equals(1);
      TypeSafe.build(bob).equalsTyped(tom);
      // TypeSafe.build(bob).equalsTyped(1);
      // TypeSafe.build(bob).equals(1);
      TypeSafe.equalsString(bob, tom);
    }
    {
      final Collection<String> collection = new ArrayList<>();
      TypeSafe.collectionContains(collection, "bob");
    }
    {
      final Map<String, Integer> map = new HashMap<>();
      TypeSafe.mapGet(map, "");
      TypeSafe.mapContainsKey(map, "");
    }
  }

  public static <K, V> boolean mapContainsKey(final Map<K, V> map, final K key) {
    assert key != null;
    return map.containsKey(key);
  }

  public static <K, V> V mapGet(final Map<K, V> map, final K key) {
    assert key != null;
    return map.get(key);
  }

  public static <K, V> V mapGetExpectValueNotNull(final Map<K, V> map, final K key) {
    assert key != null;
    final V value = map.get(key);
    assert value != null;
    return value;
  }

  public static <K, V> V mapRemove(final Map<K, V> map, final K key) {
    assert key != null;
    return map.remove(key);
  }

  public static String simpleDateFormatDoFormat(final SimpleDateFormat sdf, final Date date) {
    assert sdf != null;
    assert date != null;
    // Incredible, but this can take and Object, thus needs to be protected.
    return sdf.format(date);
  }
}
