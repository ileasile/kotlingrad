package edu.mcgill.shipshape

fun genTypeLevelVariables() = """
// This file was generated by Shipshape

package edu.umontreal.kotlingrad.typelevel

import edu.mcgill.kaliningraph.circuits.*
import edu.mcgill.kaliningraph.circuits.Dyad.*
import edu.umontreal.kotlingrad.typelevel.*

/**
 * The following code is a type-level encoding of the 3-element graded poset.
 *
 * For combination, i.e. any arithmetical operation, where P is a constant:
 *
 *        |    P      x      y      z      xy      xz      yz      xyz
 *    ----------------------------------------------------------------
 *    P   |    P      x      y      z      xy      xz      yz      xyz
 *    x   |    x      x      xy     xz     xy      xz      xyz     xyz
 *    y   |    y      xy     y      yz     xy      xyz     yz      xyz
 *    z   |    z      xz     yz     z      xyz     xz      yz      xyz
 *    xy  |    xy     xy     xy     xyz    xy      xyz     xyz     xyz
 *    xz  |    xz     xz     xyz    xz     xyz     xz      xyz     xyz
 *    yz  |    yz     xyz    yz     yz     xyz     xyz     yz      xyz
 *    xyz |    xyz    xyz    xyz    xyz    xyz     xyz     xyz     xyz
 *
 * Can be viewed as a Hasse Diagram: https://en.wikipedia.org/wiki/Hasse_diagram
 *
 * For application/invocation, where P is a constant:
 *
 *       |     P      x      y      z      xy      xz      yz      xyz
 *   -----------------------------------------------------------------
 *   P   |     P
 *   x   |            P                    y       z               yz
 *   y   |                   P             x               z       xz
 *   z   |                          P              x       y       xy
 *   xy  |            y      x             P                       z
 *   xz  |            z             x              P               y
 *   yz  |                   z      y                      P       x
 *   xyz |            yz     xz     xy     z       y       x       P
 */

${genFuns()}
""".trimMargin()

val numVars = 3

private fun genFuns() =
  listOf(
    "plus" to "+",
    "minus" to "-",
    "times" to "*",
    "div" to "÷"
  ).joinToString("\n") { (op, sop) -> genVarOp(op, sop) }

private fun genVarOp(op: String, sop: String) =
    allBinaryArrays().joinToString("\n", "", "\n") {
      it.joinToString("", "@JvmName(\"$op:", "\")") { if (it) "t" else "_" } +
        it.indices.joinToString(", ", "operator fun <", "> ") { "V$it: XO" } +
        genEx() +
        ".$op(e: " +
        genEx(it.map { if (it) "XX" else "OO" }) +
      ") = " +
        genEx(it.mapIndexed { i, b -> if (b) "XX" else "V$i" }) +
      "(this, e, op = `$sop`)"
    } + genConstOpEx(op, sop) + genExOpConst(op, sop)

private fun genEx(params: List<String> = (0 until numVars).map { "V$it" }) =
  "Ex" + params.joinToString(", ", "<", ">")

private fun genConstOpEx(op: String, sop: String) =
    "operator fun " +
      (0 until numVars).joinToString(", ", "<N: Number, ", ">") { "V$it: XO" } +
      " N.$op(e:" + genEx().let { "$it) = $it" } +
    "(Nt(this), e, op = `$sop`)\n"

private fun genExOpConst(op: String, sop: String) =
  "operator fun " +
    (0 until numVars).joinToString(", ", "<N: Number, ", "> ") { "V$it: XO" } +
    genEx().let { "$it.$op(n: N) = $it" } +
    "(this, Nt(n), op = `$sop`)\n"

fun allBinaryArrays(s: Int = numVars) =
  (0 until 2.shl(s - 1)).map { Integer.toBinaryString(it) }
    .map { it.padStart(s, '0').map { it != '0' } }