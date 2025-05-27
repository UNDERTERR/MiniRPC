package org.example.exception;

import org.example.enums.RpcErrorMessageEnum;

public class RPCexception extends RuntimeException {
    public RPCexception(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCexception(RpcErrorMessageEnum error) {
        super(error.getMessage());
    }

    public RPCexception(RpcErrorMessageEnum error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
}
