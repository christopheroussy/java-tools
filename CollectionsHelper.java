package playground;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Helps determine which collection to use.
 * 
 * NOTE: this should only be used to guide your choices, always read the Java documentation of the class of the returned instance.
 * 
 * This helper only covers some of the common List and Set collections.
 * 
 * @author Christophe Roussy
 */
public class CollectionsHelper {

  // https://www.geeksforgeeks.org/difference-between-linkedlist-and-linkedhashset-in-java/
  // https://javatutorial.net/choose-the-right-java-collection
  // http://anh.cs.luc.edu/363/notes/06A_Amortizing.html
  // https://www.developer.com/java/article.php/10922_3829891_2/Selecting-the-Best-Java-Collection-Class-for-Your-Application.htm
  // https://stackoverflow.com/questions/21974361/which-java-collection-should-i-use

  /**
   * NOTE: this should only be used to guide your choices, always read the Java documentation of the class of the returned instance.
   */
  public static final <T> Collection<T> buildCollectionWithDuplicates(

      final boolean isRandomAccessByPosition, // 1

      final boolean isForContainsChecks, // 2

      final boolean isForInsertOrRemoveAtStart, // 3

      final boolean isForQueue, // 4

      final boolean isSupportsNulls, // 5

      final int capacityHint // 6

  ) {
    // --- CAN HAVE DUPLICATES ---
    // Cannot be a SET.
    // https://stackoverflow.com/questions/322715/when-to-use-linkedlist-over-arraylist-in-java
    if (isRandomAccessByPosition || isForContainsChecks) {
      return new ArrayList<T>(capacityHint);
    }
    if (isForInsertOrRemoveAtStart) {
      if (isForQueue && !isSupportsNulls) {
        // https://docs.oracle.com/javase/6/docs/api/java/util/ArrayDeque.html
        // https://stackoverflow.com/questions/6163166/why-is-arraydeque-better-than-linkedlist
        return new ArrayDeque<T>(capacityHint);
      }
      return new LinkedList<T>(); // Consumes a lot of memory.
    }
    return new ArrayList<T>(capacityHint);
  }

  /**
   * NOTE: this should only be used to guide your choices, always read the Java documentation of the class of the returned instance.
   */
  public static final <T> Collection<T> buildCollectionWithDuplicatesWithThreads(

      final boolean isForMoreReadsThanWrites, // 1

      final int capacityHint // 2

  ) {
    // --- CAN HAVE DUPLICATES ---
    // Cannot be a SET.
    // https://stackoverflow.com/questions/322715/when-to-use-linkedlist-over-arraylist-in-java
    // https://stackoverflow.com/questions/28979488/difference-between-copyonwritearraylist-and-synchronizedlist
    if (isForMoreReadsThanWrites) {
      // COWAL does not throw ConcurrentModificationException, good for reads, bad for writes.
      return new CopyOnWriteArrayList<T>(); // COWAL
    }
    // Iterating must be synchronized.
    return Collections.synchronizedList(new ArrayList<T>(capacityHint));
  }

  /**
   * NOTE: this should only be used to guide your choices, always read the Java documentation of the class of the returned instance.
   */
  public static final <T> Collection<T> buildCollectionWithoutDuplicates(

      final boolean isPreserveInsertionOrder, // 1

      final boolean isForSortWithComparator, // 2

      final int capacityHint // 3

  ) {
    // --- NO DUPLICATES ---
    // Can be a SET.
    // https://stackoverflow.com/questions/20116660/hashset-vs-treeset-vs-linkedhashset-on-basis-of-adding-duplicate-value
    if (isPreserveInsertionOrder && isForSortWithComparator) {
      throw new AssertionError("Preserving insertion order and sorting by comparator is not compatible !");
    }
    if (isPreserveInsertionOrder) {
      return new LinkedHashSet<T>(capacityHint);
    }
    if (isForSortWithComparator) {
      return new TreeSet<T>();
    }
    return new HashSet<T>(capacityHint);
  }

  /**
   * NOTE: this should only be used to guide your choices, always read the Java documentation of the class of the returned instance.
   */
  public static final <T> Collection<T> buildCollectionWithoutDuplicatesWithThreads(

      final boolean isForMoreReadsThanWrites, // 1

      final boolean isForSortWithComparator // 2

  ) {
    // --- NO DUPLICATES ---
    // Can be a SET.
    // https://stackoverflow.com/questions/29249714/when-is-copyonwritearrayset-useful-to-achieve-thread-safe-hashset
    // https://stackoverflow.com/questions/6720396/different-types-of-thread-safe-sets-in-java
    if (isForSortWithComparator) {
      return new ConcurrentSkipListSet<T>();
    }
    if (isForMoreReadsThanWrites) {
      return new CopyOnWriteArraySet<T>();
    }
    return Collections.synchronizedSet(new HashSet<T>());
  }

  public static void main(final String[] args) {
    // Some examples:
    // --- WITH DUPLICATES.
    {
      final Collection<String> collection = buildCollectionWithDuplicates(true, false, true, true, true, 16);
      System.out.println(collection instanceof ArrayList<?>);
      System.out.println(collection.getClass().getName());
    }
    {
      final Collection<String> collection = buildCollectionWithDuplicatesWithThreads(true, 16);
      System.out.println(collection instanceof CopyOnWriteArrayList<?>);
      System.out.println(collection.getClass().getName());
    }
    {
      final Collection<String> collection = buildCollectionWithDuplicates(false, false, true, true, true, 16);
      System.out.println(collection instanceof LinkedList<?>);
      System.out.println(collection.getClass().getName());
    }
    // --- WITHOUT DUPLICATES.
    {
      final Collection<String> collection = buildCollectionWithoutDuplicates(true, false, 16);
      System.out.println(collection instanceof LinkedHashSet<?>);
      System.out.println(collection.getClass().getName());
    }
    {
      final Collection<String> collection = buildCollectionWithoutDuplicates(false, true, 16);
      System.out.println(collection instanceof TreeSet<?>);
      System.out.println(collection.getClass().getName());
    }
    {
      final Collection<String> collection = buildCollectionWithoutDuplicatesWithThreads(true, false);
      System.out.println(collection instanceof CopyOnWriteArraySet<?>);
      System.out.println(collection.getClass().getName());
    }
  }

}
