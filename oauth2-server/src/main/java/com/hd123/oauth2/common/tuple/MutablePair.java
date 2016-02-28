package com.hd123.oauth2.common.tuple;

/**
 * <p>
 * A mutable pair consisting of two {@code Object} elements.
 * </p>
 *
 * <p>
 * Not #ThreadSafe#
 * </p>
 *
 * @param <L>
 *          the left element type
 * @param <R>
 *          the right element type
 */
public class MutablePair<L, R> extends Pair<L, R> {

  /** Serialization version */
  private static final long serialVersionUID = -2392359013378476723L;

  /** Left object */
  public L left;
  /** Right object */
  public R right;

  /**
   * <p>
   * Obtains an immutable pair of from two objects inferring the generic types.
   * </p>
   *
   * <p>
   * This factory allows the pair to be created using inference to obtain the
   * generic types.
   * </p>
   *
   * @param <L>
   *          the left element type
   * @param <R>
   *          the right element type
   * @param left
   *          the left element, may be null
   * @param right
   *          the right element, may be null
   * @return a pair formed from the two parameters, not null
   */
  public static <L, R> MutablePair<L, R> of(final L left, final R right) {
    return new MutablePair<L, R>(left, right);
  }

  /**
   * Create a new pair instance of two nulls.
   */
  public MutablePair() {
    super();
  }

  /**
   * Create a new pair instance.
   *
   * @param left
   *          the left value, may be null
   * @param right
   *          the right value, may be null
   */
  public MutablePair(final L left, final R right) {
    super();
    this.left = left;
    this.right = right;
  }

  // -----------------------------------------------------------------------
  /**
   * {@inheritDoc}
   */
  @Override
  public L getLeft() {
    return left;
  }

  /**
   * Sets the left element of the pair.
   *
   * @param left
   *          the new value of the left element, may be null
   */
  public void setLeft(final L left) {
    this.left = left;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public R getRight() {
    return right;
  }

  /**
   * Sets the right element of the pair.
   *
   * @param right
   *          the new value of the right element, may be null
   */
  public void setRight(final R right) {
    this.right = right;
  }

}
