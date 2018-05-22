package models

import com.google.gson.Gson
import java.util.*

/**
 * Created by grishberg on 21.05.18.
 */
class ChatListCommand(val cmd: Int,
                      val userId: String = "",
                      val chatList: List<Chat> = ArrayList<Chat>()) {
    companion object {
        val CREATE_CHAT = 1
        val INVITE_TO_CHAT = 2
        val ASSIGN_TO_CHAT = 3
        val GET_CHAT_LIST = 4
    }

    fun toByteArray(gson: Gson): ByteArray {
        return gson.toJson(this).toByteArray()
    }
}
