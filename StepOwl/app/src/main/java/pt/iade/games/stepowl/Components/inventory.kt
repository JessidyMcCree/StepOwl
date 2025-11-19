import androidx.compose.runtime.mutableStateListOf

private val inventory = mutableStateListOf<String?>(null, null, null)

fun addItem(item: String) {
    for (i in inventory.indices) {
        if (inventory[i] == null) {
            inventory[i] = item
            return
        }
    }
}

fun getItem(index: Int): String? {
    return inventory[index]
}
