package klab.math.optimise

import breeze.linalg.DenseMatrix

/**
 *
 * https://en.wikipedia.org/wiki/Assignment_problem
 *
 * https://en.wikipedia.org/wiki/Hungarian_algorithm
 *
 * todo: fully use Breeze in the future
 *
 * Created by kmisiunas on 08/04/2014.
 */
object HungarianAlgorithm {

  def apply(cost: DenseMatrix[Double]): (Seq[Int], Double) = {

    breeze.optimize.linear.KuhnMunkres.extractMatching(
      (0 until cost.cols)
        .map( c => cost(::,c).toArray.toSeq )
        .toSeq
    )
  }

}
