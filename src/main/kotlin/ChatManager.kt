import models.Chat
import java.util.*

/**
 * Created by grishberg on 20.05.18.
 */
class ChatManager() {

    private val chatsMap = HashMap<String, Chat>()
    private val chats = HashMap<String, ArrayList<Chat>>()

    fun createChat(owner: String, name: String): Chat {
        val uuid = UUID.randomUUID()
        val newChat = Chat(uuid.toString(), name)


        var chatsForUser = chats[owner]
        if (chatsForUser == null) {
            chatsForUser = ArrayList<Chat>()
            chats[owner] = chatsForUser
        }
        chatsForUser.add(newChat)
        chatsMap[uuid.toString()] = newChat
        return newChat
    }

    fun getChatList(userId: String): List<Chat> {
        return chats[userId] ?: return ArrayList()
    }

    fun assignToChat(userId: String, chatId: String) {
        val chat = chatsMap[chatId]
        if (chat != null) {
            chat.users.add(userId)
        }
    }

    fun inviteToChat(userId: String, chatId: String, inviteFromUserId: String?) {
        val chat = chatsMap[chatId]
        if (chat != null) {
            chat.users.add(userId)
        }
    }
}
