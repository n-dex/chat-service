package com.ndex

import com.ndex.models.User
import java.util.*

/**
 * Created by grishberg on 24.05.18.
 */
class UserManager {
    private val usersMap = HashMap<String, User>()
    /**
     * Adds new user with generated name if not exists.
     */
    fun addNewUserIfNotExists(userId: String) {
        if (usersMap[userId] == null) {
            val generatedName = "User#${usersMap.size + 1}"
            usersMap[userId] = User(userId, generatedName)
        }
    }

    fun updateUserName(userId: String, name: String) {
        val user = usersMap[userId]
        if (user != null) {
            user.name = name
        }
    }

    fun addUser(user: User) {
        usersMap[user.id] = user
    }

    fun getUserById(userId: String): User? = usersMap[userId]
}