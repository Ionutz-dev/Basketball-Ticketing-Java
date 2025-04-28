package app.network.rpcprotocol;

import java.io.Serializable;

public class Response implements Serializable {
    private ResponseType type;
    private Object data;

    public Response(ResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ResponseType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public static class Builder {
        private ResponseType type;
        private Object data;

        public Builder type(ResponseType type) {
            this.type = type;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Response build() {
            return new Response(type, data);
        }
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
