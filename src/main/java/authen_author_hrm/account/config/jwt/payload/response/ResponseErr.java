package authen_author_hrm.account.config.jwt.payload.response;

public class ResponseErr {
    private static ResponseErr instant;
    private ResponseErr() {}
    public synchronized static ResponseErr getInstant() {
        if(instant == null){
            instant = new ResponseErr();
        }
        return instant;
    }
    @Override
    public String toString() {
        return "ResponseErr{" +
                "responseData=" + responseData +
                '}';
    }
    ResponseData responseData = new ResponseData();
    public void add(String s) {
        responseData.setMessage(s);
    }
    public String show(){
        return responseData.getMessage();
    }
}
