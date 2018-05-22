package models

import java.util.*

/**
 * Created by grishberg on 20.05.18.
 */
class Chat(val id: String, val name: String) {
    val users = ArrayList<String>()
}