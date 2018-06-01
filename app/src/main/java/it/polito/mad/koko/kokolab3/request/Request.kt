package it.polito.mad.koko.kokolab3.request

/**
 * Created by Franci on 23/05/18.
 */
class Request {

    val TAG = "Request"

    var senderId: String? = null;
    var receiverId: String? = null;
    var bookId: String? = null;

    var bookName: String? = null;
    var bookImage: String? = null;
    var status: String? = null;

    var ratingSender: String? = null;
    var commentSender: String? = null;
    var ratingReceiver: String? = null;
    var commentReceiver: String? = null;

    constructor() : this("",
                    "",
                    "", "","")

    constructor(_senderId: String, _receiverId: String, _bookId: String, _bookName: String, _bookImage: String) {
        senderId = _senderId;
        receiverId = _receiverId;
        bookId = _bookId;

        bookName = _bookName;
        bookImage = _bookImage;
        status = "pending";

        ratingSender = null;
        commentSender = null;
        ratingReceiver = null;
        commentReceiver = null;
    }

    constructor(senderId: String, receiverId: String, bookId: String, bookName: String, bookImage: String, _ratingSender: String, _commentSender: String, _ratingReceiver: String, _commentReceiver: String):
            this(senderId, receiverId, bookId, bookName, bookImage) {
        this.ratingSender = _ratingSender;
        this.commentSender = _commentSender;
        this.ratingReceiver = _ratingReceiver;
        this.commentReceiver = _commentReceiver;
    }

    fun returnBook(){
        this.status = "return";
    }

    fun borrowBook(){
        this.status = "onBorrow";
    }

    fun rate(rating: String, comment: String){
        this.status = "rated";
        this.ratingSender = rating;
        this.commentSender = comment;
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