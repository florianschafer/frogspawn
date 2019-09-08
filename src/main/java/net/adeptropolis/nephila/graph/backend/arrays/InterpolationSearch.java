package net.adeptropolis.nephila.graph.backend.arrays;

/* NOTE
*  -  All search methods REQUIRE the input to be SORTED!!!
   -  Returns -1 if no match was found
*/

/**
 * Perform interpolation search on both big as well as primitive arrays.
 * Note that the underlying data structure is <b>ASSUMED TO BE SORTED</b>.
 */

public class InterpolationSearch {

  /**
   * Search in BigInts
   * @param ints Big integer array
   * @param key Search key
   * @param low Lower end of the search window. Inclusive.
   * @param high Lower end of the search window. Also inclusive.
   * @return Index of the first item matching the key. -1 otherwise.
   */

  // high is inclusive!
  public static long search(BigInts ints, int key, long low, long high) {

    long mid;

    int lowVal = ints.get(low);
    int highVal = ints.get(high);
    int midVal;

    while ((highVal != lowVal) && (key >= lowVal) && (key <= highVal)) {
      mid = low + ((key - lowVal) * (high - low) / (highVal - lowVal));
      midVal = ints.get(mid);
      if (key > midVal) {
        low = mid + 1;
        lowVal = ints.get(low);
      } else if (key < midVal) {
        high = mid - 1;
        highVal = ints.get(high);
      } else {
        return mid;
      }
    }

    if (key == lowVal)
      return low;
    else
      return -1;

  }

  /**
   * Search in int[]
   * @param array Primitive int array to be sorted
   * @param key Search key
   * @param low Lower end of the search window. Inclusive.
   * @param high Lower end of the search window. INCLUSIVE.
   * @return Index of the first item matching the key. -1 otherwise.
   */

  // high is inclusive!
  public static int search(int[] array, int key, int low, int high) {

    int mid;

    int lowVal = array[low];
    int highVal = array[high];
    int midVal;

    while ((highVal != lowVal) && (key >= lowVal) && (key <= highVal)) {
      mid = (int) (low + (((key - lowVal) * (long) (high - low)) / (highVal - lowVal)));
      midVal = array[mid];
      if (key > midVal) {
        low = mid + 1;
        lowVal = array[low];
      } else if (key < midVal) {
        high = mid - 1;
        highVal = array[high];
      } else {
        return mid;
      }
    }

    if (key == lowVal)
      return low;
    else
      return -1;

  }


}
