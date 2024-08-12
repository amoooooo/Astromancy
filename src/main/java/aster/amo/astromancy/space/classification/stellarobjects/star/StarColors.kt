package aster.amo.astromancy.space.classification.stellarobjects.star

import java.awt.Color

enum class StarColors(val color: Color) {
    O(Color(40, 145, 250, 255)),
    B(Color(144, 178, 245, 255)),
    A(Color(174, 184, 245, 255)),
    F(Color(245, 221, 159, 255)),
    G(Color(245, 210, 122, 255)),
    K(Color(245, 175, 122, 255)),
    M(Color(242, 142, 102, 255)),
    EXOTIC(Color(85, 232, 85, 255)),
    PURE(Color(197, 242, 172, 255)),
    EMPTY(Color(102, 3, 252, 255)),
    HELL(Color(227, 38, 0, 255)),
    NORMAL(Color(255, 255, 255, 255))
}
