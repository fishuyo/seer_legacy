// package seer 

// import scala.quoted._
// import scala.quoted.staging._


// object Macro {

//   inline def print(inline expr:Any*):Unit = ${printImpl('expr)}

//   def printImpl(expr: Expr[Any])(using QuoteContext) = 
//     '{ println("Value of " + ${Expr(expr.show)} + " is " + $expr) }






//   inline def printAll(inline expr:Any*):Unit = ${printAllImpl('expr)}

//   def printAllImpl(exprs: Expr[Seq[Any]])(using QuoteContext) = {

//     def showWithValue(e: Expr[_]): Expr[String] = '{${Expr(e.show)} + " = " + $e}
    
//     val stringExps: Seq[Expr[String]] = exprs match 
//       case Varargs(es) => 
//         es.map {
//           case Const(s: String) => Expr(s)
//           case e => showWithValue(e)
//         }
//       case e => List(showWithValue(e))
    
//     val concatenatedStringsExp = stringExps
//       .reduceOption((e1, e2) => '{$e1 + ", " + $e2})
//       .getOrElse('{""})
      
//     '{println($concatenatedStringsExp)}

//   }




//   // staging test

//   // make available the necessary toolbox for runtime code generation
//   given Toolbox = Toolbox.make(getClass.getClassLoader)


//   val f: Array[Int] => Int = run {
//     val stagedSum: Expr[Array[Int] => Int] = '{ (arr: Array[Int]) => arr.head }
//     println(stagedSum.show) // Prints "(arr: Array[Int]) => { var sum = 0; ... }"
//     stagedSum
//   }

//   def dof(a:Array[Int]) = println(f(a))

// }


