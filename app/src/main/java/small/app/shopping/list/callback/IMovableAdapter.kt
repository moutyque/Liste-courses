package small.app.shopping.list.callback

interface IMovableAdapter {
    /**
     * Is the move action allowed
     */
    fun canMove(): Boolean

    /**
     * Defined if items can move in the adapter
     */
    fun setMove(b: Boolean)

    /**
     * Get the list of object inside the adapter
     */
    fun getAdapterList(): List<Any>

    /**
     * What happened when an item move accross the RV
     */
    fun onItemMove(initialPosition: Int, targetPosition: Int)

    /**
     * What happend when the item end its move
     */
    fun onDragEnd()
}