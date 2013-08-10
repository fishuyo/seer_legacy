
package com.fishuyo
package types

object UnionT {
type ![A] = A => Nothing
type !![A] = ![![A]]

type vv[T,U] = ![![T] with ![U]]
type v[T, U] = { type l[X] = !![X] <:< (T vv U) }

}
