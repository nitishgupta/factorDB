package visualization

import java.io.{FilenameFilter, File}

import org.sameersingh.scalaplot.gnuplot.GnuplotPlotter
import org.sameersingh.scalaplot._

import scala.collection.mutable.ArrayBuffer

/**
 * @author sameer
 * @since 12/25/14.
 */
class PRCurves {

  val attrsOrder = Seq("A", "A+R", "A+BW", "A+C", "A+C+R", "A+C+BW")
  //Excluded: "A+R+UW", "A+C+UW", "A+R+BW", "A+C+R+UW", "A+UW", "A+C+R+BW"
  val ratingsOrder = Seq("R", "A+R", "R+UW", "R+BW", "R+C", "A+R+UW", "R+C+UW", "A+C+R+UW")
  // Excluded: , "A+C+R+BW", "A+C+R", "R+C+BW", "A+R+BW"

  def readFile(file: File): Seq[(Double, Boolean)] = {
    val source = io.Source.fromFile(file)
    val result = new ArrayBuffer[(Double, Boolean)]
    for (l <- source.getLines()) {
      val split = l.split("\t")
      assert(split.length == 2)
      assert(split(1) == "0" || split(1) == "1")
      result += split(0).toDouble -> (split(1) == "1")
    }
    source.close()
    result //.grouped(10).map(_.head).toSeq
  }

  def seriesFromFile(file: File): XYSeries = {
    val data = readFile(file)
    val m = new org.sameersingh.scalaplot.metrics.PrecRecallCurve(data)
    val s = new MemXYSeries(m.prChart("").data.serieses.head.points.toSeq, file.getName.drop(2))
    s.every = Some(7500)
    s.plotStyle = XYPlotStyle.LinesPoints
    s
  }

  def reorderSerieses(serieses: Seq[XYSeries], order: Seq[String]): Seq[XYSeries] = {
    order.map(n => serieses.find(_.name == n).get)
  }

  def generateAttrChart(files: Seq[File]): XYChart = {
    val series = files.map(f => seriesFromFile(f))
    val data = new XYData(reorderSerieses(series, attrsOrder): _*)
    val chart = new XYChart("PR Curve (Attributes)", data)
    chart.x.label = "Recall"
    chart.y.label = "Precision"
    //chart.monochrome = true
    chart.showLegend = true
    chart.legendPosX = LegendPosX.Center
    chart.legendPosY = LegendPosY.Bottom
    chart.size = Some(3.5,2.5)
    chart
  }

  def generateRatingsChart(files: Seq[File]): XYChart = {
    val series = files.map(f => seriesFromFile(f))
    val data = new XYData(reorderSerieses(series, ratingsOrder): _*)
    val chart = new XYChart("PR Curve (Ratings)", data)
    chart.x.label = "Recall"
    chart.y.label = "Precision"
    //chart.monochrome = true
    chart.showLegend = true
    chart.legendPosX = LegendPosX.Right
    chart.legendPosY = LegendPosY.Top
    chart.size = Some(3.5,2.5)
    chart
  }

  def generateCharts(dirName: String) = {
    val dir = new File(dirName)
    assert(dir.isDirectory, dirName + " is not a directory.")
    // generate output dir
    val outDirName = dirName + "/output/"
    new File(outDirName).mkdir()
    // Attribute PR Curve
    val attrFiles = dir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = name.startsWith("A-")
    }).toSeq.sortBy(_.getName)
    println("attr: " + attrFiles.map(_.getName).mkString(", "))
    val attrChart = generateAttrChart(attrFiles)
    GnuplotPlotter.pdf(attrChart, outDirName, dir.getName + "A")
    // Ratings PR Curve
    val ratingsFiles = dir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String): Boolean = name.startsWith("R-")
    }).toSeq.sortBy(_.getName)
    println("ratings: " + ratingsFiles.map(_.getName).mkString(", "))
    val ratingsChart = generateRatingsChart(ratingsFiles)
    GnuplotPlotter.pdf(ratingsChart, outDirName, dir.getName + "R")
  }
}

object PRCurves {
  def main(args: Array[String]): Unit = {
    assert(args.length == 1, "Please include the directory containing the generated predictions as an argument.")
    val inputDir = args(0)
    val curves = new PRCurves
    curves.generateCharts(args(0))
  }
}
