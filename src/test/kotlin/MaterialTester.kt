import de.cypdashuhn.rooster.material.MaterialGroup
import de.cypdashuhn.rooster.material.MaterialSelector
import de.cypdashuhn.rooster.material.SelectorType

fun main() {
    var m = MaterialSelector.get(
        SelectorType.INCLUDE to MaterialGroup.IS_BRICK,
        SelectorType.REQUIRE to MaterialGroup.IS_SANDY_BLOCK
    )

    var l = MaterialSelector(MaterialGroup.IS_BRICK).include(MaterialGroup.IS_RED).get()

    println("")
}