package visualization

import java.io.PrintWriter

/**
 * @author sameer
 * @since 12/26/14.
 */
abstract class TSNEProcessing extends Plotting {
  def convertInputFileToTSNE(name: String, labelFile: String, dataFile: String): Unit = {
    println("Reading: " + name)
    val labelWriter = new PrintWriter(labelFile)
    val dataWriter = new PrintWriter(dataFile)
    val source = io.Source.fromFile(name)
    for (l <- source.getLines()) {
      val split1 = l.split("::")
      assert(split1.length == 2, s"Cannot split: {$l}.")
      val label = split1(0).trim
      val data = split1(1).split(",").map(_.trim).filterNot(_.isEmpty).map(_.toDouble)
      labelWriter.println(label)
      dataWriter.println(data.mkString("\t"))
    }
    source.close()
    labelWriter.flush()
    labelWriter.close()
    dataWriter.flush()
    dataWriter.close()
  }

  def generateScript(tsneOutput: String, labelFiles: Seq[(Prop, String)], output: String): Unit = {
    val clabels: Seq[(Prop, String)] = labelFiles.flatMap(cf => {
      val col = cf._1
      val f = cf._2
      val labels = io.Source.fromFile(f).getLines().toBuffer
      labels.map(l => col -> l)
    })
    val coords = io.Source.fromFile(tsneOutput).getLines().map(l => {
      val split = l.split(",")
      assert(split.length == 2)
      split(0).toDouble -> split(1).toDouble
    }).toBuffer
    assert(coords.length == clabels.length)
    val data = clabels.zip(coords).map(clc => Tuple4(clc._1._1, clc._1._2, clc._2._1, clc._2._2))

    val plotWriter = new PrintWriter(output)
    plotWriter.println(prefix)
    for (d <- data)
      plotWriter.println(render(d))
    plotWriter.println(postfix)
    plotWriter.flush()
    plotWriter.close()
  }

}

trait Plotting {
  type Color = String
  type Size = String
  type Prop = (Color, Size)

  def prefix: String

  def render(line: (Prop, String, Double, Double)): String

  def postfix: String
}

trait TikZ extends Plotting {

  def prefix: String =
    "\\documentclass{standalone}\n\\usepackage[usenames,dvipsnames,svgnames,table]{xcolor}\n\\usepackage{tikz}\n" +
      """\begin{document}
        |\begin{tikzpicture}[scale=1, transform shape]
        |  \newcommand{\colOne}{Maroon}
        |  \newcommand{\sizeOne}{\Large}
        |  \newcommand{\colTwo}{RoyalBlue}
        |  \newcommand{\sizeTwo}{}"""
        .stripMargin

  def render(line: (Prop, String, Double, Double)): String = {
    val color = line._1._1
    val size = line._1._2
    val label = line._2.replaceAll("\\&", "\\\\&")
    val x = line._3
    val y = line._4
    s"  \\node[$color] at (${x},${y}) {$size $label};"
  }

  def postfix: String =
    """
      |\end{tikzpicture}
      |\end{document}
    """.stripMargin

}

object TSNEProcessing extends TSNEProcessing with TikZ {

  def input(inputDir: String): Unit = {
    val filenames = Seq("catEmbeddings.txt", "wordEmbeddings.txt")
    filenames.map(inputDir + "/" + _).foreach(f => convertInputFileToTSNE(f, f + ".label", f + ".data"))
  }

  def output(inputDir: String): Unit = {
    // processing output files
    generateScript(inputDir + "/all.txt.coords",
      Seq(("\\colOne", "\\sizeOne") -> (inputDir + "catEmbeddings.txt.label"),
        ("\\colTwo", "\\sizeTwo") -> (inputDir + "wordEmbeddings.txt.label")),
      inputDir + "/tsne.tex")
  }

  def main(args: Array[String]): Unit = {
    assert(args.length == 1, "Please include the directory containing the generated predictions as an argument.")
    val inputDir = args(0)
    // input(inputDir)
    output(inputDir)
  }
}
