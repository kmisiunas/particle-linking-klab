package klab.track.analysis

import klab.track.ParticleTrack
import breeze.linalg.DenseMatrix
import com.misiunas.geoscala.vectors.Vec
import klab.track.geometry.position.Pos
import com.misiunas.geoscala.Point

/**
 * Estimates local spacial diffusion coefficient
 *
 * Version:
 *  - v1 - Based on Diffusion class
 *
 * User: kmisiunas
 * Date: 11/11/2013
 */
object DiffusionLocal {

  /** Estimates the local diffusion in matrix form.
    *
    * Works with 2D binning
    * @param spacing  spacing between grid points
    * @param r0 initial position of the grid spacing
    * @return map containing x, dx, dy, dz, stdx, stdy, stdz in a dorm of Dense matrices
    */
  def matrixForm(spacing: Vec, r0: Point = Point(0), method: Iterable[Pos] => List[Interest] = standardDiffusion):
    Iterable[ParticleTrack] => Map[String, DenseMatrix[Double]] =  ta => {
    val d = ta.flatMap( t => method(t.list) )
    val max: Pos = d.foldRight( Pos(Integer.MIN_VALUE, r0) )( (n,o) => n.pos.max(o) )
    val size: (Int, Int) = ( ((max - r0).x / spacing.x).floor.toInt + 1 , ((max - r0).y / spacing.y).floor.toInt + 1 )
    var n: DenseMatrix[Double] = DenseMatrix.zeros[Double](size._1, size._2)
    var sum: List[DenseMatrix[Double]] = List( DenseMatrix.zeros[Double](size._1, size._2)).flatMap(z => List(z,z.copy,z.copy))
    var std: List[DenseMatrix[Double]] = List( DenseMatrix.zeros[Double](size._1, size._2) ).flatMap(z => List(z,z.copy,z.copy))

    def findBin(p: Pos): Option[(Int, Int)] = {
      val ix = ((p-r0).x / spacing.x).floor.toInt
      val iy = ((p-r0).y / spacing.y).floor.toInt
      if (ix < 0 || iy < 0) return Option.empty
      else return Some( (ix, iy) )
    }
    // estimate sum and n first
    d.foreach(t => {
      val bin = findBin(t.pos)
      if (bin.nonEmpty) {
        val (xi, yi) = bin.get
        n.update(xi, yi, n(xi,yi) + 1)
        for (i <- 0 to 2) yield {
          sum(i).update(xi, yi, sum(i)(xi,yi) + t.Di(i+1))
        }
      }
    })
    // estimate means
    val mean: List[DenseMatrix[Double]] = sum.map( _ :/ n )
    // estimate std
    d.foreach(t => {
      val bin = findBin(t.pos)
      if (bin.nonEmpty) {
        val (xi, yi) = bin.get
        for (i <- 0 to 2) yield {
          std(i).update(xi, yi, std(i)(xi,yi) + Math.pow(t.Di(i+1) - mean(i)(xi, yi) , 2) ) // slow?
        }
      }
    })
    std = std.map( _ :/ (n + DenseMatrix.ones[Double](size._1, size._2)) )
    std.foreach(breeze.numerics.sqrt.inPlace( _ ))
    // create axis labels
    val axisX = DenseMatrix.zeros[Double](size._1,1)
    val axisY = DenseMatrix.zeros[Double](size._2,1)
    (0 until size._1).foreach( i => axisX.update(i,0, r0.x + spacing.x*i ))
    (0 until size._2).foreach( i => axisY.update(i,0, r0.y + spacing.y*i ))

    Map( "n" -> n,
      "dx" -> mean(0),
      "dy" -> mean(1),
      "dz" -> mean(2),
      "stdx" -> std(0),
      "stdy" -> std(1),
      "stdz" -> std(2),
      "axisx" -> axisX,
      "axisy" -> axisY)
  }
  
  
  private type Interest = Diffusion.Diffusion

  private val standardDiffusion: Iterable[Pos] => List[Interest] = Diffusion.di

}
