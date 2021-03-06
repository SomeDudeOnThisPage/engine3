package engine3.util;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ArrayUtils {
  @SuppressWarnings("unused")
  public static float[] toPrimitiveF(Float[] list) {
    float[] array = new float[list.length];
    int i = 0;

    for (Float f : list) {
      array[i++] = (f != null ? f : Float.NaN);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static float[] toPrimitiveF(ArrayList<Float> list) {
    float[] array = new float[list.size()];
    int i = 0;

    for (Float f : list) {
      array[i++] = (f != null ? f : Float.NaN);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static int[] toPrimitiveI(Integer[] list) {
    int[] array = new int[list.length];
    int j = 0;

    for (Integer i : list) {
      array[j++] = (i != null ? i : 0);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static int[] toPrimitiveI(ArrayList<Integer> list) {
    int[] array = new int[list.size()];
    int j = 0;

    for (Integer i : list) {
      array[j++] = (i != null ? i : 0);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static Float[] toBoxedArrayF(List<Float> list) {
    Float[] array = new Float[list.size()];
    int i = 0;

    for (Float f : list) {
      array[i++] = (f != null ? f : Float.NaN);
    }

    return array;
  }

  @SuppressWarnings("unused")
  public static Integer[] toBoxedArrayI(List<Integer> list) {
    Integer[] array = new Integer[list.size()];
    int i = 0;

    for (Integer f : list) {
      array[i++] = (f != null ? f : 0);
    }

    return array;
  }
}
