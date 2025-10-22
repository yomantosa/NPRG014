package h1
import scala.language.implicitConversions

// Add necessary class and object definitions in order to make the statements in the main work.
case class Complex(re: Double, im: Double):

  def +(that: Complex): Complex = Complex(this.re + that.re, this.im + that.im)

  def *(that: Complex): Complex =
    Complex(
      this.re * that.re - this.im * that.im,
      this.re * that.im + this.im * that.re
    )

  def unary_- : Complex = Complex(-re, -im)

  override def toString: String =
    def clean(x: Double): String =
      if x.isValidInt then x.toInt.toString else x.toString

    (re, im) match
      case (0, 0) => "0"
      case (_, 0) => s"${clean(re)}"
      case (0, _) => s"${clean(im)}i"
      case (_, _) => s"${clean(re)}${if (im >= 0) "+" else ""}${clean(im)}i"


object Complex:
  def apply(re: Int, im: Int): Complex = new Complex(re, im)

object I extends Complex(0, 1)

object ComplexNumbers:
	given Conversion[Int, Complex] with
		def apply(x: Int): Complex = Complex(x, 0)

	def main(args: Array[String]): Unit =
		println(Complex(1,2)) // 1+2i

		println(1 + 2*I + I*3 + 2) // 3+5i

		val c = (2+3*I + 1 + 4*I) * I
		println(-c) // 7-3i