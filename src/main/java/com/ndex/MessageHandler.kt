package com.ndex

import com.google.gson.Gson
import com.ndex.models.Chat
import com.ndex.models.ChatListCommand
import com.ndex.models.ChatListCommand.Companion.ASSIGN_TO_CHAT
import com.ndex.models.ChatListCommand.Companion.CREATE_CHAT
import com.ndex.models.ChatListCommand.Companion.GET_CHAT_LIST
import com.ndex.models.ChatListCommand.Companion.INVITE_TO_CHAT
import java.nio.charset.Charset
import java.util.*

/**
 * Created by grishberg on 21.05.18.
 */
class MessageHandler(private val chatManager: ChatManager,
                     private val userManager: UserManager) : OnNewMessageListener {
    var messageSender: MessageSender = MessageSenderStub()

    private val gson: Gson = Gson()
    override fun onMessageReceived(message: ByteArray, corrId: String?, rk: String?) {
        println("onMessageReceived: corrId = $corrId, rt = $rk")
        val msg = String(message, Charset.forName("UTF-8"))
        val cmd: ChatListCommand = gson.fromJson(msg, ChatListCommand::class.java)

        processCommand(cmd, rk)
    }

    private fun processCommand(cmd: ChatListCommand, rk: String?) {
        when (cmd.cmd) {
            CREATE_CHAT -> createChat(cmd, rk)
            GET_CHAT_LIST -> getChatList(cmd, rk)
            INVITE_TO_CHAT -> inviteToChat(cmd, rk)
            ASSIGN_TO_CHAT -> assignToChat(cmd, rk)
        }
    }

    private fun getChatList(cmd: ChatListCommand, rk: String?) {
        userManager.addNewUserIfNotExists(cmd.userId)

        val response = ChatListCommand(GET_CHAT_LIST, cmd.userId, chatManager.getChatList(cmd.userId))
                .toByteArray(gson)
        messageSender.sendMessage(response, rk, null)
    }

    private fun createChat(cmd: ChatListCommand, rk: String?) {
        val chatParam = cmd.chatList[0]
        val newChat = chatManager.createChat(cmd.userId, chatParam.name)
        val chatList = ArrayList<Chat>()
        chatList.add(newChat)
        val response = ChatListCommand(cmd = CREATE_CHAT, chatList = chatList)
                .toByteArray(gson)
        messageSender.sendMessage(response, rk, null)
    }

    private fun assignToChat(cmd: ChatListCommand, rk: String?) {
        val chatParam = cmd.chatList[0]
        chatManager.assignToChat(cmd.userId, chatParam.id)
    }

    private fun inviteToChat(cmd: ChatListCommand, rk: String?) {
        val chatParam = cmd.chatList[0]
        chatManager.inviteToChat(cmd.userId, chatParam.id, rk)
        val response = ChatListCommand(cmd = INVITE_TO_CHAT, chatList = cmd.chatList)
                .toByteArray(gson)
        messageSender.sendMessage(response, cmd.userId, null)
    }

    private inner class MessageSenderStub : MessageSender {
        override fun sendMessage(message: ByteArray?, userId: String?, corrId: String?) {
            /* stub */
        }
    }

}