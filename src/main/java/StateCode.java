public class StateCode {
    public final static int QUERY = 0;
    public final static int ADD = 1;
    public final static int REMOVE = 2;

    public final static int UPDATE = 3;

    //Command state
    public final static int SUCCESS = 4;
    public final static int FAIL = 5;

    //Net Error state
    public final static int TIMEOUT = 400;
    public final static int UNKNOWN_HOST = 401;
    public final static int CONNECTION_REFUSED = 402;
    public final static int IO_ERROR = 403;
}
