package org.example.exception;

import org.example.enums.RpcErrorMessageEnum;

public class RpcException extends RuntimeException {
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcErrorMessageEnum error) {
        super(error.getMessage());
    }

    public RpcException(RpcErrorMessageEnum error, String detail) {
        super(error.getMessage() + ": " + detail);
    }
}
