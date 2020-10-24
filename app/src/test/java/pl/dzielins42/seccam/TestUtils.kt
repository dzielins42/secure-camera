package pl.dzielins42.seccam

fun evaluate(assertBlock: () -> Unit): Boolean {
    assertBlock.invoke()
    return true
}