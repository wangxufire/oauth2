package com.hd123.oauth2.common.tuple;

import static java.lang.String.format;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * <p>
 * A pair consisting of two elements.
 * </p>
 *
 * <p>
 * This class is an abstract implementation defining the basic API. It refers to
 * the elements as 'left' and 'right'. It also implements the {@code Map.Entry}
 * interface where the KEY is 'left' and the value is 'right'.
 * </p>
 *
 * <p>
 * Subclass implementations may be mutable or immutable. However, there is no
 * restriction on the type of the stored objects that may be stored. If mutable
 * objects are stored in the pair, then the pair itself effectively becomes
 * mutable.
 * </p>
 *
 * @param <L>
 *          the left element type
 * @param <R>
 *          the right element type
 */
public abstract class Pair<L, R> implements Comparable<Pair<L, R>>, Serializable {

  /** Serialization version */
  private static final long serialVersionUID = 4954918890077093841L;

  /**
   * <p>
   * Gets the left element from this pair.
   * </p>
   *
   * <p>
   * When treated as a KEY-value pair, this is the KEY.
   * </p>
   *
   * @return the left element, may be null
   */
  public abstract L getLeft();

  /**
   * <p>
   * Gets the right element from this pair.
   * </p>
   *
   * <p>
   * When treated as a KEY-value pair, this is the value.
   * </p>
   *
   * @return the right element, may be null
   */
  public abstract R getRight();

  // -----------------------------------------------------------------------
  /**
   * <p>
   * Compares the pair based on the left element followed by the right element.
   * The types must be {@code Comparable}.
   * </p>
   *
   * @param other
   *          the other pair, not null
   * @return negative if this is less, zero if equal, positive if greater
   */
  @Override
  public int compareTo(final Pair<L, R> other) {
    return new CompareToBuilder().append(getLeft(), other.getLeft())
        .append(getRight(), other.getRight()).toComparison();
  }

  /**
   * <p>
   * Compares this pair to another based on the two elements.
   * </p>
   *
   * @param obj
   *          the object to compare to, null returns false
   * @return true if the elements of the pair are equal
   */
  @Override
  public boolean equals(final Object obj) {
    return obj == this;
  }

  /**
   * <p>
   * Returns a suitable hash code. The hash code follows the definition in
   * {@code Map.Entry}.
   * </p>
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    // see Map.Entry API specification
    return (getLeft() == null ? 0 : getLeft().hashCode())
        ^ (getRight() == null ? 0 : getRight().hashCode());
  }

  /**
   * <p>
   * Returns a String representation of this pair using the format
   * {@code ($left,$right)}.
   * </p>
   *
   * @return a string describing this object, not null
   */
  @Override
  public String toString() {
    return new StringBuilder().append('(').append(getLeft()).append(',').append(getRight())
        .append(')').toString();
  }

  /**
   * <p>
   * Formats the receiver using the given format.
   * </p>
   *
   * <p>
   * This uses {@link java.util.Formattable} to perform the formatting. Two
   * variables may be used to embed the left and right elements. Use
   * {@code %1$s} for the left element (KEY) and {@code %2$s} for the right
   * element (value). The default format used by {@code toString()} is
   * {@code (%1$s,%2$s)}.
   * </p>
   *
   * @param format
   *          the format string, optionally containing {@code %1$s} and
   *          {@code %2$s}, not null
   * @return the formatted string, not null
   */
  public String toString(final String format) {
    return format(format, getLeft(), getRight());
  }

}
