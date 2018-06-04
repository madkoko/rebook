package it.polito.mad.koko.kokolab3.request

import com.google.firebase.database.IgnoreExtraProperties

import java.io.Serializable

@IgnoreExtraProperties
data class Request (
        var senderId: String? = null,
        var receiverId: String? = null,
        var bookId: String? = null,
        var bookName: String? = null,
        var bookImage: String? = null,
        var status: String? = null,
        var ratingSender: String? = null,
        //var commentSender: String? = null
        var ratingReceiver: String? = null
        //var commentReceiver: String? = null
) {
    //constructor()

    /*constructor(senderId: String, receiverId: String, bookId: String, bookName: String, bookImage: String, status: String, ratingSender: String, ratingReceiver: String) {
        this.senderId = senderId
        this.receiverId = receiverId
        this.bookId = bookId
        this.bookName = bookName
        this.bookImage = bookImage
        this.status = status
        this.ratingSender = ratingSender
        //this.commentSender = commentSender
        this.ratingReceiver = ratingReceiver
        //this.commentReceiver = commentReceiver
    }*/

    override fun toString(): String {
        return "Request{" +
                "Sender Id='" + senderId + '\''.toString() +
                ", Receiver Id='" + receiverId + '\''.toString() +
                ", Book Name='" + bookName + '\''.toString() +
                ", Book Image='" + bookImage + '\''.toString() +
                ", Status ='" + status + '\''.toString() +
                '}'.toString()
    }
}