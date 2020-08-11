package vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VKMessage {
    @SerializedName("user_id")
    Integer userID;
    @SerializedName("owner_ids")
    List<Long> ownerIDs;
    @SerializedName("body")
    String message;

    Integer readState;
    Integer out;
    Integer id;
    String title;
    long date;

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setOwnerIDs(List<Long> ownerIDs) {
        this.ownerIDs = ownerIDs;
    }

    public void setReadState(Integer readState) {
        this.readState = readState;
    }

    public void setOut(Integer out) {
        this.out = out;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
