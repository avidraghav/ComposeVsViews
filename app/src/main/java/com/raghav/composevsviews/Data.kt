import androidx.compose.runtime.Stable

val itemList = listOf(
    Item(1),
    Item(2),
    Item(3),
    Item(4),
    Item(5),
    Item(6),
    Item(7),
    Item(8),
    Item(9),
    Item(10),
)

@Stable
data class Item(
    val id: Int
)
