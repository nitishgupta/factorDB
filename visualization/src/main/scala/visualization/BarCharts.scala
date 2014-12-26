package visualization

import org.sameersingh.scalaplot.gnuplot.GnuplotPlotter
import org.sameersingh.scalaplot.{MemBarSeries, BarData, BarChart}

import scala.collection.mutable.ArrayBuffer

/**
 * @author sameer
 * @since 12/26/14.
 */
class BarCharts {

  val attr = new ArrayBuffer[(String, Double)]()
  attr += "A" -> 81.5
  attr += "A+C" -> 83.9
  attr += "A+R" -> 80.8
  attr += "A+BW" -> 83.5
  //attr += "A+UW" -> 81.5
  attr += "A+C+R" -> 83.4
  attr += "A+C+BW" -> 83.6
  attr += "A+C+UW" -> 83.9
  attr += "A+R+BW" -> 83.5
  attr += "A+R+UW" -> 80.7
  attr += "A+C+R+BW" -> 83.7
  attr += "A+C+R+UW" -> 83.3

  val ratings = new ArrayBuffer[(String, Double)]()
  ratings += "R" -> 71.3
  ratings += "R+C" -> 73.6
  ratings += "R+A" -> 72.3
  ratings += "R+BW" -> 72.4
  ratings += "R+UW" -> 79.2
  ratings += "R+A+C" -> 72.5
  ratings += "R+C+BW" -> 72.6
  ratings += "R+C+UW" -> 80.4
  ratings += "R+A+BW" -> 72.3
  ratings += "R+A+UW" -> 79.2
  ratings += "R+A+C+BW" -> 72.3
  ratings += "R+A+C+UW" -> 79.9

  def attributesPlot: BarChart = {
    val ss = new MemBarSeries(attr.map(_._2))
    val d = new BarData((i) => attr(i)._1, Seq(ss))
    val c = new BarChart("Heldout F1 (Attributes)", d)
    c.y.label = "F1"
    c.x.label = "Factorization Models"
    c
  }

  def ratingsPlot: BarChart = {
    val ss = new MemBarSeries(ratings.map(_._2))
    val d = new BarData((i) => ratings(i)._1, Seq(ss))
    val c = new BarChart("Heldout F1 (Ratings)", d)
    c.y.label = "F1"
    c.x.label = "Factorization Models"
    c
  }
}

object BarCharts {
  def main(args: Array[String]): Unit = {
    assert(args.length == 1, "Please include the directory to write output in as an argument.")
    val outputDir = args(0)
    val charts = new BarCharts
    GnuplotPlotter.pdf(charts.attributesPlot, outputDir, "Attributes")
    GnuplotPlotter.pdf(charts.ratingsPlot, outputDir, "Ratings")
  }
}
