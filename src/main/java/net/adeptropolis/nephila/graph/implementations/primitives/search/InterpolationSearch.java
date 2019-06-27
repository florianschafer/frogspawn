package net.adeptropolis.nephila.graph.implementations.primitives.search;

/* NOTE
*  -  All search methods REQUIRE the input to be SORTED!!!
   -  Returns -1 if no match was found
*/

import net.adeptropolis.nephila.graph.implementations.primitives.Ints;

public class InterpolationSearch {

  // high is inclusive!
  public static long search(Ints ints, int key, long low, long high) {

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

  // high is inclusive!
  public static int search(int[] array, int key, int low, int high) {

    int mid;

    int lowVal = array[low];
    int highVal = array[high];
    int midVal;

    while ((highVal != lowVal) && (key >= lowVal) && (key <= highVal)) {
      mid = (int)(low + (((key - lowVal) * (long)(high - low)) / (highVal - lowVal)));
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
