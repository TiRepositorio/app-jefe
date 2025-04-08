package apolo.jefes.com.utilidades

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.tan
import kotlin.random.Random


class Clave {


    companion object{
        fun smvClave():String {
            val random = Random.Default
            val clave = random.nextInt(10000000, 99999999)
            return  clave.toString()
        }

        fun contraClave(clave: String) : String {

            val iP1 = Integer.parseInt(clave.substring(1, 8))
            val iP2 = Integer.parseInt(clave.substring(4, 8))
            val iP3 = Integer.parseInt(clave.substring(0, 8))
            val iP4 = Integer.parseInt(clave.substring(2, 8))
            val iP5 = Integer.parseInt(clave.substring(0, 5))
            val iP6 = Integer.parseInt(clave.substring(3, 8))

            var sContraClave : String = smvRoundAbs(cos(iP3.toDouble()), 100).toString()

            sContraClave += smvRoundAbs(cos(iP1.toDouble()), 100)
            sContraClave += smvRoundAbs(cos(iP4.toDouble()), 10)
            sContraClave += smvRoundAbs(tan(iP3.toDouble()), 10)
            sContraClave += smvRoundAbs(cos(iP6.toDouble()), 100)
            sContraClave += smvRoundAbs(cos(iP2.toDouble()), 10)
            sContraClave += smvRoundAbs(cos(iP5.toDouble()), 10)
            sContraClave = sContraClave.substring(0, 8)

            return sContraClave

        }

        private fun smvRoundAbs(subClave: Double, multiplo: Int) : Int {
            return abs((subClave * multiplo).roundToInt())
        }

    }

}