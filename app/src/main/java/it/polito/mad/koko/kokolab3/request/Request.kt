package it.polito.mad.koko.kokolab3.request

/**
 * Created by Franci on 23/05/18.
 */
class Request {

    private val TAG = "Request"

    var senderId: String? = null;
    var receiverId: String? = null;
    var bookId: String? = null;

    var bookName: String? = null;
    var bookImage: String? = null;
    var status: String? = null;

    var rating: Int? = null;
    var comment: String? = null;

    constructor(_senderId: String, _receiverId: String, _bookId: String, _bookName: String, _bookImage: String) {
        senderId = _senderId;
        receiverId = _receiverId;
        bookId = _bookId;

        bookName = _bookName;
        bookImage = _bookImage;
        status = "pending";

        rating = null;
        comment = null;
    }

    constructor(senderId: String, receiverId: String, bookId: String, bookName: String, bookImage: String, rating: Int, comment: String):
            this(senderId, receiverId, bookId, bookName, bookImage) {
        this.rating = rating;
        this.comment = comment;
    }

    fun returnBook(){
        this.status = "return";
    }

    fun borrowBook(){
        this.status = "onBorrow";
    }

    fun rate(rating: Int, comment: String){
        this.status = "rated";
        this.rating = rating;
        this.comment = comment;
    }

    override fun toString(): String {
        return "Request{" +
                "Sender Id='" + senderId + '\'' +
                ", Receiver Id='" + receiverId + '\'' +
                ", Book Name='" + bookName + '\'' +
                ", Book Image='" + bookImage + '\'' +
                ", Status ='" + status + '\'' +
                '}'
    }

}