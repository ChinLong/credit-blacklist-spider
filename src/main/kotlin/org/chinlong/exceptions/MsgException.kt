package org.chinlong.exceptions

class MsgException(message: String) : Exception(message) {

    override fun toString(): String {
        return message.toString()
    }
}