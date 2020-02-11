package com.example.friday_android;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;

public class HttpServer extends NanoHTTPD {




    public HttpServer() throws IOException {
        super(8080);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }



    @Override
    public Response serve(IHTTPSession session) {
        return newFixedLengthResponse("Hello world");
    }
}
