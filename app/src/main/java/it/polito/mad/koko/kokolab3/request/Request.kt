package it.polito.mad.koko.kokolab3.request

/**
 * Created by Franci on 23/05/18.
 */
class Request (_senderID: String, _receiverID: String, _bookName: String) {

    private val TAG = "Request"

    private var senderID: String? = null;
    private var receiverID: String? = null;
    var bookName: String? = null;
    var bookImage: String? = null;
    private var status: String? = null;

    private var rating: Int? = null;
    private var comment: String? = null;

    constructor(senderID: String, receiverID: String, bookName: String, bookImage: String):
            this(senderID, receiverID, bookName) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.status = "pending";
    }

    constructor(senderID: String, receiverID: String, bookName: String, bookImage: String, rating: Int, comment: String):
            this(senderID, receiverID, bookName, bookImage) {
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
                "Sender ID='" + senderID + '\'' +
                ", Receiver ID='" + receiverID + '\'' +
                ", Book Name='" + bookName + '\'' +
                ", Book Image='" + bookImage + '\'' +
                ", Status ='" + status + '\'' +
                '}'
    }

}