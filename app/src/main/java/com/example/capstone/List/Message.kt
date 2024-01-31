package com.example.capstone.List

class Message {
    var message : String? = null
    var senderID : String? = null
    var dateSent : String? = null

    constructor(){}

    constructor(message: String?, senderID: String?, dateSent: String?){
        this.message = message
        this.senderID = senderID
        this.dateSent = dateSent
    }
}