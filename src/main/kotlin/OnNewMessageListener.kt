/**
 * Created by grishberg on 21.05.18.
 */
interface OnNewMessageListener {
    fun onMessageReceived(message: ByteArray, corrId: String?, rk: String?)
}