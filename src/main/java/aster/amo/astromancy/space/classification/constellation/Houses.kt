package aster.amo.astromancy.space.classification.constellation

enum class Houses(vararg constellations: Constellations) {
    CUPS(
        Constellations.THE_FOOL,
        Constellations.THE_MAGICIAN,
        Constellations.THE_HERMIT,
        Constellations.WHEEL_OF_FORTUNE,
        Constellations.TEMPERANCE,
        Constellations.THE_WORLD
    ),
    WANDS(
        Constellations.THE_EMPEROR,
        Constellations.THE_HIEROPHANT,
        Constellations.THE_LOVERS,
        Constellations.THE_HIGH_PRIESTESS,
        Constellations.THE_EMPRESS
    ),
    SWORDS(
        Constellations.THE_DEVIL,
        Constellations.THE_TOWER,
        Constellations.THE_STAR,
        Constellations.THE_MOON,
        Constellations.THE_SUN
    ),
    PENTACLES(
        Constellations.JUSTICE,
        Constellations.THE_HANGED_MAN,
        Constellations.DEATH,
        Constellations.STRENGTH,
        Constellations.THE_CHARIOT,
        Constellations.JUDGEMENT
    );

    val constellations: Array<Constellations>

    init {
        this.constellations = constellations as Array<Constellations>
    }



    companion object {
        fun getQuadrant(constellation: Constellations): Houses? {
            for (quadrant in entries) {
                for (c in quadrant.constellations) {
                    if (c === constellation) {
                        return quadrant
                    }
                }
            }
            return null
        }
    }
}
